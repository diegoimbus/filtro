package co.moviired.transpiler.integration.rest.dto.cashout.response;

import co.moviired.transpiler.integration.rest.dto.IRestResponse;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@lombok.Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({
        "data",
        "outcome"
})
public class ResponseCashOutDTO implements IRestResponse {

    private static final long serialVersionUID = -2343845416096992063L;

    @JsonProperty("data")
    private Data data;

    @JsonProperty("outcome")
    private Outcome outcome;

}

