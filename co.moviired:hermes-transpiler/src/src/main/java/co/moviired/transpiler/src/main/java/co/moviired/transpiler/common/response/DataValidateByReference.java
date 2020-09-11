package co.moviired.transpiler.common.response;

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
        "billDueDate",
        "transactionCode",
        "eanCode",
        "shortReferenceNumber",
        "billerCode",
        "date",
        "hashEchoData",
        "authorizationCode",
        "LabelRef",
        "minValueToPay",
        "userId",
        "posId",
        "multiple",
        "echoData",
        "minPaymentValue",
        "responseCode",
        "authExternalCode",
        "productCode",
        "maxPaymentValue",
        "bankId",
        "valueToPay",
        "partialPayment",
        "transactionType",
        "processCode",
        "device"
})
public class DataValidateByReference implements Serializable {

    private static final long serialVersionUID = -6498309817262719675L;

    private String device;
    private String processCode;
    private String transactionType;
    private String partialPayment;
    private String valueToPay;
    private String bankId;
    private String authorizationCode;
    private String maxPaymentValue;
    private String productCode;
    private String minPaymentValue;
    private String echoData;
    private String multiple;
    private String posId;
    private String userId;
    private String responseCode;
    private String authExternalCode;
    private String minValueToPay;
    private String hashEchoData;
    private String date;
    private String billerCode;
    private String shortReferenceNumber;
    private String eanCode;
    private String transactionCode;
    private String billDueDate;

    @JsonProperty("LabelRef")
    private String labelRef;

}

