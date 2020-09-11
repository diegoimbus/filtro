package co.moviired.moneytransfer.domain.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;

/*
 * Copyright @2018. MOVIIRED. Todos los derechos reservados.
 *
 * @author Cuadros Cerpa, Cristian Alfonso
 * @version 1, 2018-06-20
 * @since 1.0
 */
@lombok.Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Data {

    private String errorType;
    private String errorCode;
    private String errorMessage;

    private String correlationId;
    private String cashInId;
    private String trId;

    private String transactionDate;
    private Long transactionTime;
    private String message;
    private String transactionId;
    private String cashOutId;
    private Integer amount;
    private String code;
    private String phoneNumber;
    private String state;
    private String name;
    private String agentCode;
    private String userName;
    private String otp;
    private Integer longListCashOut;
}

