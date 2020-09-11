package co.moviired.acquisition.model.entity;

import co.moviired.acquisition.model.enums.CurrencyCode;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import javax.persistence.*;

import static co.moviired.acquisition.util.ConstantsHelper.CATEGORY_ID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@Table
public class Product extends StatusAndDateLog {

    // product identifiers *********************************************************************************************

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true)
    private Integer id;

    /**
     * Product id in incomm
     * This field is the UPC value in incomm
     */
    @Column(nullable = false, unique = true)
    private String productCode;

    /**
     * Product id used for redeem pin
     */
    @Column(nullable = false, unique = true)
    private String identifier;

    // Product description *********************************************************************************************

    @Column(nullable = false)
    private String name;

    @Column
    private String description;

    // Product value ***************************************************************************************************

    @Column(nullable = false)
    private Double value;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CurrencyCode currencyCode;

    // Relationships ***************************************************************************************************

    @ManyToOne
    @JoinColumn(name = CATEGORY_ID, nullable = false)
    private Category category;
}

