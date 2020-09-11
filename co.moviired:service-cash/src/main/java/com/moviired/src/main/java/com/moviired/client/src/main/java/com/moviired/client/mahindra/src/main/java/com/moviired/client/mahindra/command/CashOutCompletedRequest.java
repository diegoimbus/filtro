package com.moviired.client.mahindra.command;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonRootName("Request")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class CashOutCompletedRequest implements Serializable {

    @JsonProperty("TYPE")
    private String type;

    @JsonProperty("LANGUAGE1")
    private String language1;

    @JsonProperty("LANGUAGE2")
    private String language2;

    @JsonProperty("BLOCKSMS")
    private String blocksms;

    @JsonProperty("SUBTYPE")
    private String subtype;

    @JsonProperty("TXNMODE")
    private String txnMode;

    @JsonProperty("MERCODE")
    private String mercode;

    @JsonProperty("PROVIDER")
    private String provider;

    @JsonProperty("PAYID2")
    private String payId2;

    @JsonProperty("PAYID")
    private String payId;

    @JsonProperty("PROVIDER2")
    private String provider2;

    @JsonProperty("AMOUNT")
    private String amount;

    @JsonProperty("MSISDN")
    private String msisDn;

    @JsonProperty("FTXNID")
    private String fTxnId;

    @JsonProperty("PIN")
    private String pin;

    @JsonProperty("MPIN")
    private String mPin;


}

