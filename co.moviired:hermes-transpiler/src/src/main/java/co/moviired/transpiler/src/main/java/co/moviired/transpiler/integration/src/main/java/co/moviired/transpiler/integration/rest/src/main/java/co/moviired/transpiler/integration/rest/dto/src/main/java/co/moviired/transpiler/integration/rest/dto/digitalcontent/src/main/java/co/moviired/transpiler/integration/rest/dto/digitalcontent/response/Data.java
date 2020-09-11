package co.moviired.transpiler.integration.rest.dto.digitalcontent.response;

import com.fasterxml.jackson.annotation.JsonAlias;
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
        "idMessage",
        "expirationDate",
        "Message",
        "transactionCode",
        "productCode",
        "customerBalance",
        "invoiceNumber",
        "code",
        "Code",
        "shortReferenceNumber",
        "date",
        "termsAndConditions",
        "authorizationCode",
        "transactionDate",
        "productId",
        "amount",
        "valueToPay",
        "processCode",
        "AdditionalInformation",
        "device",
        "posId",
        "subProductCode",
        "user",
        "pin"
})
public class Data implements Serializable {

    @JsonProperty("idMessage")
    private String idMessage;

    @JsonProperty("expirationDate")
    private String expirationDate;

    @JsonProperty("Message")
    private String message;

    @JsonProperty("transactionCode")
    private String transactionCode;

    @JsonProperty("productCode")
    private String productCode;

    @JsonProperty("customerBalance")
    private String customerBalance;

    @JsonProperty("invoiceNumber")
    private String invoiceNumber;

    @JsonProperty("code")
    private String code;

    @JsonProperty("Code")
    @JsonAlias("code2")
    private String code2;

    @JsonProperty("shortReferenceNumber")
    private String shortReferenceNumber;

    @JsonProperty("date")
    private String date;

    @JsonProperty("termsAndConditions")
    private String termsAndConditions;

    @JsonProperty("authorizationCode")
    private String authorizationCode;

    @JsonProperty("transactionDate")
    private String transactionDate;

    @JsonProperty("productId")
    private String productId;

    @JsonProperty("amount")
    private String amount;

    @JsonProperty("valueToPay")
    private String valueToPay;

    @JsonProperty("processCode")
    private String processCode;

    @JsonProperty("AdditionalInformation")
    private String additionalInformation;

    @JsonProperty("device")
    private String device;

    @JsonProperty("posId")
    private String posId;

    @JsonProperty("subProductCode")
    private String subProductCode;

    @JsonProperty("user")
    private String user;

    @JsonProperty("pin")
    private String pin;
}

