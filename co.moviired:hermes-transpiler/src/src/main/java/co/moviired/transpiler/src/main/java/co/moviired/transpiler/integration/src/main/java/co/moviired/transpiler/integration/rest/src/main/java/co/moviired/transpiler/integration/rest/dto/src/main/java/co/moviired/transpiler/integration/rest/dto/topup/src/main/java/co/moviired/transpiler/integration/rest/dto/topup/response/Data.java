package co.moviired.transpiler.integration.rest.dto.topup.response;

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
        "amount",
        "expirationDate",
        "transactionCode",
        "customerBalance",
        "AdditionalInformation",
        "invoiceNumber",
        "resolution",
        "customerDate",
        "subProductCode",
        "authorizationCode",
        "transactionDate",
        "productId"
})
public class Data implements Serializable {

    @JsonProperty("amount")
    private String amount;

    @JsonProperty("expirationDate")
    private String expirationDate;

    @JsonProperty("transactionCode")
    private String transactionCode;

    @JsonProperty("customerBalance")
    private String customerBalance;

    @JsonProperty("AdditionalInformation")
    private String additionalInformation;

    @JsonProperty("invoiceNumber")
    private String invoiceNumber;

    @JsonProperty("resolution")
    private Resolution resolution;

    @JsonProperty("customerDate")
    private String customerDate;

    @JsonProperty("subProductCode")
    private String subProductCode;

    @JsonProperty("authorizationCode")
    private String authorizationCode;

    @JsonProperty("transactionDate")
    private String transactionDate;

    @JsonProperty("productId")
    private String productId;
}

