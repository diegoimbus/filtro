package co.moviired.business.provider.integrator.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.io.Serializable;

@lombok.Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "valueToPay",
        "shortReferenceNumber",
        "billerCode",
        "categoryId",
        "EAN13BillerCode",
        "EAN128FullCode",
        "billReferenceNumber",
        "echoData",
        "transferId",
        "billDueDate",
        "transactionCode",
        "eanCode",
        "date",
        "hashEchoData",
        "authorizationCode",
        "userId",
        "posId",
        "minPaymentValue",
        "responseCode",
        "productCode",
        "maxPaymentValue",
        "bankId",
        "partialPayment",
        "transactionType",
        "processCode",
        "device",
        "minPartialPayment",
        "payAfterDueDate",
        "billerName",
        "helpOnline",
        "transactionId",
        "bankTransactionId",
        "Message",
        "idMessage",
        "minValueToPay"

})
public class Data implements Serializable {

    @JsonProperty("valueToPay")
    private String valueToPay;

    @JsonProperty("shortReferenceNumber")
    private String shortReferenceNumber;

    @JsonProperty("billerCode")
    private String billerCode;

    @JsonProperty("categoryId")
    private String categoryId;

    @JsonProperty("EAN13BillerCode")
    private String ean13Billercode;

    @JsonProperty("EAN128FullCode")
    private String ean128Fullcode;

    @JsonProperty("billReferenceNumber")
    private String billReferenceNumber;

    @JsonProperty("echoData")
    private String echoData;

    @JsonProperty("transferId")
    private String transferId;

    @JsonProperty("billDueDate")
    private String billDueDate;

    @JsonProperty("transactionCode")
    private String transactionCode;

    @JsonProperty("eanCode")
    private String eanCode;

    @JsonProperty("date")
    private String date;

    @JsonProperty("hashEchoData")
    private String hashEchoData;

    @JsonProperty("authorizationCode")
    private String authorizationCode;

    @JsonProperty("userId")
    private String userId;

    @JsonProperty("posId")
    private String posId;

    @JsonProperty("minPaymentValue")
    private String minPaymentValue;

    @JsonProperty("responseCode")
    private String responseCode;

    @JsonProperty("maxPaymentValue")
    private String maxPaymentValue;

    @JsonProperty("productCode")
    private String productCode;

    @JsonProperty("bankId")
    private String bankId;

    @JsonProperty("partialPayment")
    private String partialPayment;

    @JsonProperty("transactionType")
    private String transactionType;

    @JsonProperty("processCode")
    private String processCode;

    @JsonProperty("device")
    private String device;

    @JsonProperty("minPartialPayment")
    private String minPartialPayment;

    @JsonProperty("payAfterDueDate")
    private String payAfterDueDate;

    @JsonProperty("billerName")
    private String billerName;

    @JsonProperty("helpOnline")
    private String helpOnline;

    //Se agrego para pruebas de convenio Codensa directo
    @JsonProperty("transactionId")
    private String transactionId;

    @JsonProperty("bankTransactionId")
    private String bankTransactionId;

    @JsonProperty("chargeValue")
    private String chargeValue;

    @JsonProperty("commisionValue")
    private String commisionValue;

    @JsonProperty("detail")
    private String detail;

    @JsonProperty("convCodigoInterno")
    private String convCodigoInterno;

    @JsonProperty("multiple")
    private String multiple;

    @JsonProperty("LabelRef")
    private String labelRef;

    @JsonProperty("code")
    private String code;

    @JsonProperty("Message")
    private String message;

    @JsonProperty("idMessage")
    private String idMessage;

    @JsonProperty("user")
    private String user;

    @JsonProperty("connectorId")
    private String connectorId;

    @JsonProperty("minValueToPay")
    private String minValueToPay;

}

