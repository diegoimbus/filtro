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
        "txnid",
        "txnStatus",
        "billccode",
        "billno",
        "bdudate",
        "amount",
        "message"
})
@JsonRootName("command")
public class CommandPayBillBatchResponse implements IResponse {

    @JsonProperty("TYPE")
    private String type;
    @JsonProperty("TXNID")
    private String txnid;
    @JsonProperty("TXNSTATUS")
    private String txnStatus;
    @JsonProperty("BILLCCODE")
    private String billccode;
    @JsonProperty("BDUDATE")
    private String bdudate;
    @JsonProperty("AMOUNT")
    private String amount;
    @JsonProperty("MESSAGE")
    private String message;
    @JsonProperty("TRID")
    private String trid;

}

