package co.moviired.microservice.domain.jpa.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity(name = "ws_biller")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Biller implements Serializable {

    private static final long serialVersionUID = 2314L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true)
    private Integer id;

    @Column(name = "ean_code", unique = true)
    private String eanCode;

    @Column(name = "biller_code", nullable = false)
    private String billerCode;

    @Column(nullable = false)
    private String name;

    @Column(name = "product_code", unique = true)
    private String productCode;

    @Column(name = "product_description", length = 500)
    private String productDescription;

    @Column(name = "min_value", nullable = false)
    private Integer minValue;

    @Column(name = "max_value", nullable = false)
    private Integer maxValue;

    @Column(name = "multiple")
    private Integer multiple;

    @Column(name = "partial_payment")
    private Integer partialPayment;

    @Column(name = "overdue_bill")
    private Integer overdueBill;

    @Column(name = "reference_position1")
    private Integer referencePosition1;

    @Column(name = "reference_length1")
    private Integer referenceLength1;

    @Column(name = "value_position1")
    private Integer valuePosition1;

    @Column(name = "value_length1")
    private Integer valueLength1;

    @Column(name = "third_party_code")
    private String internalCode;

}

