package co.moviired.support.domain.entity.mysql;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Copyright @2019 MOVIIRED. Todos los derechos reservados.
 *
 * @author Rodolfo.rivas
 * @version 1.0.7
 * @category consignment
 */

@Entity
@Data
@Table(name = "csh_consignment")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Consignment implements Serializable {

    private static final long serialVersionUID = 527701445404960468L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", length = 15, nullable = false)
    private Integer id;

    @Column(name = "correlation_id", length = 100, nullable = false)
    private String correlationId;

    @Column(name = "bank_id", length = 20, nullable = false)
    private String bankId;

    @Column(name = "bank_name", length = 100, nullable = false)
    private String bankName;

    @Temporal(TemporalType.DATE)
    @Column(name = "payment_date", nullable = false)
    private Date paymentDate;

    @Column(name = "amount", length = 30, nullable = false)
    private Double amount;

    @Column(name = "process_date", nullable = true)
    private Date processDate;

    @Column(name = "agreement_number", length = 100, nullable = false)
    private String agreementNumber;

    @Column(name = "payment_reference", length = 50, nullable = false)
    private String paymentReference;

    @Column(name = "status", nullable = false)
    private byte status;

    @Column(name = "reason", length = 150, nullable = true)
    private String reason;

    //Consignment_Adrress
    @Column(name = "state", length = 30, nullable = true)
    private String state;

    @Column(name = "city", length = 30, nullable = true)
    private String city;

    @Column(name = "branch_office", length = 100, nullable = true)
    private String branchOffice;

    //Consigment_Mahindra
    @Column(name = "msisdn", length = 10, nullable = false)
    private String msisdn;

    @Column(name = "mahindra_approvement_id", length = 30, nullable = true)
    private String mahindraApprovementId;

    @Column(name = "mahindra_transaction_id", length = 30, nullable = true)
    private String mahindraTransactionId;

    @Builder.Default
    @Column(name = "ip_address", length = 45, nullable = false)
    private String ipAddress = "127.0.0.1";

    @Column(name = "type", nullable = true)
    private byte type;

    @Column(name = "processor_user", length = 50, nullable = true)
    private String processorUser;

    @Column(name = "voucher", nullable = false)
    @Lob
    private byte[] voucher;

    @Column(name = "name_client")
    private String nameClient;

    @Column(name = "name_alliance")
    private String nameAlliance;

    //AUDIT consignacion

    @Column(name = "username_registry")
    private String usernamePortalRegistry;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "registry_date", nullable = false)
    private Date registryDate;

    @Column(name = "username_edit")
    private String usernamePortalEdit;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "edit_date")
    private Date editDate;

    @Column(name = "username_authorizer")
    private String usernamePortalAuthorizer;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "authorizer_date")
    private Date authorizerDate;

}

