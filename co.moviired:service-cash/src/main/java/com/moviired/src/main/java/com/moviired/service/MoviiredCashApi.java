package com.moviired.service;

import co.moviired.base.domain.StatusCode;
import co.moviired.base.domain.enumeration.ErrorType;
import co.moviired.base.domain.exception.ProcessingException;
import co.moviired.base.domain.exception.ServiceException;
import co.moviired.base.helper.CommandHelper;
import com.moviired.client.mahindra.command.Request;
import com.moviired.client.mahindra.command.Response;
import com.moviired.conf.StatusCodeConfig;
import com.moviired.helper.Constant;
import com.moviired.helper.Utilidad;
import com.moviired.model.Configurations;
import com.moviired.model.Network;
import com.moviired.model.entities.cashservice.CashIn;
import com.moviired.model.request.RequestFormat;
import com.moviired.model.response.Data;
import com.moviired.repository.ICashRepository;
import com.moviired.repository.jpa.cashservice.ICashInRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.Serializable;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/*
 * Copyright @2018. MOVIIRED. Todos los derechos reservados.
 *
 * @author Cuadros Cerpa, Cristian Alfonso
 * @version 1, 2018-06-20
 * @since 1.0
 */
@Slf4j
@Service("moviiredCashApi")
public class MoviiredCashApi implements Serializable {

    private static final long serialVersionUID = 9191184373758100720L;
    private static final String TRANSACTION_OK = "200";
    private static final String TRANSACTION_ERROR = "400";
    private static final String TRANSACTION_FATAL = "500";
    private static final String HEADER_XML = "<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>";
    private static final String LBLTIEMPO = "Tiempo empleado en el envío/respuesta de CashApi:  ";
    private static final String LBLTYPE = "TYPE";
    private static final String LBLMSISDN = "MSISDN";
    private static final String LBLMPIN = "MPIN";
    private static final String LBLPIN = "PIN";
    private static final String LBLAMOUNT = "AMOUNT";
    private static final String LBLSOCKETEXCEPTION = "Ocurrio un error al intentar conectarse con el core [SocketException] : ";
    private static final String LBLIOEXCEPTION = "Ocurrio un error al intentar obtener la respuesta del core [IOException] : ";
    private static final String LBLEXCEPTION = "Ocurrio un error inesperado [Exception] : ";
    private static final String LBLPROVIDER = "PROVIDER";
    private static final String LBLLANGUAGE = "LANGUAGE";
    private static final String LBLREMARKS = "REMARKS";
    private static final String LBLFTXNID = "FTXNID";
    private static final String LBLTXNID = "TXNID";
    private static final String LBLPAYID = "PAYID";
    private static final String LBLCELLID = "CELLID";
    private static final String LBLTXNMODE = "TXNMODE";
    private static final String LBLBLOCKSMS = "BLOCKSMS";
    private static final String MESSAGE_NAME = "[Code = 500] Response = ";
    private static final String COMMAND = "Request";
    private static final String INVALID_CORRELATION_ID = "20";
    private final Configurations configurations;
    private final ICashRepository cashRepository;
    private final StatusCodeConfig statusCodeConfig;
    private final Network network;
    private final ICashInRepository cashInRepositoryRepository;

    @Autowired
    public MoviiredCashApi(
            @NotNull ICashRepository pcashRepository,
            @NotNull Configurations pconfigurations,
            StatusCodeConfig statusCodeConfig, Network pNetwork,
            ICashInRepository pCashInRepository) {

        super();
        this.configurations = pconfigurations;
        this.cashRepository = pcashRepository;
        this.statusCodeConfig = statusCodeConfig;
        this.network = pNetwork;
        this.cashInRepositoryRepository = pCashInRepository;

        log.info("Configuración del servicio: Cash API" + " - INICIADA");
    }

    private static Request processResponse(String responseBody) throws IOException {
        return CommandHelper.readXML(responseBody, Request.class);
    }

