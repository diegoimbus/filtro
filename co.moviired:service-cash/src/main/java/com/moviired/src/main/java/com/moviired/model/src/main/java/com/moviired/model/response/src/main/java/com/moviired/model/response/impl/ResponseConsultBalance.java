package com.moviired.model.response.impl;

import com.moviired.model.response.IResponseConsultBalance;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * Copyright @2019 MOVIIRED. Todos los derechos reservados.
 *
 * @author carlossaul.ramirez
 * @category srv-cash
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
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

