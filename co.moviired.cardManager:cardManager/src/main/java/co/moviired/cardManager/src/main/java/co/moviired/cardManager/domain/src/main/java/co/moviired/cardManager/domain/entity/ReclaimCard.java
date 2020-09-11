package co.moviired.cardManager.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@EqualsAndHashCode
@Entity(name = "reclaim_card")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ReclaimCard implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true)
    private Integer id;

    @Column(name = "point_name", nullable = false)
    private String pointName;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "neighborhood", nullable = false)
    private String neighborhood;

    @Column(name = "detail")
    private String detail;

    @Column(name = "city", nullable = false)
    private String city;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Column(name = "document_number", nullable = false)
    private String documentNumber;

    @Column(name = "document_type", nullable = false)
    private String documentType;

    @Column(name = "is_subsidiary", nullable = false)
    private boolean isSubsidiary;

    @Column(name = "card_request_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date requestDate;

    @Column(name = "card_delivery_date", nullable = true)
    @Temporal(TemporalType.TIMESTAMP)
    private Date deliveryDate;

    @Column(name = "card_delivered", nullable = false)
    private boolean cardDelivered;

}

