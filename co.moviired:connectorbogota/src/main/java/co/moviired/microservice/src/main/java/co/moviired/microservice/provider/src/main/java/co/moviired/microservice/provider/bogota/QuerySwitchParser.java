package co.moviired.microservice.provider.bogota;

/*
 * Copyright @2019. Movii, SAS. Todos los derechos reservados.
 *
 * @author Cindy Bejarano, Oscar Lopez
 * @since 1.0.0
 */

import co.moviired.base.domain.StatusCode;
import co.moviired.base.domain.enumeration.ErrorType;
import co.moviired.base.domain.exception.DataException;
import co.moviired.base.domain.exception.ProcessingException;
import co.moviired.connector.helper.ISOMsgHelper;
import co.moviired.microservice.conf.BankProductsProperties;
import co.moviired.microservice.conf.StatusCodeConfig;
import co.moviired.microservice.constants.ConstantSwitch;
import co.moviired.microservice.domain.iso.QueryRequest;
import co.moviired.microservice.domain.iso.QueryResponse;
import co.moviired.microservice.domain.request.Input;
import co.moviired.microservice.domain.response.Data;
import co.moviired.microservice.domain.response.ErrorDetail;
import co.moviired.microservice.domain.response.Outcome;
import co.moviired.microservice.domain.response.Response;
import co.moviired.microservice.helper.UtilHelper;
import co.moviired.microservice.provider.IParser;
import lombok.extern.slf4j.Slf4j;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOUtil;
import org.jpos.iso.packager.GenericPackager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Slf4j
@Service
public class QuerySwitchParser implements IParser {

    @Autowired
    private StatusCodeConfig statusCodeConfig;
    @Autowired
    private BankProductsProperties bankProducts;

    private static final String separator = "\\|";
    private final SimpleDateFormat hourIso = new SimpleDateFormat("HHmmss");
    private final SimpleDateFormat fechaIso = new SimpleDateFormat("MMddHHmmss");

