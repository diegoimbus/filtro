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
import co.moviired.connector.helper.ISOMsgHelper;
import co.moviired.microservice.conf.BankProductsProperties;
import co.moviired.microservice.conf.StatusCodeConfig;
import co.moviired.microservice.constants.ConstantSwitch;
import co.moviired.microservice.domain.iso.DepositRequest;
import co.moviired.microservice.domain.iso.DepositResponse;
import co.moviired.microservice.domain.jpa.entity.Conciliacion;
import co.moviired.microservice.domain.jpa.repository.IConciliacionRepository;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Slf4j
@Service
public class DepositSwitchParser implements IParser {

    @Autowired
    private StatusCodeConfig statusCodeConfig;
    @Autowired
    private BankProductsProperties bankProducts;
    @Autowired
    private IConciliacionRepository iConciliacionRepository;

    private String sequenceNumber;
    private static final String separator = "\\|";
    private static final String FORMATTED_LOG_2 = "{} {}";
    private final SimpleDateFormat timeIso = new SimpleDateFormat("MMdd");
    private final SimpleDateFormat dateIso = new SimpleDateFormat("HHmmss");
    private final SimpleDateFormat dateTimeIso = new SimpleDateFormat("MMddHHmmss");

    @Override
    public final ISOMsg parseRequest(Input params, GenericPackager packager) throws DataException {
        // A. Validar datos obligatorios
        validateInput(params);

        //B. Obtener datos y construir request
        String lastName = "";
        String[] issuerName = new String[ConstantSwitch.LENGTH_0];
        String[] shortReference = params.getShortReferenceNumber().split(separator);
        String[] echoData = params.getEchoData().split(separator);
        String[] imei = params.getImei().split(separator);
        String agentCode = imei[ConstantSwitch.POSITION_7];
        String julianDate = UtilHelper.getJulianDate(Calendar.getInstance());
        String prefixJulian = julianDate.substring(julianDate.length() - ConstantSwitch.LENGTH_3);
        if (shortReference[ConstantSwitch.LENGTH_1].equals("MANUAL")) {
            issuerName = echoData[ConstantSwitch.LENGTH_6].split("@@")[ConstantSwitch.LENGTH_3].split("###");
        } else if (shortReference[ConstantSwitch.LENGTH_1].equals("AUTOMATIC")) {
            issuerName = echoData[ConstantSwitch.LENGTH_4].split("@@")[ConstantSwitch.LENGTH_3].split("###");
        }
        if (issuerName.length == ConstantSwitch.LENGTH_2) {
            lastName = issuerName[ConstantSwitch.LENGTH_1];
        }
        if (agentCode.length() > ConstantSwitch.LENGTH_8) {
            agentCode = agentCode.substring(ConstantSwitch.LENGTH_2);
        }
        sequenceNumber = prefixJulian + imei[ConstantSwitch.LENGTH_3].substring(imei[ConstantSwitch.LENGTH_3].length() - ConstantSwitch.LENGTH_9);

        try {
            DepositRequest isoRequest = new DepositRequest();
            isoRequest.setProcessingCode(bankProducts.getProcessCodeDeposit());
            isoRequest.setAmount(UtilHelper.strPad(params.getAmount() + "00", ConstantSwitch.LENGTH_12, "0", ConstantSwitch.LENGTH_0));
            isoRequest.setTransmisionDateTime(dateTimeIso.format(new Date()));
            isoRequest.setTraceAuditNumber(imei[ConstantSwitch.LENGTH_3].substring(imei[ConstantSwitch.LENGTH_3].length() - ConstantSwitch.LENGTH_6));
            isoRequest.setLocalHour(dateIso.format(new Date()));
            isoRequest.setLocalDate(timeIso.format(new Date()));
            isoRequest.setCaptureDate(timeIso.format(new Date()));
            isoRequest.setPointServiceEntryMode(bankProducts.getPostEntryMode());
            isoRequest.setAcquiringCode(bankProducts.getAcquiringInstCode());
            isoRequest.setTrackData(bankProducts.getBogotaId() + bankProducts.getMovilRedId() + "D" + bankProducts.getCvv());
            isoRequest.setRetrievalReference(sequenceNumber);
            String codeHomologated = ((lastName.length() > ConstantSwitch.LENGTH_22) ? lastName.substring(ConstantSwitch.LENGTH_0, ConstantSwitch.LENGTH_22) : UtilHelper.strPad(lastName, ConstantSwitch.LENGTH_22, "", ConstantSwitch.LENGTH_1)) + bankProducts.getCityCountry();
            isoRequest.setCodeHomologated(codeHomologated);
            isoRequest.setAditionalData(UtilHelper.strPad(agentCode, ConstantSwitch.LENGTH_44, "0", ConstantSwitch.LENGTH_0));
            isoRequest.setCurrencyCode(bankProducts.getTrxCurrencyCode());
            isoRequest.setPinData(bankProducts.getPin());
            isoRequest.setAcquiringInformation(UtilHelper.strPad(bankProducts.getTerminalCoding(), ConstantSwitch.LENGTH_12, "0", ConstantSwitch.LENGTH_1));
            isoRequest.setOriginAccountNumber(UtilHelper.strPad(bankProducts.getMovilRedAccount(), ConstantSwitch.LENGTH_18, "0", ConstantSwitch.LENGTH_0));
            isoRequest.setAccountNumber(UtilHelper.strPad(shortReference[ConstantSwitch.LENGTH_2], ConstantSwitch.LENGTH_18, "0", ConstantSwitch.LENGTH_0));
            isoRequest.setMac2(UtilHelper.strPad("", ConstantSwitch.LENGTH_16, "0", ConstantSwitch.LENGTH_0));

            //para el primer despliegue se deja por defecto BANCO BOGOTA
            if (shortReference[ConstantSwitch.LENGTH_3].equals("SUBSCRIBER")) {
                isoRequest.setAcquiringIdentification(bankProducts.getAcquiringBank() + bankProducts.getTerminalCoding() + ISOUtil.zeropad(bankProducts.getTercIdPayBill().length() > ConstantSwitch.LENGTH_8 ? bankProducts.getTercIdPayBill().substring(bankProducts.getTercIdPayBill().length() - ConstantSwitch.LENGTH_8) : agentCode, ConstantSwitch.LENGTH_8));
            } else if (shortReference[ConstantSwitch.LENGTH_3].equals("CHANNEL")) {
                isoRequest.setAcquiringIdentification(bankProducts.getAcquiringBank() + bankProducts.getTerminalCoding() + ISOUtil.zeropad(agentCode.length() > ConstantSwitch.LENGTH_8 ? agentCode.substring(agentCode.length() - ConstantSwitch.LENGTH_8) : agentCode, ConstantSwitch.LENGTH_8));
            }

            if (shortReference[ConstantSwitch.LENGTH_1].equals("MANUAL")) {
                String[] echoManual = echoData[ConstantSwitch.LENGTH_6].split("@@");
                isoRequest.setAdditionalAmounts(UtilHelper.strPad(echoManual[ConstantSwitch.LENGTH_0], ConstantSwitch.LENGTH_36, "0", ConstantSwitch.LENGTH_1));
                isoRequest.setReferenceNumber(echoManual[ConstantSwitch.LENGTH_1]);
                isoRequest.setAccountIdentification(UtilHelper.strPad(echoManual[ConstantSwitch.POSITION_3], ConstantSwitch.LENGTH_18, "0", ConstantSwitch.LENGTH_0));
            } else if (shortReference[ConstantSwitch.LENGTH_1].equals("AUTOMATIC")) {
                String[] echoAutomatic = echoData[ConstantSwitch.LENGTH_4].split("@@");
                isoRequest.setAdditionalAmounts(UtilHelper.strPad(echoAutomatic[ConstantSwitch.LENGTH_0], ConstantSwitch.LENGTH_36, "0", ConstantSwitch.LENGTH_1));
                isoRequest.setReferenceNumber(echoAutomatic[ConstantSwitch.LENGTH_1]);
                isoRequest.setAccountIdentification(UtilHelper.strPad(echoAutomatic[ConstantSwitch.POSITION_3], ConstantSwitch.LENGTH_18, "0", ConstantSwitch.LENGTH_0));
            }

            return ISOMsgHelper.of("200", isoRequest, packager);
        } catch (ISOException | IllegalAccessException e) {
            throw new DataException("300", e);
        }
    }

