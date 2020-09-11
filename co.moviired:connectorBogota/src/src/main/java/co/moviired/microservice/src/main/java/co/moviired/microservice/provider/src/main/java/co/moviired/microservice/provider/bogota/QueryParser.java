package co.moviired.microservice.provider.bogota;

import co.moviired.base.domain.StatusCode;
import co.moviired.base.domain.enumeration.ErrorType;
import co.moviired.base.domain.exception.DataException;
import co.moviired.connector.helper.ISOMsgHelper;
import co.moviired.microservice.conf.BankProperties;
import co.moviired.microservice.conf.StatusCodeConfig;
import co.moviired.microservice.constants.ConstantNumbers;
import co.moviired.microservice.domain.iso.GenericWeft;
import co.moviired.microservice.domain.iso.GenericResponse;
import co.moviired.microservice.domain.request.Input;
import co.moviired.microservice.domain.response.Data;
import co.moviired.microservice.domain.response.ErrorDetail;
import co.moviired.microservice.domain.response.Outcome;
import co.moviired.microservice.domain.response.Response;
import co.moviired.microservice.helper.UtilHelper;
import co.moviired.microservice.provider.IParser;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOUtil;
import org.jpos.iso.packager.GenericPackager;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Slf4j
@Service
@AllArgsConstructor
public class QueryParser implements IParser {

    private final StatusCodeConfig statusCodeConfig;
    private final BankProperties bankProducts;

    private static final String SEPARATOR = "\\|";
    private final SimpleDateFormat hourIso = new SimpleDateFormat("HHmmss");
    private final SimpleDateFormat fechaIso = new SimpleDateFormat("MMddHHmmss");

