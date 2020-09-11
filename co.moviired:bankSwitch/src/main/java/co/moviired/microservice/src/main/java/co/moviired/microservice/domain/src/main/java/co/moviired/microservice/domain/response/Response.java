package co.moviired.microservice.domain.response;
/*
 * Copyright @2019. Movii, SAS. Todos los derechos reservados.
 *
 * @author Bejarano, Cindy
 * @version 1, 2019
 * @since 1.0
 */

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.io.Serializable;

@lombok.Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({
        "data",
        "outcome"
})
public class Response implements Serializable {
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

