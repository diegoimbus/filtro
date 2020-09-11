package co.moviired.topups.model.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * @author carlossaul.ramirez
 * @version 0.0.1
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "ws_operator")
public class Operator {

    @Id
    private Integer id;

    @Transient
    private String environment;

    @Column(name = "microservice_root")
    private String microserviceRoot;

    @Column(name = "microservice_url")
    private String microserviceUrl;

    @Column(name = "min_value")
    private int minValue;

    @Column(name = "max_value")
    private int maxValue;

    private Integer multiple;

    private String name;

    @Column(name = "operator_id")
    private int operatorId;

    @Column(name = "product_code")
    private String productCode;

    @Column(name = "reg_exp")
    private String regExp;

    private int type;

    @Column(name = "ean_code")
    private String eanCode;

    private Integer status;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name="sub_type")
    private SubtypeOperator subType;

    @Column(name = "operator_name")
    private String operatorName;

    @Column(name = "product_image")
    private String productImage;

    @Column(name = "status_view")
    private Integer statusView;

    @Column(name = "details_expiration")
    private String detailsExpiration;

}

