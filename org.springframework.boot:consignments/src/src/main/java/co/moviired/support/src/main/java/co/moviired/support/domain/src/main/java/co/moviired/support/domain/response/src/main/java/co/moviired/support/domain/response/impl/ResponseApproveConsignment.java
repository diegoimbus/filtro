package co.moviired.support.domain.response.impl;

import co.moviired.support.domain.response.IResponseApproveConsignment;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Copyright @2019 MOVIIRED. Todos los derechos reservados.
 *
 * @author Rodolfo.rivas
 * @category Consinments
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseApproveConsignment implements IResponseApproveConsignment {

    private static final long serialVersionUID = 9175429982116913523L;

    private String errorType;

    private String errorCode;

    private String errorMessage;

    private String status;

}

