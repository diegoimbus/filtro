package co.moviired.support.domain.request.impl;


import co.moviired.support.domain.request.IConsignmentSearch;
import lombok.Data;

/**
 * Copyright @2019 MOVIIRED. Todos los derechos reservados.
 *
 * @author Rodolfo.rivas
 * @category Consinments
 */
@Data
public class UserConsignmentSearch implements IConsignmentSearch {

    private static final long serialVersionUID = -5151290145486907425L;

    private String msisdn;

    private String correlationId;

    private String bankId;

    private String bankName;

    private String agreementNumber;

    private String paymentReference;

    private String status;

    private String registryDateInit;

    private String registryDateEnd;

}

