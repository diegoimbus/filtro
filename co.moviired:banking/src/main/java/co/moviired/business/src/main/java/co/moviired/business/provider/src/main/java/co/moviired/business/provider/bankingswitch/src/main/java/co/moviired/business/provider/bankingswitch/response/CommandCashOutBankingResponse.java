package co.moviired.business.provider.bankingswitch.response;
/*
 * Copyright @2019. Movii, SAS. Todos los derechos reservados.
 *
 * @author Bejarano, Cindy
 * @version 1, 2019
 * @since 1.0
 */

import co.moviired.business.provider.IResponse;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


@lombok.Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({
        "data",
        "outcome"
})
public class CommandCashOutBankingResponse implements IResponse {
    private Outcome outcome;
    private co.moviired.business.provider.bankingswitch.response.Data data;

}