    public final Data cashIn(RequestFormat request) {
        Data response = new Data();
        long createComIn = 0;
        long createComFin;
        try {

            validateCorrelationId(request.getCorrelationId(), request.getMerchantId());

            Network.NetworkDetail networkDetail = network.getOption(request.getAgentCode());
            if (!networkDetail.isEnable()) {
                throw new ProcessingException(statusCodeConfig.of("NETWORK_DISABLED", StatusCode.Level.FAIL));
            }

            Utilidad.assignCorrelative(request.getCorrelationId());

            log.info("**************Iniciando el servicio de CashIn**************");
            createComIn = System.currentTimeMillis();
            Response responseMahindra = this.createRequestBasicData(request);

            if (TRANSACTION_OK.equals(responseMahindra.getTxnstatus())) {
                String requestCashin = this.createRequestCashIn(request);
                responseMahindra = this.cashRepository.sendMahindraRequest(processResponse(requestCashin));

                if (TRANSACTION_OK.equals(responseMahindra.getTxnstatus())) {

                    CashIn cashIn = new CashIn();
                    cashIn.setCorrelationId(request.getCorrelationId());
                    cashIn.setAgentCode(request.getMerchantId());
                    cashIn.setPhoneNumber(request.getPhoneNumber());
                    cashIn.setTxnId(responseMahindra.getTxnid());
                    this.cashInRepositoryRepository.save(cashIn);

                    // Transaction Status
                    response.setCode(TRANSACTION_OK);
                    response.setErrorType("0");
                    response.setErrorCode("00");
                    response.setErrorMessage(statusCodeConfig.of("CONSIGNMENT_OK", StatusCode.Level.SUCCESS).getMessage());

                    // Specific data
                    response.setTransactionId(responseMahindra.getTxnid());
                    response.setTransactionDate(date());
                    response.setAmount(request.getAmount());
                    response.setCorrelationId(request.getCorrelationId());

                    return response;
                }
            }

            response.setErrorType("1");
            response.setTransactionId(responseMahindra.getTxnid());
            response.setErrorCode(responseMahindra.getTxnstatus());
            response.setErrorMessage(responseMahindra.getMessage());
            response.setCode(TRANSACTION_OK);

        } catch (ServiceException | Exception e) {
            response = Utilidad.generateResponseError(e);
            log.error(MESSAGE_NAME + e.getMessage());
        } finally {
            createComFin = System.currentTimeMillis() - createComIn;
            log.info(LBLTIEMPO + createComFin);

            response.setTransactionTime(null);
            response.setTransactionDate(null);
        }

        return response;
    }

    public final Data cashOutPending(RequestFormat request) {
        Data response = new Data();
        long createComIn = 0;
        long createComFin;
        try {
            createComIn = System.currentTimeMillis();
            Response responseMahindra = this.createRequestBasicData(request);

            if (responseMahindra.getTxnstatus().equals(TRANSACTION_OK)) {

                String requestOut = this.createRequestCashOutPending(request);
                log.info("Request: " + this.createRequestCashOutPendingSinPin(request));

                responseMahindra = this.cashRepository.sendRequestPending(requestOut, request.getPhoneNumber());

                if (responseMahindra.getTxnstatus().equals(TRANSACTION_OK)) {
                    response.setTransactionId(responseMahindra.getTxnid());
                    response.setAmount(request.getAmount());
                    response.setPhoneNumber(request.getPhoneNumber());
                    response.setState("EFFECTIVE");
                    response.setAmount(Integer.parseInt(responseMahindra.getRespuesta()));
                    response.setCode(TRANSACTION_OK);
                    return response;
                }
            }

            response.setErrorType("1");
            response.setErrorCode(responseMahindra.getTxnstatus());
            response.setTransactionId(responseMahindra.getTxnid());
            response.setErrorMessage(responseMahindra.getMessage());
            response.setCode(TRANSACTION_OK);

        } catch (SocketException e) {
            response = new Data();
            log.error(MESSAGE_NAME + e.getMessage());
            response.setErrorType("1");
            response.setErrorCode(TRANSACTION_FATAL);
            response.setCode(TRANSACTION_FATAL);
            response.setErrorMessage(LBLSOCKETEXCEPTION + e.getMessage());
        } catch (IOException e) {
            response = new Data();
            log.error(MESSAGE_NAME + e.getMessage());
            response.setErrorType("1");
            response.setErrorCode(TRANSACTION_FATAL);
            response.setCode(TRANSACTION_FATAL);
            response.setErrorMessage(LBLIOEXCEPTION + e.getMessage());
        } catch (Exception e) {
            response = new Data();
            log.error(MESSAGE_NAME + e.getMessage());
            response.setErrorType("1");
            response.setErrorCode(TRANSACTION_FATAL);
            response.setCode(TRANSACTION_FATAL);
            response.setErrorMessage(LBLEXCEPTION + e.getMessage());
        } catch (ServiceException e) {
            log.error("Error del servidor : {}", e.getMessage());
        } finally {
            createComFin = System.currentTimeMillis() - createComIn;
            log.info(LBLTIEMPO + createComFin);
            response.setTransactionTime(null);
            response.setTransactionDate(null);

        }
        return response;
    }

