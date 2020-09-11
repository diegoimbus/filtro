package co.moviired.transpiler.integration.rest.dto.validatebill.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@lombok.Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({
        "EAN128FullCode",
        "EAN13BillerCode"
})
public class DataValidateBillByEan implements Serializable {

    private static final long serialVersionUID = -6498309817262719675L;

    @JsonProperty(value = "EAN128FullCode")
    private String ean128FullCode;

    @JsonProperty(value = "EAN13BillerCode")
    private String ean13BillerCode;

}

