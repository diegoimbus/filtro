package co.moviired.support.domain.client.mahindra;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Copyright @2019 MOVIIRED. Todos los derechos reservados.
 *
 * @author Rodolfo.rivas
 * @category Consinments
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({"type", "txnid", "txnstatus", "txnstatuseig", "msisdn", "date", "time", "amount", "nextlevel",
        "trid"

})
@JsonRootName("command")
public class CommandApproveConsignmentResponse implements Serializable {

    private static final long serialVersionUID = 6198149977886592818L;

    @JsonProperty("TYPE")
    private String type;

    @JsonProperty("TXNID")
    private String txnid;

    @JsonProperty("TXNSTATUS")
    private String txnStatus;

    @JsonProperty("TXNSTATUSEIG")
    private String txnstatuseig;

    @JsonProperty("MSISDN")
    private String msisdn;

    @JsonProperty("DATE")
    private String date;

    @JsonProperty("TIME")
    private String time;

    @JsonProperty("AMOUNT")
    private String amount;

    @JsonProperty("NEXTLEVEL")
    private String nextlevel;

    @JsonProperty("TRID")
    private String trid;

    @JsonProperty("MESSAGE")
    private String message;
}