    public final Data validateSubscriber(RequestFormat request) {
        Data response = new Data();
        long createComIn = 0;
        try {
            createComIn = System.currentTimeMillis();
            Response responseMahindra = this.createRequestBasicData(request);

            if (TRANSACTION_OK.equals(responseMahindra.getTxnstatus())) {
                String requestOut = this.createRequestValidateSusbcriber(request);

                responseMahindra = this.cashRepository.sendMahindraRequest(processResponse(requestOut));
                if (responseMahindra.getTxnstatus().equals(TRANSACTION_OK)) {
                    response.setName(responseMahindra.getFname() + " " + responseMahindra.getLname());
                    response.setState("DESACTIVADO");
                    if (responseMahindra.getBarredtype() != null) {
                        response.setState(responseMahindra.getStatus());
                    }
                    response.setCode(TRANSACTION_OK);
                    return response;
                }
            }

            response = Utilidad.responseError(responseMahindra);

        } catch (ServiceException | Exception e) {
            response = messageError(e);
        } finally {
            response = infoFinally(createComIn, response);
        }
        return response;
    }

    private Data messageError(Throwable e) {
        Data response = new Data();
        log.error(MESSAGE_NAME + e.getMessage());
        response.setErrorType("1");
        response.setErrorCode(TRANSACTION_FATAL);
        response.setCode(TRANSACTION_FATAL);
        response.setErrorMessage(LBLEXCEPTION + e.getMessage());
        return response;
    }


    public final Data validateTransaction(RequestFormat request) {
        Data response = new Data();
        long createComIn = 0;
        try {
            createComIn = System.currentTimeMillis();
            Response responseMahindra = this.createRequestBasicData(request);

            if (responseMahindra.getTxnstatus().equals(TRANSACTION_OK)) {

                String requestOut = this.createRequestValidateTransaction(request);

                responseMahindra = this.cashRepository.sendMahindraRequest(processResponse(requestOut));

                if (responseMahindra.getTxnstatus().equals(TRANSACTION_OK)) {
                    response.setCorrelationId(request.getCorrelationId());
                    response.setState(responseMahindra.getStatusvalue());
                    response.setTransactionId(responseMahindra.getTransid());
                    requestOut = this.createRequestValidateTransactionTI(responseMahindra.getTransid());
                    responseMahindra = this.cashRepository.sendMahindraRequest(processResponse(requestOut));
                    if (responseMahindra.getTxnstatus().equals(TRANSACTION_OK)) {
                        response.setCode(TRANSACTION_OK);
                        response.setAmount(Integer.parseInt(responseMahindra.getTxnamt()));
                    } else {
                        response.setErrorType("1");
                        response.setErrorCode(responseMahindra.getTxnstatus());
                        response.setTransactionId(responseMahindra.getTxnid());
                        response.setErrorMessage(responseMahindra.getMessage());
                        response.setCode(TRANSACTION_ERROR);
                        response.setState(null);
                        response.setCorrelationId(null);
                    }

                } else {
                    response = Utilidad.responseError(responseMahindra);
                }
            } else {
                response = Utilidad.responseError(responseMahindra);
            }

        } catch (ServiceException | Exception e) {
            response = messageError(e);
        } finally {
            response = infoFinally(createComIn, response);
        }

        return response;
    }

