package co.moviired.business.provider.integrator.request;

import co.moviired.business.provider.IRequest;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@lombok.Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({
        "meta",
        "data",
        "requestSignature"
})
public class RequestIntegrator implements IRequest {

    private static final long serialVersionUID = 672790264787292336L;

    private Meta meta;
    private Data data;
    private RequestSignature requestSignature;

}

