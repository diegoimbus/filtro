package co.moviired.transpiler.jpa.movii.domain.dto.mahindra.billpay.response;

import co.moviired.transpiler.jpa.movii.domain.dto.mahindra.ICommandResponse;
import co.moviired.transpiler.jpa.movii.domain.dto.mahindra.common.response.TransactionDetail;
import com.fasterxml.jackson.annotation.JsonInclude;
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
public class CommandBillPay implements ICommandResponse {

    private static final long serialVersionUID = -3023215829840709823L;

    private String type;

    private String commission;

    private String newbalance;

    private String transactionid;

    private String bankid;

    private String banktransactionid;

    private String billercode;

    private String shortreferencenumber;

    private String valuetopay;

    private String chargevalue;

    private String commisionvalue;

    private List<TransactionDetail> detail;

    private String txnid;

    private String txnstatus;

    private String message;

    private String trid;

}

