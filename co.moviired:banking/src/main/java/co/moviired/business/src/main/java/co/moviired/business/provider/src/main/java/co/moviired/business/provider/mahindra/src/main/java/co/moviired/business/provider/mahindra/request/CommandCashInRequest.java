package co.moviired.business.provider.mahindra.request;
/*
 * Copyright @2019. Movii, SAS. Todos los derechos reservados.
 *
 * @author Bejarano, Cindy
 * @version 1, 2019
 * @since 1.0
 */

import co.moviired.business.provider.IRequest;
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
        "msisdn",
        "amount",
        "bankId",
        "referenceId",
        "blocksms",
        "txnMode",
        "cellid",
        "ftxn_id",
        "remarks"

})
@JsonRootName("command")
public class CommandCashInRequest implements IRequest {

    private static final long serialVersionUID = 5241732459822225389L;

    private String type;

    private String msisdn;

    private String amount;

    private String bankId;

    private String referenceId;

    private String blocksms;

    private String txnMode;

    private String cellid;

    @JsonProperty("ftxn_id")
    private String ftxnId;

    private String remarks;


}


