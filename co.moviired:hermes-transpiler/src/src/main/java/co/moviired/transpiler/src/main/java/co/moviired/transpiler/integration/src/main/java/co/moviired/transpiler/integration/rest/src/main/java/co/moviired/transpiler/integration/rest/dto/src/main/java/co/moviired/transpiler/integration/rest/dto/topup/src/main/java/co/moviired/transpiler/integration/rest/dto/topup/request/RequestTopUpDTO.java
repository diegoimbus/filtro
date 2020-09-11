package co.moviired.transpiler.integration.rest.dto.topup.request;

import co.moviired.transpiler.integration.rest.dto.IRestRequest;
import co.moviired.transpiler.integration.rest.dto.common.request.Meta;
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
        "meta",
        "data",
        "requestSignature"
})
public class RequestTopUpDTO implements IRestRequest {

    private static final long serialVersionUID = -6672790264787292336L;

    @JsonProperty("meta")
    private Meta meta;

    @JsonProperty("data")
    private Data data;

    @JsonProperty("requestSignature")
    private RequestSignature requestSignature;

}