    /**
     * metodo infoFinally
     */
    public Data infoFinally(long createComIn, Data response) {
        long createComFin;
        createComFin = System.currentTimeMillis() - createComIn;
        log.info(LBLTIEMPO + createComFin);
        response.setTransactionTime(null);
        response.setTransactionDate(null);
        return response;
    }

    // UTILS METHODS

    /**
     * metodo validateUser
     *
     * @param request
     * @return Data
     */
    public Data validateUser(RequestFormat request) {
        //llamar a mahindra
        try {
            validateCorrelationId(request.getCorrelationId(), request.getMerchantId());
            Network.NetworkDetail networkDetail = network.getOption(request.getAgentCode());
            if (!networkDetail.isEnable()) {
                throw new ProcessingException(statusCodeConfig.of("NETWORK_DISABLED", StatusCode.Level.FAIL));
            }

            //sendLogin al merchant
            Response loginMh = createRequestBasicData(request);
            if (!Constant.CHANNEL.equals(loginMh.getUserType())) {
                throw new ProcessingException(statusCodeConfig.of(Constant.USER_TYPE_NO_AUTHORIZED, StatusCode.Level.FAIL));
            }

            Response usrQryInfoResponse = createRequestUsrQryIfo(request);

            if (!Constant.TRANSACTION_OK.equals(usrQryInfoResponse.getTxnstatus())) {
                throw new ProcessingException(statusCodeConfig.of(Constant.INITIADOR_IS_INVALID, StatusCode.Level.FAIL));
            }

            Data data = new Data();
            data.setCode(Constant.TRANSACTION_OK);
            data.setErrorCode(Constant.TRANSACTION_OK);
            data.setErrorCode(statusCodeConfig.of(Constant.CONSIGNMENT_OK, StatusCode.Level.SUCCESS).getCode());
            data.setMessage(statusCodeConfig.of(Constant.CONSIGNMENT_OK, StatusCode.Level.SUCCESS).getMessage());
            data.setUserName(usrQryInfoResponse.getFnameUsrQryInfo() + " " + (usrQryInfoResponse.getLnameUsrQryInfo()));
            data.setCity(usrQryInfoResponse.getCity());
            data.setGender(usrQryInfoResponse.getGender());
            data.setAmount(request.getAmount());
            return data;
        } catch (ServiceException e) {
            log.error("Error {} ", e.getMessage());
            return Utilidad.generateResponseError(e);
        }
    }

    // Método utilizado para contruir el request del metodo CashIn
    private String createRequestCashIn(RequestFormat requestFormat) {

        String request = HEADER_XML;
        request += "<" + COMMAND + ">";
        request += "<" + LBLTYPE + ">" + this.configurations.getNameCashin() + "</" + LBLTYPE + ">";
        request += "<" + LBLMSISDN + ">" + requestFormat.getUsuario() + "</" + LBLMSISDN + ">";
        request += "<" + LBLMPIN + ">" + requestFormat.getMpin() + "</" + LBLMPIN + ">";
        request += "<" + LBLPIN + ">" + requestFormat.getMpin() + "</" + LBLPIN + ">";
        request += "<" + LBLMSISDN + "2>" + requestFormat.getPhoneNumber() + "</" + LBLMSISDN + "2>";
        request += "<" + LBLAMOUNT + ">" + requestFormat.getAmount() + "</" + LBLAMOUNT + ">";
        request += "<SND" + LBLPROVIDER + ">" + this.configurations.getSndprovider() + "</SND" + LBLPROVIDER + ">";
        request += "<RCV" + LBLPROVIDER + ">" + this.configurations.getRcvprovider() + "</RCV" + LBLPROVIDER + ">";
        request += "<SNDINSTRUMENT>" + this.configurations.getSndinstrument() + "</SNDINSTRUMENT>";
        request += "<RCVINSTRUMENT>" + this.configurations.getRcvinstrument() + "</RCVINSTRUMENT>";
        request += "<" + LBLLANGUAGE + "1>" + this.configurations.getLanguage1() + "</" + LBLLANGUAGE + "1>";
        request += "<" + LBLLANGUAGE + "2>" + this.configurations.getLanguage2() + "</" + LBLLANGUAGE + "2>";
        request += "<" + LBLCELLID + ">" + requestFormat.getPosId() + "</" + LBLCELLID + ">";
        request += "<" + LBLFTXNID + ">" + requestFormat.getCorrelationId() + "</" + LBLFTXNID + ">";
        request += "<" + LBLREMARKS + ">" + requestFormat.getIssuerName() + "|" + requestFormat.getIssuerId() + "|"
                + requestFormat.getMerchantId() + "|" + requestFormat.getIssueDate() + "|" + requestFormat.getPosId() + "|" + requestFormat.getUsuario() + "|" + requestFormat.getPhoneNumber() + "</" + LBLREMARKS + ">";
        request += "</" + COMMAND + ">";

        return request;
    }

