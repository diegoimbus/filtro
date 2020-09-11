package com.moviired.model.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.moviired.excepciones.ManagerException;
import com.moviired.helper.Constant;
import com.moviired.helper.Utilidad;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

/*
 * Copyright @2018. MOVIIRED. Todos los derechos reservados.
 *
 * @author Cuadros Cerpa, Cristian Alfonso
 * @version 1, 2018-06CODE_ERROR0
 * @since 1.0
 */

@Slf4j
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public final class RequestFormat {

    private static final String LBLCORRELATIONID = "No se encontró el campo correlationId.";
    private static final String LBLISSUEDATA = "No se encontró el campo issueData.";
    private static final String LBLAMOUNT = "No se encontró el campo amount.";
    private static final String LBLAMOUNTVALIDO = "Debe ingresar un amount válido.";
    private static final String LBLPHONENUMBER = "El phoneNumber debe ser numérico.";
    private static final String LBLPHONENUMBERVALIDO = "Debe ingresar un phoneNumber válido.";
    private static final String LBLMERCHANT = "merchantId inválido.";
    private static final String LBLPOST = "posId inválido.";
    private static final String LBLPARAMETROS = "Error de parámetros.";
    private static final Long MULTIPLE = 10000L;
    private static final int CODE_ERROR = -2;
    private static final int CODE_ERROR_3 = -3;
    private static final int CODE_ERROR_4 = -4;
    private static final int CODE_ERROR_5 = -5;
    private static final int CODE_ERROR_6 = -6;

    private String phoneNumber;

    private String merchantId;

    private String posId;

    private String mpin;

    private String usuario;

    private Integer amount;

    private String correlationId;

    private String correlationIdR;

    private String issueDate;

    private String issuerName;

    private String issuerId;

    private String transactionId;

    private String otp;


    private String agentCode;

    private String network;

    private String source;

    public RequestFormat() {
        super();
    }

    public static RequestFormat vCashIn(RequestFormat requestFormat,
                                        String phoneNumber,
                                        String autorization,
                                        String merchantId,
                                        String posId) throws ManagerException {

        if ((requestFormat.getCorrelationId() == null) || (requestFormat.getCorrelationId().isEmpty())) {
            throw new ManagerException(CODE_ERROR, Constant.TRANSACTION_ERROR, LBLCORRELATIONID);
        }

        if ((requestFormat.getIssueDate() == null) || (requestFormat.getIssueDate().isEmpty())) {
            throw new ManagerException(CODE_ERROR, Constant.TRANSACTION_ERROR, LBLISSUEDATA);
        }

        if ((requestFormat.getIssuerName() == null) || (requestFormat.getIssuerName().isEmpty())) {
            requestFormat.setIssuerName("");
        }

        validateAmount(requestFormat);
        validatePhoneNumber(requestFormat, phoneNumber);
        validatePostMerch(requestFormat, merchantId, posId);
        validateAuthorization(requestFormat, autorization);

        return requestFormat;
    }

    public static RequestFormat vCashOutC(RequestFormat requestFormat,
                                          String phoneNumber,
                                          String autorization,
                                          String merchantId,
                                          String posId) throws ManagerException {
        validateFields(requestFormat);
        validateAmount(requestFormat);
        validatePhoneNumber(requestFormat, phoneNumber);
        validatePostMerch(requestFormat, merchantId, posId);
        validateAuthorization(requestFormat, autorization);

        return requestFormat;
    }

    private static void validateFields(RequestFormat requestFormat) throws ManagerException {
        if (requestFormat.getCorrelationId() == null) {
            throw new ManagerException(CODE_ERROR, Constant.TRANSACTION_ERROR, LBLCORRELATIONID);
        }

        if (requestFormat.getIssueDate() == null) {
            throw new ManagerException(CODE_ERROR, Constant.TRANSACTION_ERROR, LBLISSUEDATA);
        }

        if (requestFormat.getIssuerId() == null) {
            throw new ManagerException(CODE_ERROR, Constant.TRANSACTION_ERROR, "No se encontró el campo issuerId.");
        }

        if (requestFormat.getOtp() == null) {
            throw new ManagerException(CODE_ERROR, Constant.TRANSACTION_ERROR, "No se encontró el campo otp.");
        }

        if (requestFormat.getTransactionId() == null) {
            throw new ManagerException(CODE_ERROR, Constant.TRANSACTION_ERROR, "No se encontró el campo transactionId.");
        }
    }

    private static void validateAmount(RequestFormat requestFormat) throws ManagerException {
        if ((requestFormat.getAmount() == null)) {
            throw new ManagerException(CODE_ERROR, Constant.TRANSACTION_ERROR, LBLAMOUNT);
        }
    }

    private static void validateAmountAval(RequestFormat requestFormat) throws ManagerException {
        if ((requestFormat.getAmount() == null)) {
            throw new ManagerException(CODE_ERROR, Constant.TRANSACTION_ERROR, LBLAMOUNT);
        }
    }

    private static void validatePhoneNumber(RequestFormat requestFormat, String phoneNumber) throws ManagerException {
        if (Utilidad.isInteger(phoneNumber)) {
            if (Long.parseLong(phoneNumber) <= 0) {
                throw new ManagerException(-1, Constant.TRANSACTION_ERROR, LBLPHONENUMBER);
            } else {
                requestFormat.phoneNumber = phoneNumber;
            }
        } else {
            throw new ManagerException(-1, Constant.TRANSACTION_ERROR, LBLPHONENUMBERVALIDO);
        }
    }

    private static void validatePostMerch(RequestFormat requestFormat, String merchantId, String posId) throws ManagerException {
        if (!merchantId.trim().matches("")) {
            requestFormat.merchantId = merchantId;
        } else {
            throw new ManagerException(CODE_ERROR, Constant.TRANSACTION_ERROR, LBLMERCHANT);
        }

        if (!posId.trim().matches("")) {
            requestFormat.posId = posId;
        } else {
            throw new ManagerException(CODE_ERROR, Constant.TRANSACTION_ERROR, LBLPOST);
        }
    }

    private static void validateAuthorization(RequestFormat requestFormat, String autorization) throws ManagerException {
        if (!autorization.trim().matches("")) {
            try {
                String[] vautorization = autorization.split(":");
                if (!vautorization[0].trim().matches("")) {
                    if (!vautorization[1].trim().matches("")) {
                        requestFormat.usuario = vautorization[0];
                        requestFormat.mpin = vautorization[1];
                    } else {
                        throw new ManagerException(CODE_ERROR_3, Constant.TRANSACTION_ERROR, LBLPARAMETROS);
                    }
                } else {
                    throw new ManagerException(CODE_ERROR_4, Constant.TRANSACTION_ERROR, LBLPARAMETROS);
                }

            } catch (Exception e) {
                throw new ManagerException(CODE_ERROR_5, Constant.TRANSACTION_ERROR, LBLPARAMETROS);
            }

        } else {
            throw new ManagerException(CODE_ERROR_6, Constant.TRANSACTION_ERROR, LBLPARAMETROS);
        }
    }

    public static RequestFormat vCashOutI(RequestFormat requestFormat,
                                          String autorization,
                                          String merchantId,
                                          String posId) throws ManagerException {

        validateFieldsCOI(requestFormat);
        validateAmount(requestFormat);
        validatePostMerch(requestFormat, merchantId, posId);
        validateAuthorization(requestFormat, autorization);

        return requestFormat;
    }

    public static RequestFormat vCashOutIAval(RequestFormat requestFormat,
                                              String autorization,
                                              String merchantId,
                                              String posId) throws ManagerException {

        validateFieldsCOI(requestFormat);
        validateAmountAval(requestFormat);
        validateAuthorization(requestFormat, autorization);
        validatePostMerch(requestFormat, merchantId, posId);

        return requestFormat;
    }

    private static void validateFieldsCOI(RequestFormat requestFormat) throws ManagerException {
        if (requestFormat.getCorrelationId() == null) {
            throw new ManagerException(CODE_ERROR, Constant.TRANSACTION_ERROR, LBLCORRELATIONID);
        }

        if (requestFormat.getIssueDate() == null) {
            throw new ManagerException(CODE_ERROR, Constant.TRANSACTION_ERROR, LBLISSUEDATA);
        }

        if (requestFormat.getIssuerName() == null) {
            requestFormat.setIssuerName("");
        }
    }

    public static RequestFormat vCashReverso(RequestFormat requestFormat, String autorization,
                                             String phoneNumber) throws ManagerException {

        if (requestFormat.getCorrelationId() == null) {
            throw new ManagerException(CODE_ERROR, Constant.TRANSACTION_ERROR, LBLCORRELATIONID);
        }

        if (requestFormat.getCorrelationIdR() == null) {
            throw new ManagerException(CODE_ERROR, Constant.TRANSACTION_ERROR, "No se encontró el campo correlationIdR.");
        }

        validatePhoneNumber(requestFormat, phoneNumber);
        validateAmount(requestFormat);
        validateAuthorization(requestFormat, autorization);

        return requestFormat;
    }

    public static RequestFormat vCashOutP(String phoneNumber,
                                          String autorization,
                                          String merchantId,
                                          String posId) throws ManagerException {

        RequestFormat requestFormat = new RequestFormat();

        validatePhoneNumber(requestFormat, phoneNumber);
        validatePostMerch(requestFormat, merchantId, posId);
        validateAuthorization(requestFormat, autorization);

        return requestFormat;
    }

    public static RequestFormat vSubscriber(
            String phoneNumber,
            String autorization,
            String merchantId,
            String posId) throws ManagerException {

        return vCashOutP(phoneNumber, autorization, merchantId, posId);
    }

    public static RequestFormat vTransaction(
            String correlationId,
            String autorization,
            String merchantId,
            String posId) throws ManagerException {

        RequestFormat requestFormat = new RequestFormat();

        if (Utilidad.isInteger(correlationId)) {
            if (Long.parseLong(correlationId) <= 0) {
                throw new ManagerException(CODE_ERROR, Constant.TRANSACTION_ERROR, "Debe ingresar un correlationId válido.");
            } else {
                requestFormat.correlationId = correlationId;
            }
        } else {
            throw new ManagerException(CODE_ERROR, Constant.TRANSACTION_ERROR, "El correlationId debe ser numerico.");
        }

        validatePostMerch(requestFormat, merchantId, posId);
        validateAuthorization(requestFormat, autorization);

        return requestFormat;
    }

    public static RequestFormat parse(RequestSrvBanca req, String msisdn, String pin) {
        RequestFormat rf = new RequestFormat();
        rf.amount = (int) req.getAmount();
        rf.otp = req.getOtp();
        rf.phoneNumber = req.getPhoneNumber();
        rf.issueDate = new Date().toString();
        rf.agentCode = req.getCardAcceptorId();
        rf.correlationId = req.getTxnId();
        rf.correlationIdR = null;
        rf.issuerId = null;
        rf.issuerName = null;
        rf.merchantId = req.getMerchantCode();
        rf.posId = null;
        rf.transactionId = null;

        // Usuario
        rf.usuario = msisdn;
        rf.mpin = pin;

        return rf;
    }
}
