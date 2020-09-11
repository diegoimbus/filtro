package com.moviired.controller;

import co.moviired.base.domain.StatusCode;
import co.moviired.base.domain.exception.ParsingException;
import co.moviired.base.helper.CryptoHelper;
import co.moviired.base.util.Generator;
import co.moviired.connector.domain.iso.enumeration.MTI;
import co.moviired.connector.helper.ISOMsgHelper;
import co.moviired.connector.helper.ISOPersistentClient;
import com.moviired.conf.StatusCodeConfig;
import com.moviired.helper.AtallaHelper;
import com.moviired.helper.Constant;
import com.moviired.helper.Utilidad;
import com.moviired.model.Configurations;
import com.moviired.model.Network;
import com.moviired.model.entities.cashservice.ServibancaAudit;
import com.moviired.model.request.CashOutRequest;
import com.moviired.model.response.AtallaResponse;
import com.moviired.model.response.Data;
import com.moviired.repository.jpa.cashservice.ICashOutRepository;
import com.moviired.repository.jpa.cashservice.IServibancaAuditRespository;
import com.moviired.service.CashOutService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;
import org.jpos.iso.ISOBasePackager;
import org.jpos.iso.ISODate;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import static com.moviired.helper.Constant.*;

@Slf4j
@Component
@Scope("prototype")
public class CashISOController extends ISOPersistentClient {
    private static final String ERROR_INTERNAL = "84";
    private static final String PROCESSING_CODE_COST = "89";  //CONSULTA DE COSTO
    private static final String PROCESSING_CODE_CASHOUT = "01"; //RETIRO
    private static final String NOT_IMPLEMENTED = "Transacción no implementada";

    private static final Integer FIELDS_128 = 128;
    private static final Integer FIELDS_126 = 126;
    private static final Integer FIELDS_102 = 102;
    private static final Integer FIELDS_37 = 37;
    private static final Integer FIELDS_38 = 38;
    private static final Integer FIELDS_39 = 39;
    private static final Integer FIELDS_58 = 58;
    private static final Integer FIELDS_04 = 4;
    private static final Integer FIELDS_07 = 7;
    private static final Integer FIELDS_12 = 12;
    private static final Integer FIELDS_13 = 13;
    private static final Integer FIELDS_15 = 15;
    private final StatusCodeConfig statusCodeConfig;
    private final CashOutService cashOutService;
    private final ISOBasePackager isoPackager;
    private final CryptoHelper cryptoHelperOtp;
    private final Network network;
    private final ICashOutRepository iCashOutRepository;
    private final AtallaHelper atallaHelper;
    private final IServibancaAuditRespository servibancaAuditRespository;
    private final Configurations configurations;
    private String descriptionTransaction;

    public CashISOController(@Qualifier("statusCodeConfig") StatusCodeConfig pStatusCodeConfig,
                             @NotNull CashOutService pCashOutService,
                             @NotNull ISOBasePackager pIsoPackager,
                             @NotNull @Qualifier("cryptoHelperOtp") CryptoHelper pCryptoHelperOtp,
                             @NotNull Network pNetwork,
                             @NotNull ICashOutRepository pIcashOutRepository,
                             @NotNull Configurations pWsConfig,
                             @NotNull AtallaHelper pAtallaHelper, IServibancaAuditRespository pServibancaAuditRepository, Configurations pConfigurations) {
        super(pWsConfig.getSocketIp(), pWsConfig.getSocketPort(), pWsConfig.getConnectionTimeout(), pWsConfig.getTransactionTimeout());
        this.statusCodeConfig = pStatusCodeConfig;
        this.cashOutService = pCashOutService;
        this.isoPackager = pIsoPackager;
        this.cryptoHelperOtp = pCryptoHelperOtp;
        this.network = pNetwork;
        this.iCashOutRepository = pIcashOutRepository;
        this.atallaHelper = pAtallaHelper;
        this.servibancaAuditRespository = pServibancaAuditRepository;
        this.configurations = pConfigurations;
    }