    // Método utilizado para contruir el request del metodo CashOutPending
    private String createRequestCashOutPending(RequestFormat requestFormat) {
        String request = HEADER_XML;
        request += "<" + COMMAND + ">";
        request += "<" + LBLTYPE + ">" + this.configurations.getNameCashoutPending() + "</" + LBLTYPE + ">";
        request += "<" + LBLMSISDN + ">" + requestFormat.getUsuario() + "</" + LBLMSISDN + ">";
        request += "<" + LBLPROVIDER + ">" + this.configurations.getProviderCPending() + "</" + LBLPROVIDER + ">";
        request += "<" + LBLMPIN + ">" + requestFormat.getMpin() + "</" + LBLMPIN + ">";
        request += "<" + LBLPIN + ">" + requestFormat.getMpin() + "</" + LBLPIN + ">";
        request += "<" + LBLPAYID + ">" + this.configurations.getPayId() + "</" + LBLPAYID + ">";
        request += "<" + LBLTXNID + ">" + this.configurations.getTxnId() + "</" + LBLTXNID + ">";
        request += "<" + LBLBLOCKSMS + ">" + this.configurations.getBlockSmsPending() + "</" + LBLBLOCKSMS + ">";
        request += "<" + LBLLANGUAGE + "1>" + this.configurations.getLanguage1() + "</" + LBLLANGUAGE + "1>";
        request += "<SERVICE>" + this.configurations.getService() + "</SERVICE>";
        request += "<" + LBLTXNMODE + ">" + this.configurations.getTxnMode() + "</" + LBLTXNMODE + ">";
        request += "<NOOFTXNREQ>" + "" + "</NOOFTXNREQ>";
        request += "</" + COMMAND + ">";

        return request;
    }

    private String createRequestCashOutPendingSinPin(RequestFormat requestFormat) {
        String request = HEADER_XML;
        request += "<" + COMMAND + ">";
        request += "<" + LBLTYPE + ">" + this.configurations.getNameCashoutPending() + "</" + LBLTYPE + ">";
        request += "<" + LBLMSISDN + ">" + requestFormat.getUsuario() + "</" + LBLMSISDN + ">";
        request += "<" + LBLPROVIDER + ">" + this.configurations.getProviderCPending() + "</" + LBLPROVIDER + ">";
        request += "<" + LBLPAYID + ">" + this.configurations.getPayId() + "</" + LBLPAYID + ">";
        request += "<" + LBLTXNID + ">" + this.configurations.getTxnId() + "</" + LBLTXNID + ">";
        request += "<" + LBLBLOCKSMS + ">" + this.configurations.getBlockSmsPending() + "</" + LBLBLOCKSMS + ">";
        request += "<" + LBLLANGUAGE + "1>" + this.configurations.getLanguage1() + "</" + LBLLANGUAGE + "1>";
        request += "<SERVICE>" + this.configurations.getService() + "</SERVICE>";
        request += "<" + LBLTXNMODE + ">" + this.configurations.getTxnMode() + "</" + LBLTXNMODE + ">";
        request += "<NOOFTXNREQ>" + "" + "</NOOFTXNREQ>";
        request += "</" + COMMAND + ">";

        return request;
    }

