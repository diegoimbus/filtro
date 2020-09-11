package com.moviired.model.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;


@Data
@Slf4j
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class CashOutRequest {

    private String issueDate;

    private String correlationId;

    private String source;

    private String issuerName;

    private String issuerId;

    private String ip;

    private String documentType;

    private String documentNumber;

    private String userLogin;

    private String pin;

    private String agentCode;

    private String txnId;

    private String otp;

    private String token;

    private String phoneNumber;

    private String identificationNumber;

    private Integer transactionId;

    private Integer cashOutId;

    private Integer amount;

    private String posId;

    private String conciliationDate;

}

