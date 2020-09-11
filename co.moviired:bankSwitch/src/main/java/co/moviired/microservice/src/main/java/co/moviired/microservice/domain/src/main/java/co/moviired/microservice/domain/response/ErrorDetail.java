package co.moviired.microservice.domain.response;
/*
 * Copyright @2019. Movii, SAS. Todos los derechos reservados.
 *
 * @author Bejarano, Cindy
 * @version 1, 2019
 * @since 1.0
 */

import co.moviired.microservice.domain.enums.ErrorType;
import co.moviired.microservice.exception.ServiceException;
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

    // CONSTRUCTORES

    public ErrorDetail(ErrorType perrorType, String perrorCode, String perrorMessage) {
        super();
        this.errorType = perrorType.ordinal();
        this.errorCode = perrorCode;
        this.errorMessage = perrorMessage;
    }

    public ErrorDetail(Integer perrorType, String perrorCode, String perrorMessage) {
        super();
        this.errorType = perrorType;
        this.errorCode = perrorCode;
        this.errorMessage = perrorMessage;
    }

    public ErrorDetail(ServiceException se) {
        this(se.getTipo(), se.getCodigo(), se.getMensaje());
    }

    public ErrorDetail() {

    }
}

