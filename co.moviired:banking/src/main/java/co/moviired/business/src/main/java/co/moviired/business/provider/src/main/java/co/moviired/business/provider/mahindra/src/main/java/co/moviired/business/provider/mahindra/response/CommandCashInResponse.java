package co.moviired.business.provider.mahindra.response;
/*
 * Copyright @2019. Movii, SAS. Todos los derechos reservados.
 *
 * @author Bejarano, Cindy
 * @version 1, 2019
 * @since 1.0
 */

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
        "txnstatus",
        "txnstatuseig",
        "msisdn",
        "date",
        "time",
        "amount",
        "nextlevel",
        "trid"
})

@JsonRootName("command")
public class CommandCashInResponse implements IResponse {

    private static final long serialVersionUID = -3023215829840709823L;

    @JsonProperty("TYPE")
    private String type;

    @JsonProperty("TXNID")
    private String txnid;

    @JsonProperty("TXNSTATUS")
    private String txnstatus;

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

}

