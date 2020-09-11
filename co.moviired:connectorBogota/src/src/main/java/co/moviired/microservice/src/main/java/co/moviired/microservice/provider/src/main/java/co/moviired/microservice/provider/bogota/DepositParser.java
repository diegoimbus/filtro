package co.moviired.microservice.provider.bogota;

import co.moviired.base.domain.StatusCode;
import co.moviired.base.domain.enumeration.ErrorType;
import co.moviired.base.domain.exception.DataException;
import co.moviired.connector.helper.ISOMsgHelper;
import co.moviired.microservice.conf.BankProperties;
import co.moviired.microservice.conf.StatusCodeConfig;
import co.moviired.microservice.constants.ConstantNumbers;
import co.moviired.microservice.domain.iso.DepositRequest;
import co.moviired.microservice.domain.iso.GenericResponse;
import co.moviired.microservice.domain.request.Input;
import co.moviired.microservice.domain.response.Data;
import co.moviired.microservice.domain.response.ErrorDetail;
import co.moviired.microservice.domain.response.Outcome;
import co.moviired.microservice.domain.response.Response;
import co.moviired.microservice.helper.UtilHelper;
import co.moviired.microservice.provider.IParser;
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
public class DepositParser implements IParser {

    private final StatusCodeConfig statusCodeConfig;
    private final BankProperties bankProducts;

    private String sequenceNumber;
    private static final String SEPARATOR = "\\|";
    private final SimpleDateFormat timeIso = new SimpleDateFormat("MMdd");
    private final SimpleDateFormat dateIso = new SimpleDateFormat("HHmmss");
    private final SimpleDateFormat dateTimeIso = new SimpleDateFormat("MMddHHmmss");

    public DepositParser(StatusCodeConfig statusCodeConfig, BankProperties bankProducts) {
        this.statusCodeConfig = statusCodeConfig;
        this.bankProducts = bankProducts;
    }

