package com.moviired.client.mahindra.command;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonRootName("COMMAND")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Request implements Serializable {

    @JsonProperty("TYPE")
    private String type;

    @JsonProperty("LANGUAGE1")
    private String language1;

    @JsonProperty("LANGUAGE2")
    private String language2;

    @JsonProperty("SUBTYPE")
    private String subtype;

    @JsonProperty("TXNMODE")
    private String txnMode;

    @JsonProperty("MERCODE")
    private String mercode;

    @JsonProperty("MSISDN")
    private String msisdn;

    @JsonProperty("PROVIDER")
    private String provider;

    @JsonProperty("PAYID2")
    private String payId2;

    @JsonProperty("PAY_ID")
    private String payId;

    @JsonProperty("PAYID")
    private String payIdOne;

    @JsonProperty("PROVIDER2")
    private String provider2;

    @JsonProperty("AMOUNT")
    private String amount;

    @JsonProperty("FTXNID")
    private String ftxnId;

    @JsonProperty("PIN")
    private String pin;

    @JsonProperty("MPIN")
    private String mpin;

    @JsonProperty("USER_TYPE")
    private String userType;

    @JsonProperty("USERTYPE")
    private String userTypeOne;

    @JsonProperty("RELEASE_AFTER_DAYS")
    private String releaseAfterDays;

    @JsonProperty("HOLD_TXN_ID")
    private String holdTxnId;

    @JsonProperty("PROVIDER_ID")
    private String providerId; //hold

    @JsonProperty("REMARKS")
    private String remarks;

    @JsonProperty("BLOCKSMS")
    private String blockSms;

    @JsonProperty("PRIORITY_REQUEST_TYPE")
    private String priorityRequestType;

    @JsonProperty("OTPREQ")
    private String otpreq;

    @JsonProperty("ISPINCHEKREQ")
    private String ispincheckreq;

    @JsonProperty("SOURCE")
    private String source;

    @JsonProperty("FTXNID_ORG")
    private String ftxnIdOrg;

    @JsonProperty("IS_TCP_CHECK_REQ")
    private String isTCPCheckReq;

    @JsonProperty("MSISDN2")
    private String msisdn2;

    @JsonProperty("SNDPROVIDER")
    private String sndProvider;

    @JsonProperty("RCVPROVIDER")
    private String rcvProvider;

    @JsonProperty("SNDINSTRUMENT")
    private String sndInstrument;

    @JsonProperty("RCVINSTRUMENT")
    private String rcvInstrument;

    @JsonProperty("CELLID")
    private String cellId;

}

