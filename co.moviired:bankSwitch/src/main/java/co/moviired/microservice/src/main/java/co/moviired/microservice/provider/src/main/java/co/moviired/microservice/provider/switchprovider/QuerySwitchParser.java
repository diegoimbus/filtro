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
import co.moviired.microservice.domain.iso.QueryAgrarioRequest;
import co.moviired.microservice.domain.iso.QueryBBVARequest;
import co.moviired.microservice.domain.iso.QueryResponse;
import co.moviired.microservice.domain.request.Input;
import co.moviired.microservice.domain.response.*;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class QuerySwitchParser implements IParser {

    private static final String FORMATTED_LOG_2 = "{} {}";
    private final SimpleDateFormat fechaISO = new SimpleDateFormat("MMddHHmmss");
    private final BankProductsProperties bankProducts;
    private String respuestaIncompleta = "La respuesta  recibida del operador está incompleta.";

    public QuerySwitchParser(BankProductsProperties pbankProducts) {
        super();
        this.bankProducts = pbankProducts;
    }


    @Override
    public final ISOMsg parseRequest(Input params, SwitchProperties config, GenericPackager packager) throws DataException {

        // A.  Validar parámetros de entrada

        // B. Validar datos oblifgatorios
        if (params.getReferenceNumber() == null) {
            throw new DataException("-2", "El referenceNumber es un parámetro obligatorio");
        }

        if (params.getTercId() == null) {
            throw new DataException("-2", "El tercId es un parámetro obligatorio");
        }

       /* if (params.getAccountOrdinal() == null) {
            throw new DataException("-2", "El accountOrdinal es un parámetro obligatorio");
        }
*/
        if (params.getTypeDocument() == null) {
            throw new DataException("-2", "El typeDocument es un parámetro obligatorio");
        }

        if (params.getServiceCode() == null) {
            throw new DataException("-2", "El serviceCode es un parámetro obligatorio");
        }

        if (params.getImei() == null) {
            throw new DataException("-2", "El imei es un parámetro obligatorio");
        }

        String[] imei = params.getImei().split("\\|");

        String agentCode = imei[ConstantSwitch.POSITION_6];
        if (agentCode.length() > 8) {
            agentCode = agentCode.substring(2);
        }

        String numberPhone = imei[ConstantSwitch.POSITION_7];
        String codeHomologatedBankId = imei[ConstantSwitch.POSITION_8];
        String deviceHomologated = imei[ConstantSwitch.POSITION_5];
        String tercId = imei[ConstantSwitch.POSITION_9];
        String gestorId = imei[ConstantSwitch.POSITION_10];
        String correlationId = imei[ConstantSwitch.POSITION_2];


        if (bankProducts.getProductIdBbvaQueryWithDrawal().equals(params.getServiceCode())) {

            QueryBBVARequest isoRequest = new QueryBBVARequest();

            isoRequest.setProductCode(params.getServiceCode());
            isoRequest.setProcessingCode(config.getProcessCodeQuery());
            isoRequest.setAmount(UtilHelper.strPad("00", ConstantSwitch.LENGTH_12, "0", 0));
            isoRequest.setTransmisionDateTime(fechaISO.format(new Date()));
            isoRequest.setTraceAuditNumberYML(config.getIdTransactionQuery());
            isoRequest.setReferenceNumber(params.getReferenceNumber());
            isoRequest.setTercId(UtilHelper.strPad(config.getTercIDMahindra(), ConstantSwitch.LENGTH_8, " ", 1));
            isoRequest.setUsernameQuery(UtilHelper.strPad(config.getUserNameQuery(), ConstantSwitch.LENGTH_15, " ", 1));
            isoRequest.setCodeHomologated(UtilHelper.strPad(codeHomologatedBankId, ConstantSwitch.LENGTH_40, " ", 1));

            isoRequest.setNameTercDeviceGestorCorrelation(params.getLastName() + "|" + tercId + "|" + deviceHomologated + "|" + gestorId + "|" + correlationId);
            isoRequest.setTransferReferenceTypeaccTypedocOrdinal(params.getTransferId() + "|" + params.getReferenceNumber() + "|" + params.getAccountType() + "|" + params.getTypeDocument() + "|" + params.getAccountOrdinal());
            isoRequest.setAccounttypeTypedocReferenceOrdinal(params.getAccountType() + "|" + params.getTypeDocument() + "|" + params.getReferenceNumber() + "|" + params.getAccountOrdinal());

            try {
                return ISOMsgHelper.of("200", isoRequest, new GenericPackager(new ClassPathResource("iso8583/iso-message.xml").getInputStream()));

            } catch (ISOException | IllegalAccessException | IOException e) {
                throw new DataException(e);
            }

        } else if (bankProducts.getProductIdAgrarioQueryObligations().equals(params.getServiceCode())) {

            QueryAgrarioRequest isoRequest = new QueryAgrarioRequest();
            isoRequest.setProductCode(params.getServiceCode());
            isoRequest.setProcessingCode(config.getProcessCodeQuery());
            isoRequest.setAmount(UtilHelper.strPad("00", ConstantSwitch.LENGTH_12, "0", 0));
            isoRequest.setTransmisionDateTime(fechaISO.format(new Date()));
            isoRequest.setTraceAuditNumberYML(config.getIdTransactionQuery());
            isoRequest.setReferenceNumber(params.getReferenceNumber());
            isoRequest.setPosicion35("0@# ".trim() + " ");
            isoRequest.setTercId(UtilHelper.strPad(agentCode, ConstantSwitch.LENGTH_8, " ", 1));
            isoRequest.setUsernameQuery(UtilHelper.strPad(numberPhone, ConstantSwitch.LENGTH_15, " ", 1));
            isoRequest.setCodeHomologated(UtilHelper.strPad(codeHomologatedBankId, ConstantSwitch.LENGTH_40, " ", 1));
            isoRequest.setTypeDocument(params.getTypeDocument());
            isoRequest.setNameTercDeviceGestorCorrelation(params.getLastName() + "|" + tercId + "|" + deviceHomologated + "|" + gestorId + "|" + correlationId);
            isoRequest.setNumberDocument(params.getNumberDocument() + "|");

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
        Response response = null;
        Data data = null;

        try {
            QueryResponse queryResponse = ISOMsgHelper.resolve(respOper, QueryResponse.class);

            String respCode = queryResponse.getStatusCode();
            if ((respCode == null) || (respCode.trim().isEmpty())) {
                throw new DataException("-3", respuestaIncompleta);
            }

            // Respuesta existosa
            if (respCode.equals("00")) {

                data = new Data();
                String[] position63 = queryResponse.getMessageResponse().split("\\|");

                if (bankProducts.getProductIdBbvaQueryWithDrawal().equals(request.getServiceCode())) {

                    data.setBalance(position63[ConstantSwitch.POSITION_3]);
                    data.setComission(position63[ConstantSwitch.POSITION_1]);
                    data.setShortReferenceNumber(position63[ConstantSwitch.POSITION_0]); //--> cuenta
                    data.setUpcID(position63[ConstantSwitch.POSITION_4]);
                    data.setValueToPay(position63[ConstantSwitch.POSITION_2]);
                    data.setAuthorizationCode(queryResponse.getAuthorizationNumber());

                    log.info("************ RESPONSE BankingSwitch Query ************");
                    log.info(FORMATTED_LOG_2, "billReferenceNumber ", data.getBillReferenceNumber());
                    log.info(FORMATTED_LOG_2, "valueToPay ", data.getValueToPay());
                    log.info(FORMATTED_LOG_2, "balance", data.getBalance());
                    log.info(FORMATTED_LOG_2, "shortReferenceNumber ", data.getShortReferenceNumber());
                    log.info(FORMATTED_LOG_2, "upcId ", data.getUpcID());
                    log.info(FORMATTED_LOG_2, "comission ", data.getComission());

                } else if (bankProducts.getProductIdAgrarioQueryObligations().equals(request.getServiceCode())) {

                    String titular = position63[1];
                    int amountObligations = Integer.parseInt(position63[3]);
                    String obligationData = null;
                    List<Obligation> listObligation = new ArrayList<>();

                    for (int i = 0; i < amountObligations; i++) {
                        obligationData = position63[(4 + i)];
                        if (obligationData != null) {
                            String[] obligation = obligationData.split("#");

                            if (obligation.length == 4) {
                                Obligation obl = new Obligation();

                                obl.setAccountHolder(titular);
                                obl.setReferenceNumber(obligation[0]);
                                obl.setDate(obligation[1]);
                                obl.setValuePartialPayment(obligation[2]);
                                obl.setValueToPay(obligation[3]);
                                listObligation.add(obl);

                            }
                        }
                    }
                    data.setListObligations(listObligation);
                }

                ErrorDetail e = new ErrorDetail(0, respCode, "OK");
                Outcome result = new Outcome(HttpStatus.OK, e);
                response = new Response(result, data);
                return response;
            }

            ErrorDetail e = new ErrorDetail(ErrorType.PROCESSING, respCode, queryResponse.getMessageResponse());
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

