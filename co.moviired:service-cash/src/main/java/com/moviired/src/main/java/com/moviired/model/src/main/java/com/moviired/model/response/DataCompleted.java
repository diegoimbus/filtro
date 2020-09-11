package com.moviired.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/*
 * Copyright @2018. MOVIIRED. Todos los derechos reservados.
 *
 * @author Cuadros Cerpa, Cristian Alfonso
 * @version 1, 2018-06-20
 * @since 1.0
 */
@lombok.Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class DataCompleted {

    //@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss", locale = "es_CO")
    private String transactionDate;
    private Long transactionTime;
    private String message;
    private String transactionId;
    private String cashInId;
    private String correlationId;
    private String amount;
    private String errorType;
    private String errorCode;
    private String errorMessage;
    private String code;
    private String phoneNumber;
    private String state;
    private String name;
    private String agentCode;
    private String userName;

    public DataCompleted(Data data) {
        super();
        this.transactionDate = data.getTransactionDate();
        this.transactionTime = data.getTransactionTime();
        this.message = data.getMessage();
        this.transactionId = data.getTransactionId();
        this.cashInId = data.getCashInId();
        this.correlationId = data.getCorrelationId();
        this.amount = String.valueOf(data.getAmount());
        this.errorType = data.getErrorType();
        this.errorCode = data.getErrorCode();
        this.errorMessage = data.getErrorMessage();
        this.code = data.getCode();
        this.phoneNumber = data.getPhoneNumber();
        this.state = data.getState();
        this.name = data.getName();
        this.agentCode = data.getAgentCode();
        this.userName = data.getUserName();
    }
}

