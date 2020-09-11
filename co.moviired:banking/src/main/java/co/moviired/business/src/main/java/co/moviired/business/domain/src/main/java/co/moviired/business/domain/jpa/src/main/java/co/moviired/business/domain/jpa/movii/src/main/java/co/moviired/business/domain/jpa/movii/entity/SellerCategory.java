package co.moviired.business.domain.jpa.movii.entity;

import co.moviired.business.domain.enums.Seller;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "ws_biller_category_seller")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SellerCategory implements Serializable {

    @Id
    @Column(name = "biller_category_id")
    private String categoryId;

    @Id
    @Column(name = "sellers")
    @Enumerated(EnumType.STRING)
    private Seller seller;

    @ManyToOne
    @JoinColumn(name = "biller_category_id", insertable = false, updatable = false)
    private BillerCategory billerCategory;

}