    @Override
    public final ISOMsg parseRequest(Input params, GenericPackager packager) throws DataException {

        // Obtener datos y construir request
        String lastName = "";
        String[] issuerName = new String[ConstantNumbers.LENGTH_0];
        String[] shortReference = params.getShortReferenceNumber().split(SEPARATOR);
        String[] echoData = params.getEchoData().split(SEPARATOR);
        String[] imei = params.getImei().split(SEPARATOR);
        String agentCode = imei[ConstantNumbers.POSITION_7];
        String julianDate = UtilHelper.getJulianDate(Calendar.getInstance());
        String prefixJulian = julianDate.substring(julianDate.length() - ConstantNumbers.LENGTH_3);
        if (shortReference[ConstantNumbers.LENGTH_1].equals("MANUAL")) {
            issuerName = echoData[ConstantNumbers.LENGTH_6].split("@@")[ConstantNumbers.LENGTH_3].split("###");
        } else if (shortReference[ConstantNumbers.LENGTH_1].equals("AUTOMATIC")) {
            issuerName = echoData[ConstantNumbers.LENGTH_4].split("@@")[ConstantNumbers.LENGTH_3].split("###");
        }
        if (issuerName.length == ConstantNumbers.LENGTH_2) {
            lastName = issuerName[ConstantNumbers.LENGTH_1];
        }
        if (agentCode.length() > ConstantNumbers.LENGTH_8) {
            agentCode = agentCode.substring(ConstantNumbers.LENGTH_2);
        }
        sequenceNumber = prefixJulian + imei[ConstantNumbers.LENGTH_3].substring(imei[ConstantNumbers.LENGTH_3].length() - ConstantNumbers.LENGTH_9);

        try {
            DepositRequest isoRequest = new DepositRequest();
            isoRequest.setProcessingCode(bankProducts.getPayBillProcessCode());
            isoRequest.setAmount(UtilHelper.strPad(params.getAmount() + "00", ConstantNumbers.LENGTH_12, "0", ConstantNumbers.LENGTH_0));
            isoRequest.setTransmisionDateTime(dateTimeIso.format(new Date()));
            isoRequest.setTraceAuditNumber(imei[ConstantNumbers.LENGTH_3].substring(imei[ConstantNumbers.LENGTH_3].length() - ConstantNumbers.LENGTH_6));
            isoRequest.setLocalHour(dateIso.format(new Date()));
            isoRequest.setLocalDate(timeIso.format(new Date()));
            isoRequest.setCaptureDate(timeIso.format(new Date()));
            isoRequest.setPointServiceEntryMode(bankProducts.getPostEntryMode());
            isoRequest.setAcquiringCode(bankProducts.getAcquiringInstCode());
            isoRequest.setTrackData(bankProducts.getBogotaId() + bankProducts.getMovilRedId() + "D" + bankProducts.getCvv());
            isoRequest.setRetrievalReference(sequenceNumber);
            String codeHomologated = ((lastName.length() > ConstantNumbers.LENGTH_22) ? lastName.substring(ConstantNumbers.LENGTH_0, ConstantNumbers.LENGTH_22) : UtilHelper.strPad(lastName, ConstantNumbers.LENGTH_22, "", ConstantNumbers.LENGTH_1)) + bankProducts.getCityCountry();
            isoRequest.setCodeHomologated(codeHomologated);
            isoRequest.setAditionalData(UtilHelper.strPad(agentCode, ConstantNumbers.LENGTH_44, "0", ConstantNumbers.LENGTH_0));
            isoRequest.setCurrencyCode(bankProducts.getTrxCurrencyCode());
            isoRequest.setPinData(bankProducts.getPin());
            isoRequest.setAcquiringInformation(UtilHelper.strPad(bankProducts.getTerminalCoding(), ConstantNumbers.LENGTH_12, "0", ConstantNumbers.LENGTH_1));
            isoRequest.setOriginAccountNumber(UtilHelper.strPad(bankProducts.getMovilRedAccount(), ConstantNumbers.LENGTH_18, "0", ConstantNumbers.LENGTH_0));
            isoRequest.setAccountNumber(UtilHelper.strPad(shortReference[ConstantNumbers.LENGTH_2], ConstantNumbers.LENGTH_18, "0", ConstantNumbers.LENGTH_0));
            isoRequest.setMac2(UtilHelper.strPad("", ConstantNumbers.LENGTH_16, "0", ConstantNumbers.LENGTH_0));

            //para el primer despliegue se deja por defecto BANCO BOGOTA
            if (shortReference[ConstantNumbers.LENGTH_3].equals("SUBSCRIBER")) {
                isoRequest.setAcquiringIdentification(bankProducts.getAcquiringBank() + bankProducts.getTerminalCoding() + ISOUtil.zeropad(imei[ConstantNumbers.LENGTH_10].length() > ConstantNumbers.LENGTH_8 ? imei[ConstantNumbers.LENGTH_10].substring(imei[ConstantNumbers.LENGTH_10].length() - ConstantNumbers.LENGTH_8) : agentCode, ConstantNumbers.LENGTH_8));
            } else if (shortReference[ConstantNumbers.LENGTH_3].equals("CHANNEL")) {
                isoRequest.setAcquiringIdentification(bankProducts.getAcquiringBank() + bankProducts.getTerminalCoding() + ISOUtil.zeropad(agentCode.length() > ConstantNumbers.LENGTH_8 ? agentCode.substring(agentCode.length() - ConstantNumbers.LENGTH_8) : agentCode, ConstantNumbers.LENGTH_8));
            }

            if (shortReference[ConstantNumbers.LENGTH_1].equals("MANUAL")) {
                String[] echoManual = echoData[ConstantNumbers.LENGTH_6].split("@@");
                isoRequest.setAdditionalAmounts(UtilHelper.strPad(echoManual[ConstantNumbers.LENGTH_0], ConstantNumbers.LENGTH_36, "0", ConstantNumbers.LENGTH_1));
                isoRequest.setReferenceNumber(echoManual[ConstantNumbers.LENGTH_1]);
                isoRequest.setAccountIdentification(UtilHelper.strPad(echoManual[ConstantNumbers.POSITION_3], ConstantNumbers.LENGTH_18, "0", ConstantNumbers.LENGTH_0));
            } else if (shortReference[ConstantNumbers.LENGTH_1].equals("AUTOMATIC")) {
                String[] echoAutomatic = echoData[ConstantNumbers.LENGTH_4].split("@@");
                isoRequest.setAdditionalAmounts(UtilHelper.strPad(echoAutomatic[ConstantNumbers.LENGTH_0], ConstantNumbers.LENGTH_36, "0", ConstantNumbers.LENGTH_1));
                isoRequest.setReferenceNumber(echoAutomatic[ConstantNumbers.LENGTH_1]);
                isoRequest.setAccountIdentification(UtilHelper.strPad(echoAutomatic[ConstantNumbers.POSITION_3], ConstantNumbers.LENGTH_18, "0", ConstantNumbers.LENGTH_0));
            }

            return ISOMsgHelper.of("200", isoRequest, packager);
        } catch (ISOException | IllegalAccessException e) {
            throw new DataException("300", e);
        }
    }

    @Override
    public final Response parseResponse(@NotNull ISOMsg respOper, Input params) throws DataException, IllegalAccessException, ParseException, InstantiationException {
        // Transformar la respuesta
        ErrorDetail e;
        Response response;
        Data data = new Data();
        try {
            GenericResponse depositResponse = ISOMsgHelper.resolve(respOper, GenericResponse.class);
            String respCode = depositResponse.getResponseCode();
            if ((respCode == null) || (respCode.trim().isEmpty())) {
                throw new DataException("-3", "La respuesta  recibida del operador está incompleta.");
            }

            String bankId = bankProducts.getGestorId();
            String[] refNumber = params.getShortReferenceNumber().split(SEPARATOR);
            String referenceNumber = refNumber[ConstantNumbers.POSITION_0];

            // Respuesta existosa
            if ("00".equals(respCode)) {

                String cost = depositResponse.getAmount().substring(ConstantNumbers.POSITION_0, ConstantNumbers.LENGTH_10);
                Integer valor = Integer.parseInt(cost);
                data.setValueToPay(String.valueOf(valor));
                data.setAuthorizationCode(depositResponse.getAuthorizationResponse()+ "|" + sequenceNumber);
                data.setTransactionId(referenceNumber);
                data.setBankId(bankId);
                data.setShortReferenceNumber(referenceNumber);

                StatusCode statusCode = statusCodeConfig.of(depositResponse.getResponseCode(), "Transaccion Exitosa");
                e = new ErrorDetail(statusCode.getMessage(), ConstantNumbers.LENGTH_0, respCode);

            } else {
                data.setTransactionId(referenceNumber);
                data.setBankId(bankId);
                data.setShortReferenceNumber(referenceNumber);

                StatusCode statusCode = statusCodeConfig.of(depositResponse.getResponseCode(), "Transaccion rechazada");
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

}

