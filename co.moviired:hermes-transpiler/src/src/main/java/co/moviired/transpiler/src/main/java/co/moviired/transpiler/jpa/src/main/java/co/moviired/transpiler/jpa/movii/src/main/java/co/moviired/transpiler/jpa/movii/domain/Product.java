package co.moviired.transpiler.jpa.movii.domain;

import co.moviired.transpiler.jpa.movii.domain.enums.GeneralStatus;
import co.moviired.transpiler.jpa.movii.domain.enums.ProductType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ws_operator")
public class Product implements Serializable {

    private static final long serialVersionUID = -8936285757583036124L;

    private static final int NAME_LENGTH = 500;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true)
    private Integer id;

    @NotNull
    @Builder.Default
    @Enumerated
    @Column(nullable = false)
    private ProductType type = ProductType.OTHER;

    @NotNull
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

    @NotNull
    @Column(name = "min_value", nullable = false)
    private Integer minValue;

    @NotNull
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

    @JsonIgnore
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<ClientProduct> clients;

    @Enumerated
    @Builder.Default
    @Column(nullable = false)
    private GeneralStatus status = GeneralStatus.ENABLED;

}

