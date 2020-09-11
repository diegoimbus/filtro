package co.moviired.digitalcontent.incomm.model.response;

import co.moviired.base.domain.enumeration.ErrorType;
import co.moviired.base.domain.exception.ServiceException;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

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
        "errorMessage",
        "errorType",
        "errorCode"
})
@Data
public class ErrorDetail implements Serializable {

    private static final long serialVersionUID = 7785330586421414944L;
    private String errorMessage;
    private Integer errorType;
    private String errorCode;

    // CONSTRUCTORES

    public ErrorDetail() {
        super();
    }

    ErrorDetail(ErrorType perrorType, String perrorCode, String perrorMessage) {
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
        this(se.getErrorType(), se.getCode(), se.getMessage());
    }

}

