package co.moviired.support.domain.response.impl;

import co.moviired.support.domain.dto.ConsignmentDetailDTO;
import co.moviired.support.domain.response.IResponseConsignmentSearch;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Copyright @2019 MOVIIRED. Todos los derechos reservados.
 *
 * @author Rodolfo.rivas
 * @category Consinments
 */
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseUserConsignmentSearch implements IResponseConsignmentSearch {

    private static final long serialVersionUID = -8579018286423567390L;

    private String errorType;

    private String errorCode;

    private String errorMessage;

    private List<ConsignmentDetailDTO> consigments;

}

