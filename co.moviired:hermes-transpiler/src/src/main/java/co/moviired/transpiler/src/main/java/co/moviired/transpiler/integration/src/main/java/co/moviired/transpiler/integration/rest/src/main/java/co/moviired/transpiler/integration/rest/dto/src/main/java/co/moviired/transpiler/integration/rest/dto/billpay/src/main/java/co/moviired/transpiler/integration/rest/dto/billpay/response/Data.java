package co.moviired.transpiler.integration.rest.dto.billpay.response;

import com.fasterxml.jackson.annotation.JsonInclude;
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
        "chargeValue",
        "transactionId",
        "productCode",
        "transactionCode",
        "commisionValue",
        "eanCode",
        "billerCode",
        "shortReferenceNumber",
        "date",
        "bankId",
        "valueToPay",
        "bankTransactionId",
        "processCode",
        "device",
        "convCodigoInterno",
        "posId"
})
public class Data implements Serializable {

    private static final long serialVersionUID = 1496798864361543972L;

    private String billReferenceNumber;

    private String chargeValue;

    private String transactionId;

    private String productCode;

    private String transactionCode;

    private String commisionValue;

    private String eanCode;

    private String billerCode;

    private String shortReferenceNumber;

    private String date;

    private String bankId;

    private String valueToPay;

    private String bankTransactionId;

    private String processCode;

    private String device;

    private String posId;

    private String convCodigoInterno;

}

