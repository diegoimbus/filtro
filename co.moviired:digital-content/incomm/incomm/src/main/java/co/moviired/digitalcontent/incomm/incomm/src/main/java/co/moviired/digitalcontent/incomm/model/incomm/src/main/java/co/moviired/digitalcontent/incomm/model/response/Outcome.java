package co.moviired.digitalcontent.incomm.model.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
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
@JsonPropertyOrder({
        "message",
        "statusCode",
        "error"
})
@Data
public class Outcome implements Serializable {

    private static final long serialVersionUID = -2474750698502160340L;
    private String message;
    private Integer statusCode;
    private ErrorDetail error;

    // CONSTRUCTORES

    public Outcome() {
        super();
    }

    Outcome(HttpStatus statusCode, ErrorDetail error) {
        super();
        this.statusCode = statusCode.value();
        this.message = statusCode.getReasonPhrase();
        this.error = error;
    }
}

