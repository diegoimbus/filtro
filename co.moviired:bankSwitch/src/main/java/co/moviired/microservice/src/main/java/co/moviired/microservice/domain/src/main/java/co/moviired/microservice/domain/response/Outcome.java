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
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.io.Serializable;

@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({
        "message",
        "statusCode",
        "error"
})
public class Outcome implements Serializable {

    private static final int OK = 200;
    private static final int BAD_REQUEST = 400;
    private static final int UNAUTHORIZED = 401;
    private static final int FORBIDDEN = 403;
    private static final int REQUEST_TIMEOUT = 408;
    private static final int SERVICE_UNAVAILABLE = 503;
    private String message;
    private Integer statusCode;
    private ErrorDetail error;
    // CONSTRUCTORES

    public Outcome(HttpStatus pstatusCode, ErrorDetail perror) {
        super();
        this.statusCode = pstatusCode.value();
        this.message = perror.getErrorMessage();
        this.error = perror;
    }

    public Outcome(Integer pstatusCode, ErrorDetail perror) {
        super();
        this.error = perror;

        switch (pstatusCode) {
            case OK: {
                this.statusCode = HttpStatus.OK.value();
                this.message = HttpStatus.OK.getReasonPhrase();

            }
            break;
            case BAD_REQUEST: {
                this.statusCode = HttpStatus.BAD_REQUEST.value();
                this.message = HttpStatus.BAD_REQUEST.getReasonPhrase();
            }
            break;

            case UNAUTHORIZED: {
                this.statusCode = HttpStatus.UNAUTHORIZED.value();
                this.message = HttpStatus.UNAUTHORIZED.getReasonPhrase();
            }
            break;

            case FORBIDDEN: {
                this.statusCode = HttpStatus.FORBIDDEN.value();
                this.message = HttpStatus.FORBIDDEN.getReasonPhrase();
            }
            break;

            case REQUEST_TIMEOUT: {
                this.statusCode = HttpStatus.REQUEST_TIMEOUT.value();
                this.message = HttpStatus.REQUEST_TIMEOUT.getReasonPhrase();
            }
            break;

            case SERVICE_UNAVAILABLE: {
                this.statusCode = HttpStatus.SERVICE_UNAVAILABLE.value();
                this.message = HttpStatus.SERVICE_UNAVAILABLE.getReasonPhrase();
            }
            break;

            default: {
                this.statusCode = HttpStatus.INTERNAL_SERVER_ERROR.value();
                this.message = HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase();
            }
        }
    }

    public Outcome() {

    }
}

