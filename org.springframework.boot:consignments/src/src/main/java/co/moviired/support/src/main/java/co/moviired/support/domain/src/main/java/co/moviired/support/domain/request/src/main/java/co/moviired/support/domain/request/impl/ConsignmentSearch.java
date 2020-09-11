package co.moviired.support.domain.request.impl;


import co.moviired.support.domain.request.IConsignmentSearch;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

/**
 * Copyright @2019 MOVIIRED. Todos los derechos reservados.
 *
 * @author Rodolfo.rivas
 * @category Consinments
 */
@Data
@Builder
public class ConsignmentSearch implements IConsignmentSearch {

    private static final long serialVersionUID = -3332829323765013605L;

    private String id;
    private String correlationId;
    private String bankId;
    private String bankName;
    private String paymentDate;
    private String amount;
    private String processDate;
    private String agreementNumber;
    private String paymentReference;
    private String status;
    private String reason;
    private String state;
    private String city;
    private String branchOffice;
    private String msisdn;
    private String approvementId;
    private String txnid;
    private String registryDateInit;
    private String registryDateEnd;
    private String usernamePortalRegistry;
    private Date registryDate;
    private String usernamePortalEdit;
    private Date editDate;
    private String usernamePortalAuthorizer;
    private Date authorizerDate;

}

