package co.moviired.microservice.domain.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.io.Serializable;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@JsonPropertyOrder({
        "message",
        "statusCode",
        "error"
})
public class Outcome implements Serializable {

    private static final long serialVersionUID = 4L;

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
