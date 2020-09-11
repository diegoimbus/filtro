package co.moviired.microservice.domain.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@lombok.Data
@NoArgsConstructor
@AllArgsConstructor
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

}

