package co.moviired.transpiler.integration.rest.dto.cashout.response;

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
        "errorType",
        "errorCode",
        "errorMessage",
        "requestReference",
        "amount",
        "destinationNumber",
        "requestDate",
        "TXND",
        "transactionCode"
})
class Error implements Serializable {

    private static final long serialVersionUID = 5460469422530837163L;

    @JsonProperty("errorType")
    private String errorType;

    @JsonProperty("errorCode")
    private String errorCode;

    @JsonProperty("errorMessage")
    private String errorMessage;

    @JsonProperty("requestReference")
    private String requestReference;

    @JsonProperty("amount")
    private String amount;

    @JsonProperty("destinationNumber")
    private String destinationNumber;

    @JsonProperty("requestDate")
    private String requestDate;

    @JsonProperty("TXND")
    private String txnd;

    @JsonProperty("transactionCode")
    private String transactionCode;

}

