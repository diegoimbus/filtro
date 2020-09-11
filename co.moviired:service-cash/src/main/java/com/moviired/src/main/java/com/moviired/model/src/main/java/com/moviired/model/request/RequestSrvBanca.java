package com.moviired.model.request;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class RequestSrvBanca implements Serializable {

    private String txnId;

    private String phoneNumber;

    private double amount;

    private String otp;

    private Date issueDate;

    private boolean reverse = Boolean.FALSE;

    private String errorDetail;

    private String cardAcceptorId;

    private String merchantCode;

}

