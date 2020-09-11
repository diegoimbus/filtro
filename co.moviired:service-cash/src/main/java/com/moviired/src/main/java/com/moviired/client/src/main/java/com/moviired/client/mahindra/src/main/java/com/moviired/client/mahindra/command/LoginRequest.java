package com.moviired.client.mahindra.command;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonRootName("Request")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class LoginRequest implements Serializable {

    @JsonProperty("TYPE")
    private String type;

    @JsonProperty("PROVIDER")
    private String provider;

    @JsonProperty("MSISDN")
    private String msisdn;

    @JsonProperty("MPIN")
    private String mpin;

    @JsonProperty("OTPREQ")
    private String otpreq;

    @JsonProperty("ISPINCHEKREQ")
    private String ispincheckreq;

    @JsonProperty("SOURCE")
    private String source;
}

