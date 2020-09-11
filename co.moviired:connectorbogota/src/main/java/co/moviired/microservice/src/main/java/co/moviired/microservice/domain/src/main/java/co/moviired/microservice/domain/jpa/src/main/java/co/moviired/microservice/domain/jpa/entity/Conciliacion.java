package co.moviired.microservice.domain.jpa.entity;

/*
 * Copyright @2020. Movii, SAS. Todos los derechos reservados.
 *
 * @author Oscar Lopez
 * @since 1.1.1
 */

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "bank_conciliation_bcobogota",
        schema = "bank_services",
        indexes = {
                @Index(name = "IDX_REFERENCE_NUMBER", columnList = "reference_number"),
                @Index(name = "IDX_TRANSFER_ID", columnList = "reference_number")
        })
public class Conciliacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true)
    private Integer id;

    @Column(name = "authorization_number")
    private String authorizationNumber;

    @Column(name = "transfer_id")
    private String transferId;

    @Column(name = "nura_code")
    private String nuraCode;

    @Column(name = "reference_number")
    private String referenceNumber;

    @Column(name = "sequence_number")
    private String sequenceNumber;

    @Column(name = "value_to_pay")
    private String valuePay;

}
