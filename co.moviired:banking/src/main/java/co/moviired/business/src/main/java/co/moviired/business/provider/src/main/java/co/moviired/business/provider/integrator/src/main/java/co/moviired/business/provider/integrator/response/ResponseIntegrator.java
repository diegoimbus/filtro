package co.moviired.business.provider.integrator.response;

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
public class ResponseIntegrator implements IResponse {

    private co.moviired.business.provider.integrator.response.Data data;
    private Outcome outcome;

}



