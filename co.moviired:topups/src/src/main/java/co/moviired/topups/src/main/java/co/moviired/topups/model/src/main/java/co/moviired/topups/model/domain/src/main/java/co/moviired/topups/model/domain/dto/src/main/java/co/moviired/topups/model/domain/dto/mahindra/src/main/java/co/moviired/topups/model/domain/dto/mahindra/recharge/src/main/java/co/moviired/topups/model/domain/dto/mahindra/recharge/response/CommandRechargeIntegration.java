package co.moviired.topups.model.domain.dto.mahindra.recharge.response;

import co.moviired.topups.model.domain.dto.mahindra.ICommandResponse;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({
        "type",
        "commission",
        "newbalance",
        "transactioncode",
        "authorizationcode",
        "invoicenumber",
        "transactiondate",
        "customerdate",
        "customerbalance",
        "productid",
        "subproduct",
        "expirationdate",
        "amount",
        "txnid",
        "txnstatus",
        "message",
        "trid",
        "ivrresponse"
})
@JsonRootName("command")
public class CommandRechargeIntegration implements ICommandResponse {

    private static final long serialVersionUID = 3852928136092151854L;

    @JsonProperty("TYPE")
    private String type;

    @JsonProperty("COMMISSION")
    private String commission;

    @JsonProperty("NEWBALANCE")
    private String newbalance;

    @JsonProperty("TRANSACTIONCODE")
    private String transactioncode;

    @JsonProperty("AUTHORIZATIONCODE")
    private String authorizationcode;

    @JsonProperty("INVOICENUMBER")
    private String invoicenumber;

    @JsonProperty("TRANSACTIONDATE")
    private String transactiondate;

    @JsonProperty("CUSTOMERDATE")
    private String customerdate;

    @JsonProperty("CUSTOMERBALANCE")
    private String customerbalance;

    @JsonProperty("PRODUCTID")
    private String productid;

    @JsonProperty("SUBPRODUCT")
    private String subproduct;

    @JsonProperty("EXPIRATIONDATE")
    private String expirationdate;

    @JsonProperty("AMOUNT")
    private String amount;

    @JsonProperty("TXNID")
    private String txnid;

    @JsonProperty("TXNSTATUS")
    private String txnstatus;

    @JsonProperty("MESSAGE")
    private String message;

    @JsonProperty("TRID")
    private String trid;

    @JsonProperty("IVR-RESPONSE")
    private String ivrresponse;
}

