package co.moviired.transpiler.integration.rest.dto.billpay.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;


@lombok.Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({
        "billerCode",
        "shortReferenceNumber",
        "EAN13BillerCode",
        "billReferenceNumber",
        "echoData",
        "hashEchoData",
        "valueToPay"
})
public class Data implements Serializable {

    private static final long serialVersionUID = -6498309817262719675L;

    private String billerCode;

    private String shortReferenceNumber;

    @JsonProperty("EAN13BillerCode")
    private String ean13BillerCode;

    private String billReferenceNumber;

    @NotBlank
    private String echoData;

    @NotBlank
    private String hashEchoData;

    private String valueToPay;

}

