package co.moviired.business.provider.integrator.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.io.Serializable;

@lombok.Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({
        "valueToPay",
        "EAN128FullCode",
        "echoData",
        "billerCode",
        "shortReferenceNumber"
})
public class Data implements Serializable {

    private static final long serialVersionUID = 98309817262719675L;

    @JsonProperty("EAN128FullCode")
    private String ean128FullCode;
    private String valueToPay;
    private String echoData;
    private String billerCode;
    private String shortReferenceNumber;

}