    @Override
    public final ISOMsg parseRequest(Input params, GenericPackager packager) throws DataException, ParseException, IllegalAccessException, ISOException {
        // A.  Validar parámetros de entrada
        validateInput(params);

        // C.1. Consulta MANUAL
        Date actualDate = new Date();
        String[] imei = params.getImei().split(SEPARATOR);
        String[] shortReference = params.getShortReferenceNumber().split(SEPARATOR);
        String field062 = UtilHelper.strPad(shortReference[ConstantNumbers.LENGTH_0], ConstantNumbers.LENGTH_30, "0", ConstantNumbers.LENGTH_0).concat(UtilHelper.strPad("0", ConstantNumbers.LENGTH_120, "0", ConstantNumbers.LENGTH_1));
        String field102 = UtilHelper.strPad("0", ConstantNumbers.LENGTH_18, "0", ConstantNumbers.LENGTH_1);
        String field103 = UtilHelper.strPad(shortReference[ConstantNumbers.LENGTH_2], ConstantNumbers.LENGTH_18, "0", ConstantNumbers.LENGTH_0);
        String field104 = UtilHelper.strPad("1", ConstantNumbers.LENGTH_12, "0", ConstantNumbers.LENGTH_0).concat(UtilHelper.strPad("0", ConstantNumbers.LENGTH_6, "0", ConstantNumbers.LENGTH_1));

        // C.2. Consulta AUTOMÁTICA
        if (shortReference[ConstantNumbers.LENGTH_0].length() > ConstantNumbers.LENGTH_20) {
            field062 = UtilHelper.strPad("0", ConstantNumbers.LENGTH_30, "0", ConstantNumbers.LENGTH_0) + UtilHelper.strPad(shortReference[ConstantNumbers.LENGTH_0], ConstantNumbers.LENGTH_110, " ", ConstantNumbers.LENGTH_1) + UtilHelper.strPad("0", ConstantNumbers.LENGTH_10, "0", ConstantNumbers.LENGTH_0);
            field103 = UtilHelper.strPad("0", ConstantNumbers.LENGTH_18, "0", ConstantNumbers.LENGTH_0);
            field104 = UtilHelper.strPad("2", ConstantNumbers.LENGTH_18, "0", ConstantNumbers.LENGTH_1);
        }

        // C. Armar los mensajes ISO
        try {
            String julianDate = UtilHelper.getJulianDate(Calendar.getInstance());
            String prefixJulian = julianDate.substring(julianDate.length() - ConstantNumbers.LENGTH_3);
            String field13And17 = UtilHelper.getDateCompensation(bankProducts.getClearingDate());
            GenericWeft isoRequest = new GenericWeft();
            isoRequest.setProcessingCode(bankProducts.getQueryProcessCode());
            isoRequest.setAmount(UtilHelper.strPad("0", ConstantNumbers.LENGTH_12, "0", ConstantNumbers.LENGTH_1));
            isoRequest.setTransmisionDateTime(fechaIso.format(actualDate));
            isoRequest.setTraceAuditNumber(imei[ConstantNumbers.LENGTH_3].substring(imei[ConstantNumbers.LENGTH_3].length() - ConstantNumbers.LENGTH_6));
            isoRequest.setCaptureDate(field13And17);
            isoRequest.setLocalHour(hourIso.format(actualDate));
            isoRequest.setLocalDate(field13And17);
            isoRequest.setPointServiceEntryMode(bankProducts.getPostEntryMode());
            isoRequest.setAcquiringCode(bankProducts.getAcquiringInstCode());
            isoRequest.setCurrencyCode(bankProducts.getTrxCurrencyCode());
            isoRequest.setPinData(bankProducts.getPin());
            isoRequest.setAcquiringInformation(UtilHelper.strPad(bankProducts.getTerminalCoding(), ConstantNumbers.LENGTH_12, "0", ConstantNumbers.LENGTH_1));
            isoRequest.setTrackData(bankProducts.getBogotaId() + bankProducts.getMovilRedId() + "D" + bankProducts.getCvv());
            isoRequest.setRetrievalReference(prefixJulian + imei[ConstantNumbers.LENGTH_3].substring(imei[ConstantNumbers.LENGTH_3].length() - ConstantNumbers.LENGTH_9));
            if (shortReference[ConstantNumbers.LENGTH_3].equals("SUBSCRIBER")) {
                isoRequest.setAditionalData(UtilHelper.strPad(imei[ConstantNumbers.LENGTH_10], ConstantNumbers.LENGTH_44, "0", ConstantNumbers.LENGTH_0));
                isoRequest.setAcquiringIdentification(bankProducts.getAcquiringBank() + bankProducts.getTerminalCoding() + ISOUtil.zeropad(imei[ConstantNumbers.LENGTH_10].length() > ConstantNumbers.LENGTH_8 ? imei[ConstantNumbers.LENGTH_10].substring(imei[ConstantNumbers.LENGTH_10].length() - ConstantNumbers.LENGTH_8) : imei[ConstantNumbers.LENGTH_10], ConstantNumbers.LENGTH_8));
            } else if (shortReference[ConstantNumbers.LENGTH_3].equals("CHANNEL")) {
                isoRequest.setAditionalData(UtilHelper.strPad(imei[ConstantNumbers.LENGTH_7], ConstantNumbers.LENGTH_44, "0", ConstantNumbers.LENGTH_0));
                isoRequest.setAcquiringIdentification(bankProducts.getAcquiringBank() + bankProducts.getTerminalCoding() + ISOUtil.zeropad(imei[ConstantNumbers.LENGTH_7].length() > ConstantNumbers.LENGTH_8 ? imei[ConstantNumbers.LENGTH_7].substring(imei[ConstantNumbers.LENGTH_7].length() - ConstantNumbers.LENGTH_8) : imei[ConstantNumbers.LENGTH_7], ConstantNumbers.LENGTH_8));
            }
            String codeHomologated = ((params.getLastName().length() > ConstantNumbers.LENGTH_22) ? params.getLastName().substring(ConstantNumbers.LENGTH_0, ConstantNumbers.LENGTH_22) : UtilHelper.strPad(params.getLastName(), ConstantNumbers.LENGTH_22, "", ConstantNumbers.LENGTH_1)) + bankProducts.getCityCountry();
            isoRequest.setCodeHomologated(codeHomologated);
            isoRequest.setReferenceNumber(field062);
            isoRequest.setOriginAccountNumber(field102);
            isoRequest.setAccountNumber(field103);
            isoRequest.setAccountIdentification(field104);
            isoRequest.setMac2(UtilHelper.strPad("0", ConstantNumbers.LENGTH_16, "0", ConstantNumbers.LENGTH_1));

            return ISOMsgHelper.of("200", isoRequest, packager);

        } catch (Exception e) {
            log.error(e.getMessage());
            throw e;
        }
    }

