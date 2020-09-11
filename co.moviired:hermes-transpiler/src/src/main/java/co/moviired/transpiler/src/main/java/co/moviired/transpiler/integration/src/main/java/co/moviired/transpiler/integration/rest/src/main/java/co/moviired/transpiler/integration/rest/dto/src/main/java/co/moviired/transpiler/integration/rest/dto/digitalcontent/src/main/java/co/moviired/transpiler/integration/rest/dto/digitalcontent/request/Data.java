package co.moviired.transpiler.integration.rest.dto.digitalcontent.request;

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
        "customerDate",
        "email",
        "phoneNumber",
        "customerTxReference",
        "operation",
        "cardSerialNumber"
})
public class Data implements Serializable {

    private static final long serialVersionUID = -6498309817262719675L;

    @JsonProperty("productId")
    private String productId;

    @JsonProperty("EANCode")
    private String eANCode;

    @JsonProperty("amount")
    private Integer amount;

    @JsonProperty("customerDate")
    private String customerDate;

    @JsonProperty("email")
    private String email;

    @JsonProperty("phoneNumber")
    private String phoneNumber;

    @JsonProperty("customerTxReference")
    private String customerTxReference;

    @JsonProperty("operation")
    private String operation;

    @JsonProperty("cardSerialNumber")
    private String cardSerialNumber;

}



