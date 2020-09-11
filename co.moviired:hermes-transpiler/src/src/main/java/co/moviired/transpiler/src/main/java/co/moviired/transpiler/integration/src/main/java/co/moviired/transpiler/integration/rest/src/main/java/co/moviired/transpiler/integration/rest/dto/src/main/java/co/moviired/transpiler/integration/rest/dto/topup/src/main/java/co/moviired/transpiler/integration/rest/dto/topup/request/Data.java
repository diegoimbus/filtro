package co.moviired.transpiler.integration.rest.dto.topup.request;

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
        "productId",
        "EANCode",
        "amount",
        "destinationNumber",
        "customerDate",
        "customerTxReference"
})
public class Data implements Serializable {

    private static final long serialVersionUID = -6498309817262719675L;

    @JsonProperty("productId")
    private String productId;

    @JsonProperty("EANCode")
    private String eANCode;

    @JsonProperty("amount")
    private String amount;

    @JsonProperty("destinationNumber")
    private String destinationNumber;

    @JsonProperty("customerDate")
    private String customerDate;

    @JsonProperty("customerTxReference")
    private String customerTxReference;

    @JsonProperty("deviceCode")
    private String deviceCode;

    @JsonProperty("customerId")
    private String customerId;

    @JsonProperty("requestDate")
    private String requestDate;

}