    /**
     * Metodo commuter.
     *
     * @param input
     * @return byte[].
     */
    @Override
    public byte[] commuter(@NotNull String input) {
        ISOMsg response = new ISOMsg();
        byte[] resp = {};
        try {
            String correlationId = assignCorrelative();
            log.info(".................... INICIO TRANSACCIÓN .................");
            log.info("REQUEST  : {}", input);

            this.descriptionTransaction = "";

            ISOMsg isoMsg = new ISOMsg();
            isoMsg.setPackager(isoPackager);
            isoMsg.unpack(input.getBytes());
            ISOMsgHelper.logISOMsg(isoMsg);

            ServibancaAudit servibancaAudit = new ServibancaAudit();

            servibancaAudit.setStartDate(new Date());
            servibancaAudit.setRequest(input);
            servibancaAudit.setTransactionType(isoMsg.getMTI());
            servibancaAudit.setCorrelationId(correlationId);

            if (MTI.NETWORK_ADMINISTRATION_MESSAGES.value().equals(isoMsg.getMTI())) {

                response = getEcho(isoMsg);

            } else {

                String p58 = isoMsg.getString(FIELDS_58).substring(FOUR);

                StringBuilder concilation = new StringBuilder();
                concilation.append(p58.substring(0, THIRTEEN).replaceFirst("^0*", ""));
                concilation.append("|");
                concilation.append(isoMsg.getString(FIELDS_37), FOUR, EIGHT);
                concilation.append("|");
                concilation.append(isoMsg.getString(FIELDS_37), EIGHT, TWELVE);
                concilation.append("|");
                concilation.append(isoMsg.getString(FIFTEEN));
                concilation.trimToSize();

                servibancaAudit.setConciliation(concilation.toString());

                response = financialProcess(isoMsg, correlationId, input);

                if (response.getString(THIRTY_NINE).equals("00")) {
                    servibancaAudit.setTransactionState(Constant.SUCCESSFUL_TEXT);
                } else {
                    servibancaAudit.setTransactionState("DENEGADO");
                }

                servibancaAudit.setDescription(descriptionTransaction);
                servibancaAudit.setResponse(new String(response.pack()));
                servibancaAuditRespository.save(servibancaAudit);

            }

            resp = response.pack();
            return resp;
        } catch (Exception | ParsingException e) {
            log.error("ISO ERROR. Causa: {}", e.getMessage());
            response = messageErrorDefault();
            try {
                resp = response.pack();
                log.info(new String(resp));
            } catch (ISOException ex) {
                log.error("Error commuter {} ", ex.getMessage());
            }
            return resp;
        } finally {
            log.info("RESPONSE : {}", new String(resp));
            ISOMsgHelper.logISOMsg(response);
            log.info(".................... FIN TRANSACCIÓN .................");
        }
    }

    private ISOMsg financialProcess(ISOMsg isoMsg, String correlationId, String input) throws IOException, ParsingException, ISOException {

        ISOMsg response;
        Network.NetworkDetail networkDetail;

        // Cajero:
        //   - xxxxx0: Servibanca
        //   - xxxxx2: Otros cajeros

        if (isoMsg.getString(FORTY_TWO).trim().endsWith("0")) {
            networkDetail = network.getOption(configurations.getAgentCodeServibanca());
        } else {
            networkDetail = network.getOption(configurations.getAgentCodeServibancaAliados());
        }

        if (!isoMsg.hasField(FIELDS_128)) {
            response = messageErrorMac(isoMsg);
        } else {
            String field128Mac = isoMsg.getString(FIELDS_128).substring(0, FOUR) + " " + isoMsg.getString(FIELDS_128).substring(FOUR, EIGHT);
            AtallaResponse atallaResponse = atallaHelper.sendMessage(false, input.substring(0, input.length() - isoMsg.getString(FIELDS_128).length()), field128Mac);
            if (atallaResponse == null || !atallaResponse.isValid()) {
                response = messageErrorMac(isoMsg);
            } else {
                response = processTramaService(isoMsg, networkDetail, correlationId);
            }
        }

        return response;
    }


    private ISOMsg processTramaService(ISOMsg isoMsg, Network.NetworkDetail networkDetail, String correlationId) throws ParsingException, IOException, ISOException {

        if (MTI.FINANCIAL_TRANSACTION_REQUEST.value().equals(isoMsg.getMTI()) || MTI.NOTICE_OF_FINANCIAL_TRANSACTION.value().equals(isoMsg.getMTI())) {
            switch (isoMsg.getString(THREE).substring(0, 2)) {
                case PROCESSING_CODE_COST:
                    descriptionTransaction = "CASH-OUT-COST-TRANSACTION";
                    return getCashOutCost(isoMsg, networkDetail, correlationId);
                case PROCESSING_CODE_CASHOUT:
                    descriptionTransaction = "CASH-OUT";
                    return cashOutComplete(isoMsg, networkDetail, correlationId);
                default:
                    throw new NotImplementedException(NOT_IMPLEMENTED);
            }
        } else if (MTI.REVERSAL_OF_FINANCIAL_TRANSACTION.value().equals(isoMsg.getMTI()) || MTI.NOTICE_OF_FINANCIAL_TRANSACTION_REVERSAL.value().equals(isoMsg.getMTI())) {
            descriptionTransaction = "REVERSE";
            return cashOutReverse(isoMsg, networkDetail, correlationId);
        } else {
            throw new NotImplementedException(NOT_IMPLEMENTED);
        }
    }

