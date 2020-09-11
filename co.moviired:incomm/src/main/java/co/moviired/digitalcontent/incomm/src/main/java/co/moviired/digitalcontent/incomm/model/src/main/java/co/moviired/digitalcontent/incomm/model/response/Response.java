package co.moviired.digitalcontent.incomm.model.response;

import co.moviired.base.domain.enumeration.ErrorType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.springframework.http.HttpStatus;

import java.io.Serializable;

/*
 * Copyright @2019. MOVIIRED, SAS. Todos los derechos reservados.
 *
 * @author RIVAS, Ronel
 * @version 1, 2019-01-22
 * @since 1.0
 */
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@lombok.Data
@JsonPropertyOrder({
        "data",
        "outcome"
})
public class Response implements Serializable {

    private static final long serialVersionUID = -3999791240749099217L;
    private Data data;
    private Outcome outcome;

    // CONSTRUCTORES

    public Response() {
        super();
    }

    public Response(String codigo, String mensaje, ErrorType perror, HttpStatus status) {
        super();
        ErrorDetail error = new ErrorDetail(perror, codigo, mensaje);
        this.outcome = new Outcome(status, error);
        this.data = null;
    }
}

