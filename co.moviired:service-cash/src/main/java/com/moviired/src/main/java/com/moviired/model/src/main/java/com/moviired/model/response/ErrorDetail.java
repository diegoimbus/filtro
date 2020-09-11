package com.moviired.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.moviired.excepciones.ManagerException;
import lombok.Data;

/*
 * Copyright @2018. MOVIIRED. Todos los derechos reservados.
 *
 * @author Cuadros Cerpa, Cristian Alfonso
 * @version 1, 2018-06-20
 * @since 1.0
 */
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ErrorDetail {

    private final Integer errorType;
    private final String errorCode;
    private final String errorMessage;

    // CONSTRUCTORES

    public ErrorDetail(Integer perrorType, String perrorCode, String perrorMessage) {
        super();
        this.errorType = perrorType;
        this.errorCode = perrorCode;
        this.errorMessage = perrorMessage;
    }

    public ErrorDetail(ManagerException se) {
        this(se.getTipo(), se.getCodigo(), se.getMessage());
    }

}

