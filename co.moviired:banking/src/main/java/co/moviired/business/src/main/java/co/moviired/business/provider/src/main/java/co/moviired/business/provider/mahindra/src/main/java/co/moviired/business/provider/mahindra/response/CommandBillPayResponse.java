package co.moviired.business.provider.mahindra.response;

import co.moviired.business.provider.IResponse;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({
        "type",
        "commission",
        "newbalance",
        "transactionid",
        "bankid",
        "banktransactionid",
        "billercode",
        "shortreferencenumber",
        "valuetopay",
        "chargevalue",
        "commisionvalue",
        "detail",
        "txnid",
        "txnstatus",
        "message",
        "trid"
})
@JsonRootName("command")
public class CommandBillPayResponse implements IResponse {

    private static final long serialVersionUID = -3023215829840709823L;

    @JsonProperty("TYPE")
    private String type;

    @JsonProperty("COMMISSION")
    private String commission;

    @JsonProperty("NEWBALANCE")
    private String newbalance;

    @JsonProperty("TRANSACTIONID")
    private String transactionid;

    @JsonProperty("BANKID")
    private String bankid;

    @JsonProperty("BANKTRANSACTIONID")
    private String banktransactionid;

    @JsonProperty("BILLERCODE")
    private String billercode;

    @JsonProperty("SHORTREFERENCENUMBER")
    private String shortreferencenumber;

    @JsonProperty("VALUETOPAY")
    private String valuetopay;

    @JsonProperty("CHARGEVALUE")
    private String chargevalue;

    @JsonProperty("COMMISIONVALUE")
    private String commisionvalue;

    @JsonProperty("DETAIL")
    private String detail;

    @JsonProperty("TXNID")
    private String txnid;

    @JsonProperty("TXNSTATUS")
    private String txnstatus;

    @JsonProperty("MESSAGE")
    private String message;

    @JsonProperty("TRID")
    private String trid;

}