    @Override
    public final Response parseResponse(@NotNull ISOMsg respOper, Input request) throws DataException, IllegalAccessException, ParseException, InstantiationException {
        // Transformar la respuesta
        ErrorDetail e;
        Data data = null;
        Response response;
        try {
            GenericResponse queryResponse = ISOMsgHelper.resolve(respOper, GenericResponse.class);
            String respCode = queryResponse.getResponseCode();
            if ((respCode == null) || (respCode.trim().isEmpty())) {
                throw new DataException("-3", "La respuesta  recibida del operador está incompleta.");
            }

            // Respuesta existosa
            if (respCode.equals("00")) {
                data = new Data();
                String[] shortReference = request.getShortReferenceNumber().split(SEPARATOR);
                String cost = queryResponse.getAdditionalAmounts().substring(ConstantNumbers.POSITION_0, ConstantNumbers.LENGTH_10);
                Integer valor = Integer.parseInt(cost);
                String referenceNumber = ISOUtil.zeroUnPad(queryResponse.getReferenceNumber().substring(ConstantNumbers.POSITION_0, ConstantNumbers.LENGTH_30));

                data.setBankId(bankProducts.getGestorId());
                data.setValueToPay(String.valueOf(valor));
                data.setAuthorizationCode(queryResponse.getAuthorizationResponse().trim());

                if (shortReference[ConstantNumbers.LENGTH_0].length() < ConstantNumbers.LENGTH_20) { //MANUAL

                    data.setShortReferenceNumber(referenceNumber);
                    data.setDate(ISOUtil.zeroUnPad(queryResponse.getReferenceNumber().substring(ConstantNumbers.LENGTH_90, ConstantNumbers.LENGTH_110)));
                    String numberPILA = ISOUtil.zeroUnPad(queryResponse.getReferenceNumber().substring(ConstantNumbers.LENGTH_0, ConstantNumbers.LENGTH_30));
                    String convInfo = ISOUtil.zeroUnPad(queryResponse.getReferenceNumber().substring(ConstantNumbers.LENGTH_30, ConstantNumbers.LENGTH_60));
                    String expiryDate = ISOUtil.zeroUnPad(queryResponse.getReferenceNumber().substring(ConstantNumbers.LENGTH_90, ConstantNumbers.LENGTH_110));
                    String offsetDate = ISOUtil.zeroUnPad(queryResponse.getReferenceNumber().substring(ConstantNumbers.LENGTH_110, ConstantNumbers.LENGTH_130));
                    String nit = ISOUtil.zeroUnPad(queryResponse.getReferenceNumber().substring(ConstantNumbers.LENGTH_130, ConstantNumbers.LENGTH_150));

                    String paymentPeriod = "";
                    String tipoTx = queryResponse.getAccountNumber().substring(ConstantNumbers.LENGTH_12, ConstantNumbers.LENGTH_13);
                    if ("1".equals(tipoTx)) {
                        log.debug("Procesando periodo liquidado recaudo PILA");
                        paymentPeriod = ISOUtil.zeroUnPad(queryResponse.getReferenceNumber().substring(ConstantNumbers.LENGTH_60, ConstantNumbers.LENGTH_90));
                    }
                    data.setEchoData(numberPILA.concat("|")
                            .concat(convInfo).concat("|")
                            .concat(paymentPeriod).concat("|")
                            .concat(expiryDate).concat("|")
                            .concat(offsetDate).concat("|")
                            .concat(nit).concat("|")
                            .concat(queryResponse.getAdditionalAmounts()).concat("@@")
                            .concat(queryResponse.getReferenceNumber()).concat("@@")
                            .concat(queryResponse.getAccountNumber()).concat("@@")
                            .concat(queryResponse.getAccountIdentification().concat("###"))
                            .concat(request.getLastName()));

                } else { //AUTOMATIC

                    String pos2 = ISOUtil.zeroUnPad(queryResponse.getReferenceNumber().substring(ConstantNumbers.LENGTH_130));
                    data.setEchoData(referenceNumber.concat("|")
                            .concat("NO DISPONIBLE").concat("|")
                            .concat(pos2).concat("|")
                            .concat(queryResponse.getAmount()).concat("|")
                            .concat(queryResponse.getAdditionalAmounts()).concat("@@")
                            .concat(queryResponse.getReferenceNumber()).concat("@@")
                            .concat(queryResponse.getAccountNumber()).concat("@@")
                            .concat(queryResponse.getAccountIdentification())
                            .concat(queryResponse.getAccountIdentification().concat("###"))
                            .concat(request.getLastName()));

                    data.setDate("NO DISPONIBLE");
                    data.setBillReferenceNumber(referenceNumber);

                }

                StatusCode statusCode = statusCodeConfig.of(queryResponse.getResponseCode(), "Transaccion Aprobada");
                e = new ErrorDetail(statusCode.getMessage(), ConstantNumbers.LENGTH_0, respCode);

            } else {
                StatusCode statusCode = statusCodeConfig.of(queryResponse.getResponseCode(), "Transaccion Rechazada");
                e = new ErrorDetail(statusCode.getMessage(), ErrorType.PROCESSING.ordinal(), respCode);

            }
            Outcome result = new Outcome(HttpStatus.OK, e);
            response = new Response(result, data);

        } catch (Exception ex) {
            log.error(ex.getMessage());
            throw ex;
        }
        return response;
    }

    private void validateInput(Input params) throws DataException {
        if (params.getShortReferenceNumber() == null) {
            throw new DataException("-2", "El shortReferenceNumber es un parámetro obligatorio");
        }
    }
}

