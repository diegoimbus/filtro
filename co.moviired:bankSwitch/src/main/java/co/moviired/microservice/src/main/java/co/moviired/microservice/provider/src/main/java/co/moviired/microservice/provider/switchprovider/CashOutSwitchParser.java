package co.moviired.microservice.provider.switchprovider;
/*
 * Copyright @2019. Movii, SAS. Todos los derechos reservados.
 *
 * @author Bejarano, Cindy
 * @version 1, 2019
 * @since 1.0
 */

import co.moviired.connector.helper.ISOMsgHelper;
import co.moviired.microservice.conf.BankProductsProperties;
import co.moviired.microservice.conf.SwitchProperties;
import co.moviired.microservice.domain.constants.ConstantSwitch;
import co.moviired.microservice.domain.enums.ErrorType;
import co.moviired.microservice.domain.iso.CashOutAgrarioRequest;
import co.moviired.microservice.domain.iso.CashOutBBVARequest;
import co.moviired.microservice.domain.iso.CashOutResponse;
import co.moviired.microservice.domain.request.Input;
import co.moviired.microservice.domain.response.Data;
import co.moviired.microservice.domain.response.ErrorDetail;
import co.moviired.microservice.domain.response.Outcome;
import co.moviired.microservice.domain.response.Response;
import co.moviired.microservice.exception.DataException;
import co.moviired.microservice.helper.UtilHelper;
import co.moviired.microservice.provider.IParser;
import lombok.extern.slf4j.Slf4j;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.packager.GenericPackager;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
@Service
public class CashOutSwitchParser implements IParser {


    private static final String FORMATTED_LOG_2 = "{} {}";
    private final SimpleDateFormat fechaISO = new SimpleDateFormat("MMddHHmmss");
    private final BankProductsProperties bankProducts;
    private String respuestaIncompleta = "La respuesta  recibida del operador está incompleta.";

    public CashOutSwitchParser(BankProductsProperties pbankProducts) {
        super();
        this.bankProducts = pbankProducts;
    }


    @Override
    public final ISOMsg parseRequest(Input params, SwitchProperties config, GenericPackager packager) throws DataException {


        // B. Validar datos oblifgatorios
        if (params.getReferenceNumber() == null) {
            throw new DataException("-2", "El referenceNumber es un parámetro obligatorio");
        }

        if (params.getValueToPay() == null) {
            throw new DataException("-2", "El valueToPay es un parámetro obligatorio");
        }

        if (params.getTercId() == null) {
            throw new DataException("-2", "El tercId es un parámetro obligatorio");
        }


        if (params.getOtp() == null) {
            throw new DataException("-2", "El otp es un parámetro obligatorio");
        }

        if (params.getServiceCode() == null) {
            throw new DataException("-2", "El serviceCode es un parámetro obligatorio");
        }

        if (params.getImei() == null) {
            throw new DataException("-2", "El imei es un parámetro obligatorio");
        }

        String[] imei = params.getImei().split("\\|");

        String agentCode = imei[ConstantSwitch.POSITION_7];
        if (agentCode.length() > 8) {
            agentCode = agentCode.substring(agentCode.length() - 8);
        }

        String gestorId = imei[ConstantSwitch.POSITION_12];

        String numberPhone = imei[ConstantSwitch.POSITION_8];
        String codeHomologatedBankId = imei[ConstantSwitch.POSITION_11];
        String deviceHomologated = imei[ConstantSwitch.POSITION_6];
        String tercId = imei[ConstantSwitch.POSITION_10];
        String correlationId = imei[ConstantSwitch.POSITION_3];

        if (bankProducts.getProductIdBvvaWithdrawal().equals(params.getServiceCode())) {
            CashOutBBVARequest isoRequest = new CashOutBBVARequest();

            isoRequest.setProductCode(params.getServiceCode());
            isoRequest.setProcessingCode(config.getProcessCodeCashOut());
            isoRequest.setAmount(UtilHelper.strPad(params.getValueToPay() + "00", ConstantSwitch.LENGTH_12, "0", 0));
            isoRequest.setTransmisionDateTime(fechaISO.format(new Date()));
            isoRequest.setTraceAuditNumberYML(config.getIdTransactionCashOut());
            isoRequest.setReferenceNumber(params.getReferenceNumber());
            isoRequest.setUpcID(params.getUpcId());
            isoRequest.setAgentCode(UtilHelper.strPad(agentCode, ConstantSwitch.LENGTH_8, " ", 1));
            isoRequest.setCellphoneNumber(UtilHelper.strPad(numberPhone, ConstantSwitch.LENGTH_15, " ", 1));
            isoRequest.setCodeHomologated(UtilHelper.strPad(codeHomologatedBankId, ConstantSwitch.LENGTH_40, " ", 1));
            isoRequest.setNameTercDeviceGestorCorrelation(params.getLastName() + "|" + tercId + "|" + deviceHomologated + "|" + gestorId + "|" + correlationId);
            isoRequest.setTransferReferenceAccounttypeTypedocOrdinal(params.getTransferId() + "|" + params.getReferenceNumber() + "|" + params.getAccountType() + "|" + params.getTypeDocument() + "|" + params.getAccountOrdinal());
            isoRequest.setAccounttypeTypedocReferenceOrdinalOtp(params.getAccountType() + "|" + params.getTypeDocument() + "|" + params.getReferenceNumber() + "|" + params.getAccountOrdinal() + "|" + params.getOtp());

            try {
                return ISOMsgHelper.of("200", isoRequest, new GenericPackager(new ClassPathResource("iso8583/iso-message.xml").getInputStream()));
            } catch (ISOException | IllegalAccessException | IOException e) {
                throw new DataException(e);
            }

        } else if (bankProducts.getProductIdAgrarioWithdrawal().equals(params.getServiceCode())) {

            CashOutAgrarioRequest isoRequest = new CashOutAgrarioRequest();
            isoRequest.setProductCode(params.getServiceCode());
            isoRequest.setProcessingCode(config.getProcessCodeCashOut());
            isoRequest.setAmount(UtilHelper.strPad(params.getValueToPay() + "00", ConstantSwitch.LENGTH_12, "0", 0));
            isoRequest.setTransmisionDateTime(fechaISO.format(new Date()));
            isoRequest.setTraceAuditNumberYML(config.getIdTransactionCashOut());
            isoRequest.setReferenceNumber(params.getReferenceNumber());
            isoRequest.setAgentCode(UtilHelper.strPad(agentCode, ConstantSwitch.LENGTH_8, " ", 1));
            isoRequest.setCellphoneNumber(UtilHelper.strPad(numberPhone, ConstantSwitch.LENGTH_15, " ", 1));
            isoRequest.setCodeHomologated(UtilHelper.strPad(codeHomologatedBankId, ConstantSwitch.LENGTH_40, " ", 1));
            isoRequest.setBlankField(UtilHelper.strPad("", ConstantSwitch.LENGTH_16, " ", 0));
            isoRequest.setNameTercDeviceGestorCorrelation(params.getLastName() + "|" + tercId + "|" + deviceHomologated + "|" + gestorId + "|" + correlationId);
            isoRequest.setReferenceAccountypeOtp(params.getReferenceNumber() + "|" + params.getAccountType() + "|" + params.getOtp());

            try {
                return ISOMsgHelper.of("200", isoRequest, new GenericPackager(new ClassPathResource("iso8583/iso-message.xml").getInputStream()));
            } catch (ISOException | IllegalAccessException | IOException e) {
                throw new DataException(e);
            }
        }
        return null;
    }

