package co.moviired.acquisition.model.entity;

import co.moviired.acquisition.common.model.IModel;
import co.moviired.acquisition.model.enums.TransactionState;
import co.moviired.acquisition.model.enums.TransactionType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import javax.persistence.*;
import java.util.Date;

import static co.moviired.acquisition.common.util.ConstantsHelper.COLUMN_DEFINITION_TINYINT;
import static co.moviired.acquisition.util.ConstantsHelper.PRODUCT_CODE_ID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@Table
public class Transaction extends IModel {

    /**
     * This id is the correlative number assign in transaction
     */
    @Id
    @Column(nullable = false, unique = true)
    private String id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    // Information send for incomm in transaction ***********************************************************************

    @Column
    private String incommRefNum;

    @Column
    private String merchName;

    @Column
    private String merchRefNum;

    @Column
    private Date incommDateTime;

    @Column
    private String incommTimeZone;

    @Column
    private String storeId;

    @Column
    private String terminalId;

    /**
     * This field should be cypher
     */
    @Column
    private String cardNumber;

    @Column
    private Double value;

    @Column
    private String currencyCode;

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

    // Status, date logs and status of transaction log *****************************************************************

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionState state;

    @Column(nullable = false)
    private Date dateTransaction;

    @Column(nullable = false)
    private String respCode;

    // Relationships ***************************************************************************************************

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = PRODUCT_CODE_ID, nullable = false)
    private ProductCode productCode;

    // Methods *********************************************************************************************************

    @Override
    public final String protectedToString() {
        return toJson();
    }


    /**
     * Copy the object instance
     *
     * @param other object to copy
     */
    public Transaction(Transaction other) {
        this.id = other.id;
        this.transactionType = other.transactionType;
        this.incommRefNum = other.incommRefNum;
        this.merchName = other.merchName;
        this.merchRefNum = other.merchRefNum;
        this.dateTransaction = other.dateTransaction;
        this.incommDateTime = other.incommDateTime;
        this.incommTimeZone = other.incommTimeZone;
        this.storeId = other.storeId;
        this.terminalId = other.terminalId;
        this.cardNumber = other.cardNumber;
        this.value = other.value;
        this.currencyCode = other.currencyCode;
        this.state = other.state;
        this.productCode = other.productCode;
        this.respCode = other.respCode;
        this.isAltered = other.isAltered;
        this.signature = other.signature;
        this.signatureVersion = other.signatureVersion;
    }
}

