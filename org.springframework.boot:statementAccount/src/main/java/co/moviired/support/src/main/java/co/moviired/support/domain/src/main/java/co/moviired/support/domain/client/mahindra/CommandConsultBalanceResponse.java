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
@JsonPropertyOrder({
        "type",
        "txnid",
        "txnstatus",
        "balance",
        "message",
        "frbalance",
        "ficbalance",
        "otherwallets",
        "trid",
        "ivrresponse"

})
@JsonRootName("command")
public class CommandConsultBalanceResponse implements Serializable {


    private static final long serialVersionUID = 6578095125886206454L;

    @JsonProperty("TYPE")
    private String type;

    @JsonProperty("TXNID")
    private String txnid;

    @JsonProperty("TXNSTATUS")
    private String txnStatus;

    @JsonProperty("BALANCE")
    private String balance;

    @JsonProperty("MESSAGE")
    private String message;

    @JsonProperty("FRBALANCE")
    private String frbalance;

    @JsonProperty("FICBALANCE")
    private String ficbalance;

    @JsonProperty("OTHERWALLETS")
    private CommandConsultBalanceOtherWalletsResponse otherwallets;

    @JsonProperty("TRID")
    private String trid;

    @JsonProperty("IVR-RESPONSE")
    private String ivrresponse;

}

