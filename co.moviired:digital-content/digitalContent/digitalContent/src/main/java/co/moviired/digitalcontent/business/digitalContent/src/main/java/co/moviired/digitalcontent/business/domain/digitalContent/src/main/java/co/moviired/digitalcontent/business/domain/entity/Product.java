package co.moviired.digitalcontent.business.domain.entity;

import co.moviired.digitalcontent.business.domain.enums.GeneralStatus;
import co.moviired.digitalcontent.business.domain.enums.SellerChannel;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ws_operator")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Product implements Serializable {

    private static final long serialVersionUID = -8936285757583036124L;

    private static final int NAME_LENGTH = 500;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(name = "type")
    private Integer ttype;
    private transient String type;

    @Column(name = "sub_type")
    private Integer tsubType;
    private transient String subType;

    @Column(name = "operator_id", nullable = false)
    private Integer operatorId;

    @NotNull
    @Column(nullable = false, length = NAME_LENGTH)
    private String name;

    @NotNull
    @Column(name = "product_code", unique = true)
    private String productCode;

    @NotNull
    @Column(name = "ean_code", unique = true)
    private String eanCode;

    @Transient
    private Integer amount;

    @Column(name = "min_value", nullable = false)
    private Integer minValue;

    @Column(name = "max_value", nullable = false)
    private Integer maxValue;

    @Column(name = "reg_exp")
    private String regExp;

    @NotBlank
    @Column(name = "microservice_root", nullable = false)
    private String microserviceRoot;

    @NotBlank
    @Column(name = "microservice_url", nullable = false)
    private String microserviceUrl;

    @Column(name = "product_image")
    private String productImage;

    @Column(name = "tax")
    private String tax;

    @Enumerated
    @Column
    private GeneralStatus status = GeneralStatus.ENABLED;

    @Enumerated(EnumType.STRING)
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "ws_operator_seller", joinColumns = @JoinColumn(name = "operator_id"),
            uniqueConstraints = {@UniqueConstraint(name = "UQ_operator_seller", columnNames = {"operator_id", "sellers"})})
    private Set<SellerChannel> sellers = new HashSet<>();

    public final void toPublic() {
        this.id = null;
        this.category = null;
        this.operatorId = null;
        this.amount = this.minValue;
        this.minValue = null;
        this.maxValue = null;
        this.microserviceRoot = null;
        this.microserviceUrl = null;
        this.ttype = null;
        this.tsubType = null;
        this.sellers = new HashSet<>();
    }
}


