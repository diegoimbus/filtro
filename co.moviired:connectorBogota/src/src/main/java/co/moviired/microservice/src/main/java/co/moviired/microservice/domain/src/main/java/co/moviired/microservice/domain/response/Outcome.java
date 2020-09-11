package co.moviired.microservice.domain.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.io.Serializable;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@JsonPropertyOrder({
        "message",
        "statusCode",
        "error"
})
public class Outcome implements Serializable {

    private static final long serialVersionUID = 9L;

    private static final int OK = 200;
    private static final int BAD_REQUEST = 400;
    private static final int UNAUTHORIZED = 401;
    private static final int FORBIDDEN = 403;
    private static final int REQUEST_TIMEOUT = 408;
    private static final int SERVICE_UNAVAILABLE = 503;
    private String message;
    private Integer statusCode;
    private ErrorDetail error;

    public Outcome(HttpStatus pstatusCode, ErrorDetail perror) {
        super();
        this.statusCode = pstatusCode.value();
        this.message = perror.getErrorMessage();
        this.error = perror;
    }

}