    @Override
    public final Response parseResponse(@NotNull ISOMsg respOper, Input request) throws DataException {
        // Transformar la respuesta
        Response response;
        Data data = null;

        // C. ARMAR EL MENSAJE - RESPONSE
        //   0	Identificacion del mensaje (MTI)
        //   2	Código de producto
        //   3	Código de procesamiento
        //   4	Valor (Entero, últimos 2 dígitos decimales)
        //   7	Fecha de envío (MMDDHHMISS)
        //  39	CÓDIGO DE RESPUESTA

        // Obtener el código de respuesta

        try {

            CashOutResponse cashOutResponse = ISOMsgHelper.resolve(respOper, CashOutResponse.class);

            String respCode = cashOutResponse.getStatusCode();
            if ((respCode == null) || (respCode.trim().isEmpty())) {
                throw new DataException("-3", respuestaIncompleta);
            }
            // Respuesta existosa
            if (respCode.equals("00")) {

                data = new Data();
                data.setValueToPay(cashOutResponse.getAmount());

                String[] codeTransaction = cashOutResponse.getMessageResponse().split("\\|");
                if (codeTransaction.length > 0) {
                    data.setAuthorizationCode(codeTransaction[0]);
                }
                data.setTransactionId(cashOutResponse.getAuthorizationNumber().trim());

                log.info("************ RESPONSE BankingSwitch CashOut ************");
                log.info(FORMATTED_LOG_2, "authorizationCode ", data.getAuthorizationCode());
                log.info(FORMATTED_LOG_2, "transactionId ", data.getTransactionId());

                ErrorDetail e = new ErrorDetail(0, respCode, "OK");
                Outcome result = new Outcome(HttpStatus.OK, e);
                response = new Response(result, data);
                return response;
            }

            ErrorDetail e = new ErrorDetail(ErrorType.PROCESSING, respCode, cashOutResponse.getMessageResponse());
            Outcome result = new Outcome(HttpStatus.OK, e);
            response = new Response(result, data);

        } catch (IllegalAccessException | InstantiationException e) {
            throw new DataException("-2", e.getMessage());
        } catch (Exception ex) {
            throw new DataException("-2", ex.getMessage());
        }

        return response;
    }
}

