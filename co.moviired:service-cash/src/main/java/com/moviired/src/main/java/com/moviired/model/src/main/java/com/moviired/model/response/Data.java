package com.moviired.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

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

    private String code;
    private String errorType;
    private String errorCode;
    private String errorMessage;
    private String correlationId;
    private String cashInId;
    private String trId;
    private String message;
    private String transactionId;
    private String cashOutId;
    private String transactionDate;
    private String phoneNumber;
    private String state;
    private String name;
    private String agentCode;
    private String userName;
    private String otp;
    private String city;
    private String gender;
    private Long transactionTime;
    private Integer amount;
    private Integer longListCashOut;
    private List<CashOutResponse> cashOutList;

}