    @Override
    public final ISOMsg parseRequest(Input params, GenericPackager packager) throws DataException {
        // A.  Validar parámetros de entrada
        validateInput(params);

        // C.1. Consulta MANUAL
        Date actualDate = new Date();
        String[] imei = params.getImei().split(separator);
        String[] shortReference = params.getShortReferenceNumber().split(separator);
        String field062 = UtilHelper.strPad(shortReference[ConstantSwitch.LENGTH_0], ConstantSwitch.LENGTH_30, "0", ConstantSwitch.LENGTH_0).concat(UtilHelper.strPad("0", ConstantSwitch.LENGTH_120, "0", ConstantSwitch.LENGTH_1));
        String field102 = UtilHelper.strPad("0", ConstantSwitch.LENGTH_18, "0", ConstantSwitch.LENGTH_1);
        String field103 = UtilHelper.strPad(shortReference[ConstantSwitch.LENGTH_2], ConstantSwitch.LENGTH_18, "0", ConstantSwitch.LENGTH_0);
        String field104 = UtilHelper.strPad("1", ConstantSwitch.LENGTH_12, "0", ConstantSwitch.LENGTH_0).concat(UtilHelper.strPad("0", ConstantSwitch.LENGTH_6, "0", ConstantSwitch.LENGTH_1));

        // C.2. Consulta AUTOMÁTICA
        if (shortReference[ConstantSwitch.LENGTH_0].length() > ConstantSwitch.LENGTH_20) {
            field062 = UtilHelper.strPad("0", ConstantSwitch.LENGTH_30, "0", ConstantSwitch.LENGTH_0) + UtilHelper.strPad(shortReference[ConstantSwitch.LENGTH_0], ConstantSwitch.LENGTH_110, " ", ConstantSwitch.LENGTH_1) + UtilHelper.strPad("0", ConstantSwitch.LENGTH_10, "0", ConstantSwitch.LENGTH_0);
            field103 = UtilHelper.strPad("0", ConstantSwitch.LENGTH_18, "0", ConstantSwitch.LENGTH_0);
            field104 = UtilHelper.strPad("2", ConstantSwitch.LENGTH_18, "0", ConstantSwitch.LENGTH_1);
        }

        // C. Armar los mensajes ISO
        try {
            String julianDate = UtilHelper.getJulianDate(Calendar.getInstance());
            String prefixJulian = julianDate.substring(julianDate.length() - ConstantSwitch.LENGTH_3);
            String field13And17 = UtilHelper.getDateCompensation(bankProducts.getClearingDate());
            QueryRequest isoRequest = new QueryRequest();
            isoRequest.setProcessingCode(bankProducts.getProcessCodeQuery());
            isoRequest.setAmount(UtilHelper.strPad("0", ConstantSwitch.LENGTH_12, "0", ConstantSwitch.LENGTH_1));
            isoRequest.setTransmisionDateTime(fechaIso.format(actualDate));
            isoRequest.setTraceAuditNumber(imei[ConstantSwitch.LENGTH_3].substring(imei[ConstantSwitch.LENGTH_3].length() - ConstantSwitch.LENGTH_6));
            isoRequest.setCaptureDate(field13And17);
            isoRequest.setLocalHour(hourIso.format(actualDate));
            isoRequest.setLocalDate(field13And17);
            isoRequest.setPointServiceEntryMode(bankProducts.getPostEntryMode());
            isoRequest.setAcquiringCode(bankProducts.getAcquiringInstCode());
            isoRequest.setCurrencyCode(bankProducts.getTrxCurrencyCode());
            isoRequest.setPinData(bankProducts.getPin());
            isoRequest.setAcquiringInformation(UtilHelper.strPad(bankProducts.getTerminalCoding(), ConstantSwitch.LENGTH_12, "0", ConstantSwitch.LENGTH_1));
            isoRequest.setTrackData(bankProducts.getBogotaId() + bankProducts.getMovilRedId() + "D" + bankProducts.getCvv());
            isoRequest.setRetrievalReference(prefixJulian + imei[ConstantSwitch.LENGTH_3].substring(imei[ConstantSwitch.LENGTH_3].length() - ConstantSwitch.LENGTH_9));
            if (shortReference[ConstantSwitch.LENGTH_3].equals("SUBSCRIBER")) {
                isoRequest.setAditionalData(UtilHelper.strPad(bankProducts.getTercIdPayBill(), ConstantSwitch.LENGTH_44, "0", ConstantSwitch.LENGTH_0));
                isoRequest.setAcquiringIdentification(bankProducts.getAcquiringBank() + bankProducts.getTerminalCoding() + ISOUtil.zeropad(bankProducts.getTercIdPayBill().length() > ConstantSwitch.LENGTH_8 ? bankProducts.getTercIdPayBill().substring(bankProducts.getTercIdPayBill().length() - ConstantSwitch.LENGTH_8) : bankProducts.getTercIdPayBill(), ConstantSwitch.LENGTH_8));
            } else if (shortReference[ConstantSwitch.LENGTH_3].equals("CHANNEL")) {
                isoRequest.setAditionalData(UtilHelper.strPad(imei[ConstantSwitch.LENGTH_7], ConstantSwitch.LENGTH_44, "0", ConstantSwitch.LENGTH_0));
                isoRequest.setAcquiringIdentification(bankProducts.getAcquiringBank() + bankProducts.getTerminalCoding() + ISOUtil.zeropad(imei[ConstantSwitch.LENGTH_7].length() > ConstantSwitch.LENGTH_8 ? imei[ConstantSwitch.LENGTH_7].substring(imei[ConstantSwitch.LENGTH_7].length() - ConstantSwitch.LENGTH_8) : imei[ConstantSwitch.LENGTH_7], ConstantSwitch.LENGTH_8));
            }
            String codeHomologated = ((params.getLastName().length() > ConstantSwitch.LENGTH_22) ? params.getLastName().substring(ConstantSwitch.LENGTH_0, ConstantSwitch.LENGTH_22) : UtilHelper.strPad(params.getLastName(), ConstantSwitch.LENGTH_22, "", ConstantSwitch.LENGTH_1)) + bankProducts.getCityCountry();
            isoRequest.setCodeHomologated(codeHomologated);
            isoRequest.setReferenceNumber(field062);
            isoRequest.setOriginAccountNumber(field102);
            isoRequest.setAccountNumber(field103);
            isoRequest.setAccountIdentification(field104);
            isoRequest.setMac2(UtilHelper.strPad("0", ConstantSwitch.LENGTH_16, "0", ConstantSwitch.LENGTH_1));

            return ISOMsgHelper.of("200", isoRequest, packager);

        } catch (ProcessingException | Exception e) {
            throw new DataException("300", e);
        }
    }