    private ISOMsg getEcho(ISOMsg isoMsg) throws ISOException, IOException {
        Data data = new Data();
        try {
            this.iCashOutRepository.findById(1);
            data.setCode(StatusCode.Level.SUCCESS.value());
        } catch (Exception e) {
            data.setCode("96");
        }
        return generateResponse(isoMsg, MTI.RESPONSE_TO_MESSAGES_NETWORK_MANAGEMENT.value(), data, null);
    }

    private ISOMsg getCashOutCost(ISOMsg isoMsg, Network.NetworkDetail networkDetail, String correlationId) throws ISOException, ParsingException, IOException {
        log.info("**************Iniciando el servicio de CashOutCost**************");
        CashOutRequest request = parse(isoMsg, networkDetail, correlationId);

        Data data = cashOutService.findCostByNetwork(request.getAgentCode());
        this.descriptionTransaction = this.descriptionTransaction.concat(Constant.TEXT_PLECA).concat(Constant.SUCCESSFUL_TEXT);

        return generateResponse(isoMsg, MTI.RESPONSE_TO_FINANCIAL_TRANSACTION_REQUEST.value(), data, networkDetail);
    }

    private ISOMsg cashOutComplete(ISOMsg isoMsg, Network.NetworkDetail networkDetail, String correlationId) throws ISOException, ParsingException, IOException {
        log.info("**************Iniciando el servicio de CashOutComplete**************");
        Data data;
        CashOutRequest request = parse(isoMsg, networkDetail, correlationId);
        data = cashOutService.complete(request);

        if (Constant.TRANSACTION_OK_00.equals(data.getErrorCode())) {
            data.setCode("00");
            this.descriptionTransaction = this.descriptionTransaction.concat(Constant.TEXT_PLECA).concat(Constant.SUCCESSFUL_TEXT);
        } else if (statusCodeConfig.of(Constant.CASH_OUT_NO_FOUND).getCode().equals(data.getErrorCode()) || statusCodeConfig.of(Constant.AMOUNT_IS_INVALID).getCode().equals(data.getErrorCode()) || statusCodeConfig.of(Constant.OTP_NOT_FOUND).getCode().equals(data.getErrorCode())) {
            data.setCode("06");
            this.descriptionTransaction = this.descriptionTransaction.concat(Constant.TEXT_PLECA + data.getErrorMessage());
        } else {
            data.setCode("12");
            this.descriptionTransaction = this.descriptionTransaction.concat(Constant.TEXT_PLECA + data.getErrorMessage());
        }
        return generateResponse(isoMsg, MTI.RESPONSE_TO_FINANCIAL_TRANSACTION_REQUEST.value(), data, networkDetail);
    }

    private ISOMsg cashOutReverse(ISOMsg isoMsg, Network.NetworkDetail networkDetail, String correlationId) throws ISOException, ParsingException, IOException {
        CashOutRequest request = parse(isoMsg, networkDetail, correlationId);
        Data data = cashOutService.reverse(request);

        if (Constant.TRANSACTION_OK_00.equals(data.getErrorCode())) {
            data.setCode("00");
            this.descriptionTransaction = this.descriptionTransaction.concat(Constant.TEXT_PLECA).concat(Constant.SUCCESSFUL_TEXT);
        } else {
            data.setCode("12");
            this.descriptionTransaction = this.descriptionTransaction.concat(Constant.TEXT_PLECA + data.getErrorMessage());
        }
        return generateResponse(isoMsg, MTI.RESPONSE_OF_REVERSAL_OF_FINANCIAL_TRANSACTION.value(), data, networkDetail);
    }

    private CashOutRequest parse(ISOMsg isoMsg, Network.NetworkDetail networkDetail, String correlationId) throws ParsingException {
        CashOutRequest request = new CashOutRequest();
        request.setCorrelationId(correlationId);
        request.setUserLogin(networkDetail.getMsisdn());
        request.setPin(networkDetail.getPin());
        request.setPhoneNumber(networkDetail.getMsisdn());
        request.setIp(isoMsg.getString(FORTY_THREE));
        request.setIssueDate(Utilidad.dateFormat(new Date()));
        request.setIssuerName(Constant.NAME_SERVIBANCA);
        request.setConciliationDate(isoMsg.getString(FIFTEEN));

        request.setAgentCode(configurations.getAgentCodeServibanca());
        request.setPosId(isoMsg.getString(FORTY_TWO));
        request.setIssuerId(isoMsg.getString(ELEVEN));
        request.setAmount(Integer.parseInt(isoMsg.getString(FIELDS_04)) / HUNDRED);
        request.setTxnId(isoMsg.getString(FIELDS_37));
        String p58 = isoMsg.getString(FIELDS_58).substring(FOUR);
        request.setIdentificationNumber(p58.substring(0, THIRTEEN).replaceFirst("^0*", ""));
        request.setOtp(cryptoHelperOtp.encoder(p58.substring(p58.length() - networkDetail.getLengthOTP())));
        request.setSource("CHANNEL");
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());

