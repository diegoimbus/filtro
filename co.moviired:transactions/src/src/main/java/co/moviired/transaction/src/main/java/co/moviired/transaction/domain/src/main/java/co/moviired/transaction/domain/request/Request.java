package co.moviired.transaction.domain.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Data;

import java.io.Serializable;


@Data
@JsonRootName("COMMAND")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Request implements Serializable {

    private static final long serialVersionUID = 2749750644931916191L;

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

    @JsonProperty("ISPINCHECKREQ")
    private String ispincheckreq;

    @JsonProperty("SOURCE")
    private String source;

}

