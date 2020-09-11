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
        "walletname",
        "balance",
        "txnstatus",
        "fbalance"

})
@JsonRootName("command")
public class CommandConsultBalanceOtherWalletsResponse implements Serializable {


    private static final long serialVersionUID = 7500068325600247510L;

    public CommandConsultBalanceOtherWalletsResponse(String otherwallets) {}

    @JsonProperty("WALLETNAME")
    private String walletname;

    @JsonProperty("BALANCE")
    private String balance;

    @JsonProperty("FBALANCE")
    private String fbalance;

}