    // Método utilizado para contruir el request del metodo ValidateSubscriber
    private String createRequestValidateSusbcriber(RequestFormat requestFormat) {

        String request = HEADER_XML;
        request += "<" + COMMAND + ">";
        request += "<" + LBLTYPE + ">" + this.configurations.getType() + "</" + LBLTYPE + ">";
        request += "<" + LBLMSISDN + ">" + requestFormat.getPhoneNumber() + "</" + LBLMSISDN + ">";
        request += "<" + LBLPROVIDER + ">" + this.configurations.getProviderCPending() + "</" + LBLPROVIDER + ">";
        request += "<USERTYPE>" + this.configurations.getUserType() + "</USERTYPE>";
        request += "<" + LBLLANGUAGE + "1>" + this.configurations.getLanguage1() + "</" + LBLLANGUAGE + "1>";
        request += "</" + COMMAND + ">";

        return request;
    }

    // Método utilizado para contruir el request del metodo ValidateTransaction
    private String createRequestValidateTransaction(RequestFormat requestFormat) {

        String request = HEADER_XML;
        request += "<" + COMMAND + ">";
        request += "<" + LBLTYPE + ">" + this.configurations.getNameQueryTransaction() + ("</" + LBLTYPE + ">");
        request += ("<" + LBLFTXNID + ">") + (requestFormat.getCorrelationId()) + ("</" + LBLFTXNID + ">");
        request += ("<MERCHANTCODE>") + (requestFormat.getMerchantId()) + ("</MERCHANTCODE>");
        request += ("<USERTYPE>") + (this.configurations.getUserTypeQt()) + ("</USERTYPE>");
        request += ("<SYSTEM>") + (this.configurations.getSystem()) + ("</SYSTEM>");
        request += "<" + LBLLANGUAGE + "1>" + this.configurations.getLanguage1() + "</" + LBLLANGUAGE + "1>";
        request += ("</" + COMMAND + ">");

        return request;
    }

    // Método utilizado para contruir el request del metodo TransactionTI
    private String createRequestValidateTransactionTI(String txid) {

        String request = HEADER_XML;
        request += "<" + COMMAND + ">";
        request += "<" + LBLTYPE + ">" + this.configurations.getNameQueryTransactionMh() + "</LBLTYPE>";
        request += "<REFERENCEID>" + txid + "</REFERENCEID>";
        request += "<" + LBLLANGUAGE + "1>" + this.configurations.getLanguage1() + "</" + LBLLANGUAGE + "1>";
        request += "</" + COMMAND + ">";

        return request;
    }

    // Método utilizado para contruir el request del metodo BasicData
    private Response createRequestBasicData(RequestFormat requestFormat) throws ServiceException {
        Request loginReq = new Request();
        loginReq.setType(this.configurations.getNameAuthpinreq());
        loginReq.setProvider(this.configurations.getProviderAuth());
        loginReq.setMsisdn(requestFormat.getUsuario());
        loginReq.setMpin(requestFormat.getMpin());
        loginReq.setOtpreq(this.configurations.getOtpReq());
        loginReq.setIspincheckreq(this.configurations.getIsPinCheckReq());
        loginReq.setSource(requestFormat.getSource());

        return this.cashRepository.sendMahindraRequest(loginReq);
    }

    private Response createRequestUsrQryIfo(RequestFormat requestFormat) throws ServiceException {
        Request usrQryInfo = new Request();
        usrQryInfo.setType(this.configurations.getNameUsrQryInfo());
        usrQryInfo.setProvider(this.configurations.getProviderAuth());
        usrQryInfo.setMsisdn(requestFormat.getPhoneNumber());
        usrQryInfo.setUserTypeOne("SUBSCRIBER");
        return this.cashRepository.sendMahindraRequest(usrQryInfo);
    }


    private void validateCorrelationId(String correlationId, String agentCode) throws ServiceException {
        List<CashIn> cashOutCorrelationId = cashInRepositoryRepository.findByCorrelationIdAndAgentCode(correlationId, agentCode);
        if (!cashOutCorrelationId.isEmpty()) {
            throw new ServiceException(ErrorType.DATA, statusCodeConfig.of(INVALID_CORRELATION_ID, StatusCode.Level.FAIL).getCode(), statusCodeConfig.of(INVALID_CORRELATION_ID, StatusCode.Level.FAIL).getMessage());
        }

    }

    private String date() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(new Date());
    }

}

