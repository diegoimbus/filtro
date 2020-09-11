package co.moviired.acquisition.model.entity;

import co.moviired.acquisition.common.model.IModel;
import co.moviired.acquisition.model.enums.ProductState;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import javax.persistence.*;
import java.util.Date;

import static co.moviired.acquisition.common.util.ConstantsHelper.COLUMN_DEFINITION_TINYINT;
import static co.moviired.acquisition.util.ConstantsHelper.PRODUCT_ID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@Table
public class ProductCode extends IModel {

    // product identifiers *********************************************************************************************

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true)
    private Integer id;

    /**
     * This is the id used for incomm for activate or deactivate card
     * This field is the PAN value in incomm
     * This code is unique for product
     * This field should be cypher
     */
    @JsonIgnore
    @Column(nullable = false)
    private String cardCode;

    /**
     * This is the id used for user for redeem the code
     * This field is the PIN value in incomm
     * This code is unique for product
     * This field should be cypher
     * This hash is the secure representation of pin, with this field we can find code
     */
    @JsonIgnore
    @Column(nullable = false)
    private String pinHash;

    /**
     * This is the id used for user for redeem the code
     * This field is the PIN value in incomm
     * This code is unique for product
     * This field should be cypher
     * This field can be encrypted, this is used for get pines to print
     */
    @JsonIgnore
    @Column(nullable = false)
    private String pin;

    /**
     * This field indicate the lot of creation
     * This is the correlative assign in transaction of creation
     */
    @JsonIgnore
    @Column(nullable = false)
    private String lotIdentifier;

    // Status and date logs ********************************************************************************************

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ProductState status;

    @JsonIgnore
    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate;

    @Column
    @JsonIgnore
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateDate;

    // Signature values ************************************************************************************************

    @JsonIgnore
    @Column(nullable = false, columnDefinition = COLUMN_DEFINITION_TINYINT)
    private Boolean isAltered;

    @JsonIgnore
    @Column(nullable = false)
    private String signature;

    @JsonIgnore
    @Column(nullable = false)
    private Integer signatureVersion;

    // Relationships ***************************************************************************************************

    @ManyToOne
    @JoinColumn(name = PRODUCT_ID, nullable = false)
    private Product product;

    // Methods *********************************************************************************************************

    @Override
    public final String protectedToString() {
        return toJson("pin", "cardCode");
    }

    /**
     * Copy the object instance
     *
     * @param other object to copy
     */
    public ProductCode(ProductCode other) {
        this.id = other.id;
        this.cardCode = other.cardCode;
        this.pinHash = other.pinHash;
        this.pin = other.pin;
        this.lotIdentifier = other.lotIdentifier;
        this.status = other.status;
        this.creationDate = other.creationDate;
        this.updateDate = other.updateDate;
        this.isAltered = other.isAltered;
        this.signature = other.signature;
        this.signatureVersion = other.signatureVersion;
        this.product = other.product;
    }
}