    @Override
    public final Response parseResponse(@NotNull ISOMsg respOper, Input request) throws DataException {
        // Transformar la respuesta
        ErrorDetail e;
        Data data = null;
        Response response;
        try {
            QueryResponse queryResponse = ISOMsgHelper.resolve(respOper, QueryResponse.class);
            String respCode = queryResponse.getResponseCode();
            if ((respCode == null) || (respCode.trim().isEmpty())) {
                throw new DataException("-3", "La respuesta  recibida del operador está incompleta.");
            }

            // Respuesta existosa
            if (respCode.equals("00")) {
                data = new Data();
                String[] shortReference = request.getShortReferenceNumber().split(separator);
                String cost = queryResponse.getAdditionalAmounts().substring(ConstantSwitch.POSITION_0, ConstantSwitch.LENGTH_10);
                Integer valor = Integer.parseInt(cost);
                String referenceNumber = ISOUtil.zeroUnPad(queryResponse.getAdditionalDataReferenceNumber().substring(ConstantSwitch.POSITION_0, ConstantSwitch.LENGTH_30));

                data.setBankId(bankProducts.getGestorId());
                data.setValueToPay(String.valueOf(valor));
                data.setAuthorizationCode(queryResponse.getAuthorizationResponse().trim());

                if (shortReference[ConstantSwitch.LENGTH_0].length() < ConstantSwitch.LENGTH_20) { //MANUAL

                    data.setShortReferenceNumber(referenceNumber);
                    data.setDate(ISOUtil.zeroUnPad(queryResponse.getAdditionalDataReferenceNumber().substring(ConstantSwitch.LENGTH_90, ConstantSwitch.LENGTH_110)));
                    String numberPILA = ISOUtil.zeroUnPad(queryResponse.getAdditionalDataReferenceNumber().substring(ConstantSwitch.LENGTH_0, ConstantSwitch.LENGTH_30));
                    String convInfo = ISOUtil.zeroUnPad(queryResponse.getAdditionalDataReferenceNumber().substring(ConstantSwitch.LENGTH_30, ConstantSwitch.LENGTH_60));
                    String expiryDate = ISOUtil.zeroUnPad(queryResponse.getAdditionalDataReferenceNumber().substring(ConstantSwitch.LENGTH_90, ConstantSwitch.LENGTH_110));
                    String offsetDate = ISOUtil.zeroUnPad(queryResponse.getAdditionalDataReferenceNumber().substring(ConstantSwitch.LENGTH_110, ConstantSwitch.LENGTH_130));
                    String nit = ISOUtil.zeroUnPad(queryResponse.getAdditionalDataReferenceNumber().substring(ConstantSwitch.LENGTH_130, ConstantSwitch.LENGTH_150));

                    String paymentPeriod = "";
                    String tipoTx = queryResponse.getAccountNumber().substring(ConstantSwitch.LENGTH_12, ConstantSwitch.LENGTH_13);
                    if ("1".equals(tipoTx)) {
                        log.debug("Procesando periodo liquidado recaudo PILA");
                        paymentPeriod = ISOUtil.zeroUnPad(queryResponse.getAdditionalDataReferenceNumber().substring(ConstantSwitch.LENGTH_60, ConstantSwitch.LENGTH_90));
                    }
                    data.setEchoData(numberPILA.concat("|")
                            .concat(convInfo).concat("|")
                            .concat(paymentPeriod).concat("|")
                            .concat(expiryDate).concat("|")
                            .concat(offsetDate).concat("|")
                            .concat(nit).concat("|")
                            .concat(queryResponse.getAdditionalAmounts()).concat("@@")
                            .concat(queryResponse.getAdditionalDataReferenceNumber()).concat("@@")
                            .concat(queryResponse.getAccountNumber()).concat("@@")
                            .concat(queryResponse.getAccountIdentification().concat("###"))
                            .concat(request.getLastName()));

                } else { //AUTOMATIC

                    String pos2 = ISOUtil.zeroUnPad(queryResponse.getAdditionalDataReferenceNumber().substring(ConstantSwitch.LENGTH_130));
                    data.setEchoData(referenceNumber.concat("|")
                            .concat("NO DISPONIBLE").concat("|")
                            .concat(pos2).concat("|")
                            .concat(queryResponse.getAmount()).concat("|")
                            .concat(queryResponse.getAdditionalAmounts()).concat("@@")
                            .concat(queryResponse.getAdditionalDataReferenceNumber()).concat("@@")
                            .concat(queryResponse.getAccountNumber()).concat("@@")
                            .concat(queryResponse.getAccountIdentification())
                            .concat(queryResponse.getAccountIdentification().concat("###"))
                            .concat(request.getLastName()));

                    data.setDate("NO DISPONIBLE");
                    data.setBillReferenceNumber(referenceNumber);

                }

                StatusCode statusCode = statusCodeConfig.of(queryResponse.getResponseCode(), "Transaccion Aprobada");
                e = new ErrorDetail(ConstantSwitch.LENGTH_0, respCode, statusCode.getMessage());

            } else {
                StatusCode statusCode = statusCodeConfig.of(queryResponse.getResponseCode(), "Transaccion Rechazada");
                e = new ErrorDetail(ErrorType.PROCESSING.ordinal(), respCode, statusCode.getMessage());

            }
            Outcome result = new Outcome(HttpStatus.OK, e);
            response = new Response(result, data);

        } catch (Exception ex) {
            throw new DataException("-2", ex.getMessage());
        }
        return response;
    }

    private void validateInput(Input params) throws DataException {
        if (params.getShortReferenceNumber() == null) {
            throw new DataException("-2", "El shortReferenceNumber es un parámetro obligatorio");
        }
    }
}

