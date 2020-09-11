package co.moviired.support.domain.response.impl;

import co.moviired.support.domain.response.IResponseConsultBalance;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

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
public class ResponseConsultBalance implements IResponseConsultBalance {

    private static final long serialVersionUID = 1L;

    private String errorType;

    private String errorCode;

    private String errorMessage;

    private String correlationId;

    private String transactionId;

    private Date transactionDate;

    private String balance;
}

