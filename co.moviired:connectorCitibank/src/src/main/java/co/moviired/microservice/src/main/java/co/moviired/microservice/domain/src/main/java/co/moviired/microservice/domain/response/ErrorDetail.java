package co.moviired.microservice.domain.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@JsonPropertyOrder({
        "errorMessage",
        "errorType",
        "errorCode"
})
public class ErrorDetail implements Serializable {

    private static final long serialVersionUID = 6L;

    private String errorMessage;
    private Integer errorType;
    private String errorCode;

    public ErrorDetail(Integer perrorType, String perrorCode, String perrorMessage) {
        super();
        this.errorType = perrorType;
        this.errorCode = perrorCode;
        this.errorMessage = perrorMessage;
    }

}