        return request;
    }

    private ISOMsg generateResponse(ISOMsg isoMsg, String mti, Data data, Network.NetworkDetail networkDetail) throws ISOException, IOException {
        ISOMsg isoMsgResponse = new ISOMsg();
        isoMsgResponse.setPackager(isoPackager);
        isoMsgResponse.unpack(isoMsg.pack());
        isoMsgResponse.setMTI(mti);
        isoMsgResponse.unset(FOURTEEN);
        isoMsgResponse.set(FIELDS_39, data.getCode());

        if (MTI.RESPONSE_TO_FINANCIAL_TRANSACTION_REQUEST.value().equals(mti)) {
            //RESPUESTA 0210 CONSULTA DE COSTO O RETIRO
            isoMsgResponse.set(FIELDS_38, isoMsg.getString(ELEVEN));
            isoMsgResponse.set(FIELDS_102, data.getCorrelationId());
            if (!data.getCode().equals("00")) {
                isoMsgResponse.set(FIELDS_126, configurationField126("0", isoMsg.getString(FIELDS_126)));
            } else {
                isoMsgResponse.set(FIELDS_126, configurationField126(String.valueOf(networkDetail.getCostTransaction()), isoMsg.getString(FIELDS_126)));
            }
        }

        if (!MTI.NETWORK_ADMINISTRATION_MESSAGES.value().equals(mti) && !MTI.RESPONSE_TO_MESSAGES_NETWORK_MANAGEMENT.value().equals(mti)) {
            byte[] isoMsgByte = isoMsgResponse.pack();
            String tramaResponse = new String(isoMsgByte);
            AtallaResponse atallaResponse = this.atallaHelper.sendMessage(true, tramaResponse.substring(0, tramaResponse.length() - isoMsgResponse.getString(FIELDS_128).length()), "");
            isoMsgResponse.set(FIELDS_128, atallaResponse.getMac().replace(" ", "").concat("00000000"));
        }

        isoMsgResponse.recalcBitMap();
        return isoMsgResponse;
    }

    private ISOMsg messageErrorMac(ISOMsg isoMsg) {
        char[] field0 = isoMsg.getString(0).toCharArray();
        field0[0] = '9';
        String identificateMessage = String.valueOf(field0);
        isoMsg.set(0, identificateMessage);
        return isoMsg;

    }

    private ISOMsg messageErrorDefault() {
        ISOMsg isoMsgResponse = new ISOMsg();
        try {
            isoMsgResponse.setPackager(isoPackager);
            isoMsgResponse.setMTI(MTI.RESPONSE_TO_MESSAGES_NETWORK_MANAGEMENT.value());
            Date now = new Date();
            isoMsgResponse.set(FIELDS_07, ISODate.getDateTime(now));
            isoMsgResponse.set(FIELDS_12, ISODate.getDate(now));
            isoMsgResponse.set(FIELDS_13, ISODate.getExpirationDate(now));
            isoMsgResponse.set(FIELDS_15, ISODate.getExpirationDate(now));

            isoMsgResponse.set(FIELDS_39, ERROR_INTERNAL);

            return isoMsgResponse;

        } catch (ISOException e) {
            log.error("error messageErrorDefault {}", e.getMessage());
        }

        return isoMsgResponse;

    }

    private String configurationField126(String transactionCost, String field126) {

        char[] field = field126.toCharArray();
        String transactionCostChange;

        switch (transactionCost.length()) {
            case FOUR:
                transactionCostChange = "000000".concat(transactionCost).concat("00");
                break;
            case FIVE:
                transactionCostChange = "00000".concat(transactionCost).concat("00");
                break;
            case SIX:
                transactionCostChange = "0000".concat(transactionCost).concat("00");
                break;
            default:
                transactionCostChange = "000000000000";
                break;
        }

        for (int i = 0; i < transactionCostChange.length(); i++) {
            field[field126.length() - FOURTEEN + i] = transactionCostChange.charAt(i);
        }

        return String.valueOf(field);
    }

    private String assignCorrelative() {
        String correlation = String.valueOf(Generator.correlationId());
        MDC.putCloseable("correlation-id", correlation);
        MDC.putCloseable("component", "cash-out");
        return correlation;
    }

}

