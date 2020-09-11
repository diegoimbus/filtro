package co.moviired.transpiler.integration.rest.dto.topup.response;

import co.moviired.transpiler.integration.rest.dto.IRestResponse;
import co.moviired.transpiler.integration.rest.dto.common.response.Outcome;
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
public class ResponseTopUpDTO implements IRestResponse {

    @JsonProperty("data")
    private Data data;

    @JsonProperty("outcome")
    private Outcome outcome;

}

