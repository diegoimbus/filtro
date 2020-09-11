package co.moviired.transpiler.jpa.movii.domain.dto.mahindra.topup.response;

import co.moviired.transpiler.jpa.movii.domain.dto.mahindra.ICommandResponse;
import co.moviired.transpiler.jpa.movii.domain.dto.mahindra.common.response.TransactionDetail;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
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
        "ivr-response",
        "nooftxn",
        "transdetails"
})
@JsonRootName("command")
public class Command implements ICommandResponse {

    private static final long serialVersionUID = -3023215829840709823L;

    private String type;

    private String commission;

    private String newbalance;

    private String transactioncode;

    private String authorizationcode;

    private String invoicenumber;

    private String transactiondate;

    private String customerdate;

    private String customerbalance;

    private String productid;

    private String subproduct;

    private String expirationdate;

    private String amount;

    private String txnid;

    private String txnstatus;

    private String message;

    private String trid;

    @JsonProperty("ivr-response")
    private String ivrresponse;

    private String nooftxn;

    private List<TransactionDetail> transdetails;

}

