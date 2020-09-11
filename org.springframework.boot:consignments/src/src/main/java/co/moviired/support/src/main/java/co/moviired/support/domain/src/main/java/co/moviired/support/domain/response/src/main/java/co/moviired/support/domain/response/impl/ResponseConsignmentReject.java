package co.moviired.support.domain.response.impl;

import co.moviired.support.domain.response.IResponseConsignmentReject;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

/**
 * Copyright @2019 MOVIIRED. Todos los derechos reservados.
 *
 * @author Rodolfo.rivas
 * @category Consinments
 */
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseConsignmentReject implements IResponseConsignmentReject {


    private static final long serialVersionUID = -7849411975799212370L;

    private String errorType;

    private String errorCode;

    private String errorMessage;

    private String correlationId;

    private String status;

}

