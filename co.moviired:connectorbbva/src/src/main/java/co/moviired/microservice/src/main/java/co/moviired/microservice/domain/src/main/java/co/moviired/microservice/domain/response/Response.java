package co.moviired.microservice.domain.response;


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
public class Response {

    private Outcome outcome;
    private Data data;

    // CONSTRUCTORES

    public Response() {
    }

    public Response(Outcome poutcome, Data pdata) {
        this.outcome = poutcome;
        this.data = pdata;
    }
}

