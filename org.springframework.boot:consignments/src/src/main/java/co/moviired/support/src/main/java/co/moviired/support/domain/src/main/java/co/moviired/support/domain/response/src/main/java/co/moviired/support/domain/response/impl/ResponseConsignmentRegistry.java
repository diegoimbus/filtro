package co.moviired.support.domain.response.impl;

import co.moviired.support.domain.response.IResponseConsignmentRegistry;
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
public class ResponseConsignmentRegistry implements IResponseConsignmentRegistry {

    private static final long serialVersionUID = -8043010822317166864L;

    private String errorType;

    private String errorCode;

    private String errorMessage;

    private String correlationId;

    private String registryDate;

    private String status;


}

