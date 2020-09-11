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
import co.moviired.microservice.domain.iso.DepositAgrarioRequest;
import co.moviired.microservice.domain.iso.DepositObligationsAgrarioRequest;
import co.moviired.microservice.domain.iso.DepositResponse;
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
public class DepositSwitchParser implements IParser {

    private static final String FORMATTED_LOG_2 = "{} {}";
    private final SimpleDateFormat fechaISO = new SimpleDateFormat("MMddHHmmss");
    private final BankProductsProperties bankProducts;
    private String respuestaIncompleta = "La respuesta  recibida del operador está incompleta.";

    public DepositSwitchParser(BankProductsProperties pbankProducts) {
        super();
        this.bankProducts = pbankProducts;
    }


    @Override
    public final ISOMsg parseRequest(Input params, SwitchProperties config, GenericPackager packager) throws DataException {

        // A. Validar datos oblifgatorios

        if (params.getValueToPay() == null) {
            throw new DataException("-2", "El valueToPay es un parámetro obligatorio");
        }

        if (params.getEchoData() == null) {
            throw new DataException("-2", "El echoData es un parámetro obligatorio");
        }

        if (params.getTransferId() == null) {
            throw new DataException("-2", "El transferId es un parámetro obligatorio");
        }

        if (params.getImei() == null) {
            throw new DataException("-2", "El imei es un parámetro obligatorio");
        }


        String[] echoData = params.getEchoData().split("\\|");
        String[] shortReferenceNumber = params.getShortReferenceNumber().split("\\|");
        String[] imei = params.getImei().split("\\|");

        String agentCode = imei[ConstantSwitch.POSITION_7];
        if (agentCode.length() > 8) {
            agentCode = agentCode.substring(2);
        }
        String numberPhone = imei[ConstantSwitch.POSITION_8];
        String codeHomologatedBankId = imei[ConstantSwitch.POSITION_11];
        String deviceHomologated = imei[ConstantSwitch.POSITION_6];
        String tercId = imei[ConstantSwitch.POSITION_10];
        String gestorId = imei[ConstantSwitch.POSITION_12];
        String correlationId = imei[ConstantSwitch.POSITION_3];

        if (bankProducts.getProductIdAgrarioDeposit().equals(params.getServiceCode())) {

            DepositAgrarioRequest isoRequest = new DepositAgrarioRequest();
            isoRequest.setProductCode(echoData[ConstantSwitch.POSITION_2]);
            isoRequest.setProcessingCode(echoData[ConstantSwitch.POSITION_1]);
            isoRequest.setAmount(UtilHelper.strPad(params.getValueToPay() + "00", ConstantSwitch.LENGTH_12, "0", 0));
            isoRequest.setTransmisionDateTime(fechaISO.format(new Date()));
            isoRequest.setTraceAuditNumberYML(config.getIdTransaction());
            isoRequest.setReferenceNumber(shortReferenceNumber[ConstantSwitch.POSITION_0]);
            isoRequest.setAgentCode(UtilHelper.strPad(agentCode, ConstantSwitch.LENGTH_8, " ", 1));
            isoRequest.setCellphoneNumber(UtilHelper.strPad(numberPhone, ConstantSwitch.LENGTH_15, " ", 1));
            isoRequest.setCodeHomologated(UtilHelper.strPad(codeHomologatedBankId, ConstantSwitch.LENGTH_40, " ", 1));
            isoRequest.setBlankField(UtilHelper.strPad("", ConstantSwitch.LENGTH_16, " ", 0));
            isoRequest.setNameTercDeviceGestorCorrelation(echoData[ConstantSwitch.POSITION_0] + "|" + tercId + "|" + deviceHomologated + "|" + gestorId + "|" + correlationId);
            isoRequest.setTransferReferenceTypeAccount(params.getTransferId() + "|" + shortReferenceNumber[ConstantSwitch.POSITION_0] + "|" + shortReferenceNumber[ConstantSwitch.POSITION_1]);

            try {
                return ISOMsgHelper.of("200", isoRequest, new GenericPackager(new ClassPathResource("iso8583/iso-message.xml").getInputStream()));

            } catch (ISOException | IllegalAccessException | IOException e) {
                throw new DataException(e);
            }

        } else if (bankProducts.getProductIdAgrarioPayObligations().equals(params.getServiceCode())) {

            DepositObligationsAgrarioRequest isoRequest = new DepositObligationsAgrarioRequest();
            isoRequest.setProductCode(echoData[ConstantSwitch.POSITION_2]);
            isoRequest.setProcessingCode(echoData[ConstantSwitch.POSITION_1]);
            isoRequest.setAmount(UtilHelper.strPad(params.getValueToPay() + "00", ConstantSwitch.LENGTH_12, "0", 0));
            isoRequest.setTransmisionDateTime(fechaISO.format(new Date()));
            //isoRequest.setTransmisionDateTime("0717191919"); //cipabemo
            isoRequest.setTraceAuditNumberYML(config.getIdTransaction());
            isoRequest.setPrefixReferenceNumber(bankProducts.getPrefixAgrarioPayObligations() + shortReferenceNumber[ConstantSwitch.POSITION_0]);
            isoRequest.setAgentCode(UtilHelper.strPad(agentCode, ConstantSwitch.LENGTH_8, " ", 1));
            isoRequest.setCellphoneNumber(UtilHelper.strPad(numberPhone, ConstantSwitch.LENGTH_15, " ", 1));
            isoRequest.setCodeHomologated(UtilHelper.strPad(codeHomologatedBankId, ConstantSwitch.LENGTH_40, " ", 1));
            isoRequest.setNameTercDeviceGestorCorrelation(echoData[ConstantSwitch.POSITION_0] + "|" + tercId + "|" + deviceHomologated + "|" + gestorId + "|" + correlationId);
            isoRequest.setTransferReferenceTypeAccount(params.getTransferId() + "|" + shortReferenceNumber[ConstantSwitch.POSITION_0] + "|");
            isoRequest.setReferenceNumber(shortReferenceNumber[ConstantSwitch.POSITION_0]);



         /*   isoRequest.setProductCode("8045");
            isoRequest.setProcessingCode("000020");
            isoRequest.setAmount("000022809000");
            isoRequest.setTransmisionDateTime("0815104455");
            //isoRequest.setTransmisionDateTime("0717191919"); //cipabemo
            isoRequest.setTraceAuditNumberYML("093725");
            isoRequest.setPosition12Cipabemo("104454");
            isoRequest.setPrefixReferenceNumber("TX10804@# 725048060701003");
            isoRequest.setPosition37Cipabemo("1764929977");
            isoRequest.setPosition38Cipabemo("146381");
            isoRequest.setPosition39Cipabemo("00");
            isoRequest.setAgentCode("00002522");
            isoRequest.setCellphoneNumber("3166657446");
            isoRequest.setCodeHomologated("11620");

            //isoRequest.setAgentCode(UtilHelper.strPad(agentCode, ConstantSwitch.LENGTH_8, " ", 1));
            //isoRequest.setCellphoneNumber(UtilHelper.strPad(numberPhone, ConstantSwitch.LENGTH_15, " ", 1));
            //isoRequest.setCodeHomologated(UtilHelper.strPad(codeHomologatedBankId, ConstantSwitch.LENGTH_40, " ", 1));
            isoRequest.setNameTercDeviceGestorCorrelation( "SOLUCIONTES TECNOLOGICAS DAC|174484|421303|4|2019081510445452296");
            isoRequest.setTransferReferenceTypeAccount("006009446564|CHAMORRO ESTUPIÑAN ANDRES MAURICIO|000000000000||2019-08-15|10:44:54");
            isoRequest.setPosition73Cipabemo("150819");
            isoRequest.setReferenceNumber("725048060701003");
*/

            try {
                return ISOMsgHelper.of("200", isoRequest, new GenericPackager(new ClassPathResource("iso8583/iso-message.xml").getInputStream()));

            } catch (ISOException | IllegalAccessException | IOException e) {
                throw new DataException(e);
            }
        }

        return null;
    }


