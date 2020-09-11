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
        "billReferenceNumber",
        "billDueDate",
        "EAN13BillerCode",
        "minPartialPayment",
        "maxPaymentValue",
        "billerCode",
        "hashEchoData",
        "payAfterDueDate",
        "billerName",
        "bankId",
        "valueToPay",
        "partialPayment",
        "LabelRef",
        "helpOnline",
        "multiple",
        "echoData",
        "minPaymentValue"
})
public class DataValidateByEan implements Serializable {

    private static final long serialVersionUID = -6498309817262719675L;

    private String minPaymentValue;
    private String minPartialPayment;
    private String echoData;
    private String billerCode;
    private String helpOnline;
    private String payAfterDueDate;
    private String valueToPay;
    private String bankId;
    private String billerName;
    private String hashEchoData;
    private String partialPayment;
    private String maxPaymentValue;
    private String multiple;
    private String billDueDate;
    private String billReferenceNumber;

    @JsonProperty("LabelRef")
    private String labelRef;

    @JsonProperty("EAN13BillerCode")
    private String ean13BillerCode;
}

