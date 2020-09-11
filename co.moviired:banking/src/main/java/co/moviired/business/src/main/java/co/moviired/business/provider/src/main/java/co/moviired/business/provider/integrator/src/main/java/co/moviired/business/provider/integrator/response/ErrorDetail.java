package co.moviired.business.provider.integrator.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({
        "errorMessage",
        "errorType",
        "errorCode"
})
public class ErrorDetail implements Serializable {

    private String errorMessage;
    private Integer errorType;
    private String errorCode;

}

