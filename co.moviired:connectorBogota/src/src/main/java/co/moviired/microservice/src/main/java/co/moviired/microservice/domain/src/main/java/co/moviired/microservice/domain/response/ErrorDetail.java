package co.moviired.microservice.domain.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@JsonPropertyOrder({
        "errorMessage",
        "errorType",
        "errorCode"
})
public class ErrorDetail implements Serializable {

    private static final long serialVersionUID = 10L;

    private String errorMessage;
    private Integer errorType;
    private String errorCode;

}

