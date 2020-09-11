package co.moviired.microservice.domain.response;
/*
 * Copyright @2019. Movii, SAS. Todos los derechos reservados.
 *
 * @author Bejarano, Cindy
 * @version 1, 2019
 * @since 1.0
 */

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;

@lombok.Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@JsonInclude(JsonInclude.Include.NON_NULL)
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
        "transactionType",
        "processCode",
        "device",
        "billReferenceNumber",
        "upcID",
        "balance",
        "valuePartialPayment",
        "comission"
})
@SuppressWarnings("unchecked")
public class Data {

    @JsonProperty("billDueDate")
    private String billDueDate;
    @JsonProperty("transactionCode")
    private String transactionCode;
    @JsonProperty("eanCode")
    private String eanCode;
    @JsonProperty("shortReferenceNumber")
    private String shortReferenceNumber;
    @JsonProperty("billerCode")
    private String billerCode;
    @JsonProperty("date")
    private String date;
    @JsonProperty("hashEchoData")
    private String hashEchoData;
    @JsonProperty("authorizationCode")
    private String authorizationCode;
    @JsonProperty("LabelRef")
    private String labelRef = null;
    @JsonProperty("minValueToPay")
    private String minValueToPay = null;
    @JsonProperty("userId")
    private String userId;
    @JsonProperty("posId")
    private String posId;
    @JsonProperty("multiple")
    private String multiple = null;
    @JsonProperty("echoData")
    private String echoData;
    @JsonProperty("minPaymentValue")
    private String minPaymentValue;
    @JsonProperty("responseCode")
    private String responseCode;
    @JsonProperty("authExternalCode")
    private String authExternalCode = null;
    @JsonProperty("productCode")
    private String productCode;
    @JsonProperty("maxPaymentValue")
    private String maxPaymentValue;
    @JsonProperty("bankId")
    private String bankId;
    @JsonProperty("valueToPay")
    private String valueToPay;
    @JsonProperty("transactionType")
    private String transactionType;
    @JsonProperty("processCode")
    private String processCode;
    @JsonProperty("device")
    private String device;
    @JsonProperty("billReferenceNumber")
    private String billReferenceNumber;
    @JsonProperty("transactionId")
    private String transactionId;
    @JsonProperty("upcID")
    private String upcID;
    @JsonProperty("balance")
    private String balance;
    @JsonProperty("comission")
    private String comission;
    @JsonProperty("valuePartialPayment")
    private String valuePartialPayment;
    @JsonProperty("listObligations")
    private List<Obligation> listObligations;

}