    @Override
    public final Response parseResponse(@NotNull ISOMsg respOper, Input params) throws DataException {
        // Transformar la respuesta
        ErrorDetail e;
        Response response;
        Data data = new Data();
        try {
            DepositResponse depositResponse = ISOMsgHelper.resolve(respOper, DepositResponse.class);
            String respCode = depositResponse.getResponseCode();
            if ((respCode == null) || (respCode.trim().isEmpty())) {
                throw new DataException("-3", "La respuesta  recibida del operador está incompleta.");
            }

            String bankId = bankProducts.getGestorId();
            String[] refNumber = params.getShortReferenceNumber().split(separator);
            String referenceNumber = refNumber[ConstantSwitch.POSITION_0];

            // Respuesta existosa
            if ("00".equals(respCode)) {

                String cost = depositResponse.getAmount().substring(ConstantSwitch.POSITION_0, ConstantSwitch.LENGTH_10);
                Integer valor = Integer.parseInt(cost);
                data.setValueToPay(String.valueOf(valor));
                data.setAuthorizationCode(depositResponse.getAuthorizationResponse());
                data.setTransactionId(referenceNumber);
                data.setBankId(bankId);
                data.setShortReferenceNumber(referenceNumber);

                log.info("************ RESPUESTA CONNECTOR BOGOTA PAYBILL ************");
                log.info(FORMATTED_LOG_2, "valuetoPay ", data.getValueToPay());
                log.info(FORMATTED_LOG_2, "bankId ", bankId);
                log.info(FORMATTED_LOG_2, "authorizationCode ", data.getAuthorizationCode());
                log.info(FORMATTED_LOG_2, "referenceNumber ", referenceNumber);

                Conciliacion c = new Conciliacion();
                c.setAuthorizationNumber(data.getAuthorizationCode());
                c.setTransferId(params.getTransferId());
                c.setNuraCode(refNumber[ConstantSwitch.LENGTH_2]);
                c.setReferenceNumber(referenceNumber);
                c.setSequenceNumber(sequenceNumber);
                c.setValuePay(data.getValueToPay());
                iConciliacionRepository.save(c);

                StatusCode statusCode = statusCodeConfig.of(depositResponse.getResponseCode(), "Transaccion Exitosa");
                e = new ErrorDetail(ConstantSwitch.LENGTH_0, respCode, statusCode.getMessage());

            } else {
                data.setTransactionId(referenceNumber);
                data.setBankId(bankId);
                data.setShortReferenceNumber(referenceNumber);

                StatusCode statusCode = statusCodeConfig.of(depositResponse.getResponseCode(), "Transaccion rechazada");
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
        if (params.getAmount() == null) {
            throw new DataException("-2", "El amount es un parámetro obligatorio");
        }
        if (params.getEchoData() == null) {
            throw new DataException("-2", "El echoData es un parámetro obligatorio");
        }
        if (params.getShortReferenceNumber() == null) {
            throw new DataException("-2", "El shortReferenceNumber es un parámetro obligatorio");
        }
    }

}