    @Override
    public final Response parseResponse(@NotNull ISOMsg respOper, Input params) throws DataException {
        // Transformar la respuesta
        Response response = null;
        Data data = null;

        try {

            DepositResponse depositResponse = ISOMsgHelper.resolve(respOper, DepositResponse.class);

            String respCode = depositResponse.getStatusCode();
            if ((respCode == null) || (respCode.trim().isEmpty())) {
                throw new DataException("-3", respuestaIncompleta);
            }
            // Respuesta existosa
            if (respCode.equals("00")) {

                data = new Data();

                String[] imei = params.getImei().split("\\|");
                String gestorId = imei[ConstantSwitch.POSITION_12];

                String[] shortReferenceNumber = params.getShortReferenceNumber().split("\\|");
                String referenceNumber = shortReferenceNumber[ConstantSwitch.POSITION_0];

                if (bankProducts.getProductIdAgrarioDeposit().equals(params.getServiceCode())) {

                    data.setValueToPay(depositResponse.getAmount());
                    data.setAuthorizationCode(depositResponse.getAuthorizationNumber());
                    data.setTransactionId(depositResponse.getIdGetrax());
                    data.setBankId(gestorId);
                    data.setShortReferenceNumber(referenceNumber);
                    log.info("************ RESPONSE BankingSwitch Deposit ************");
                    log.info(FORMATTED_LOG_2, "productCode ", data.getProductCode());
                    log.info(FORMATTED_LOG_2, "processscode ", data.getProcessCode());
                    log.info(FORMATTED_LOG_2, "valuetoPay ", data.getValueToPay());
                    log.info(FORMATTED_LOG_2, "idGetrax ", data.getTransactionId());
                    log.info(FORMATTED_LOG_2, "gestorId ", gestorId);
                    log.info(FORMATTED_LOG_2, "AuthorizationCode ", data.getAuthorizationCode());
                    log.info(FORMATTED_LOG_2, "referenceNumber ", referenceNumber);

                    ErrorDetail e = new ErrorDetail(0, respCode, "OK");
                    Outcome result = new Outcome(HttpStatus.OK, e);
                    response = new Response(result, data);

                } else if (bankProducts.getProductIdAgrarioPayObligations().equals(params.getServiceCode())) {

                    data.setValueToPay(depositResponse.getAmount());

                    data.setAuthorizationCode(depositResponse.getAuthorizationNumber());
                    data.setTransactionId(depositResponse.getIdGetrax());
                    data.setBankId(gestorId);
                    data.setShortReferenceNumber(referenceNumber);

                    ErrorDetail e = new ErrorDetail(0, respCode, "OK");
                    Outcome result = new Outcome(HttpStatus.OK, e);
                    response = new Response(result, data);
                }

                return response;
            }

            data = new Data();

            String[] imei = params.getImei().split("\\|");
            String gestorId = imei[ConstantSwitch.POSITION_12];

            String[] shortReferenceNumber = params.getShortReferenceNumber().split("\\|");
            String referenceNumber = shortReferenceNumber[ConstantSwitch.POSITION_0];

            data.setTransactionId(depositResponse.getIdGetrax());
            data.setBankId(gestorId);
            data.setShortReferenceNumber(referenceNumber);

            ErrorDetail e = new ErrorDetail(ErrorType.PROCESSING, respCode, depositResponse.getMessageResponse());
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

