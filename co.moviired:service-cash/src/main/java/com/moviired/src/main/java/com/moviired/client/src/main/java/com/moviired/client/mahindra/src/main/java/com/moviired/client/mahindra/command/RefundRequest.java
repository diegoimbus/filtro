package com.moviired.client.mahindra.command;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonRootName("Request")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RefundRequest implements Serializable {

    @JsonProperty("TYPE")
    private String type;

    @JsonProperty("FTXNID")
    private String ftxnId;

    @JsonProperty("FTXNID_ORG")
    private String ftxnIdOrg;

    @JsonProperty("AMOUNT")
    private Integer amount;

    @JsonProperty("IS_TCP_CHECK_REQ")
    private String isTCPCheckReq;

}

