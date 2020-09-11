package com.moviired.client.mahindra.command;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonRootName("Request")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class HoldRequest implements Serializable {

    @JsonProperty("TYPE")
    private String type;

    @JsonProperty("USER_TYPE")
    private String userType;

    @JsonProperty("MSISDN")
    private String msisDn;

    @JsonProperty("RELEASE_AFTER_DAYS")
    private String releaseAfterDays;

    @JsonProperty("PAY_ID")
    private String payId;

    @JsonProperty("HOLD_TXN_ID")
    private String holdTxnId;

    @JsonProperty("LANGUAGE1")
    private String language1;

    @JsonProperty("PROVIDER_ID")
    private String provider;

    @JsonProperty("AMOUNT")
    private String amount;

    @JsonProperty("REMARKS")
    private String remarks;

    @JsonProperty("FTXNID")
    private Long fTxnId;

    @JsonProperty("BLOCKSMS")
    private String blockSms;

    @JsonProperty("PRIORITY_REQUEST_TYPE")
    private String priorityRequestType;

}

