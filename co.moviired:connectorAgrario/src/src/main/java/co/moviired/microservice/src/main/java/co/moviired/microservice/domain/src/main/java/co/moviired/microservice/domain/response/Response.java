package co.moviired.microservice.domain.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.io.Serializable;

@lombok.Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@JsonPropertyOrder({
        "data",
        "outcome"
})
public class Response implements Serializable {

    private static final long serialVersionUID = 7L;

    private Outcome outcome;
    private Data data;

    public Response() {
    }

    public Response(Outcome poutcome, Data pdata) {
        this.outcome = poutcome;
        this.data = pdata;
    }

}
