package co.moviired.support.domain.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * Copyright @2019 MOVIIRED. Todos los derechos reservados.
 *
 * @author Rodolfo.rivas
 * @category Consinments
 */
@Data
@Builder
public class ConsignmentDetailDTO implements Serializable {

    private static final long serialVersionUID = 1203436888219853176L;

    private String correlationId;
    private String id;
    private String bankName;
    private String bankId;
    private String amount;
    private String paymentDate;
    private String agreementNumber;
    private String processDate;
    private String status;
    private String paymentReference;
    private String state;
    private String reason;
    private String branchOffice;
    private String city;
    private String approvementId;
    private String msisdn;
    private String ipAddress;
    private String txnid;
    private String processorUser;
    private String approvementMethod;
    private String nameAlliance;
    private String nameClient;
    private Date registryDate;
    private String usernamePortalRegistry;
    private Date editDate;
    private String usernamePortalEdit;
    private Date authorizerDate;
    private String usernamePortalAuthorizer;

}

