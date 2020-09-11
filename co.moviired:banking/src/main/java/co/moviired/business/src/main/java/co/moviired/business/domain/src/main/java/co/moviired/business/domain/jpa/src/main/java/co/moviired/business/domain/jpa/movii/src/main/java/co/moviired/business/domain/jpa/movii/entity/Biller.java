package co.moviired.business.domain.jpa.movii.entity;

import co.moviired.business.domain.enums.*;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Data
@Slf4j
@Entity
@Table(name = "ws_biller", indexes = {
        @Index(name = "idxBillerCode", columnList = "biller_code"),
        @Index(name = "idxEanCode", columnList = "ean_code"),
        @Index(name = "idxCategory", columnList = "category_id, state_view, collection_type"),
        @Index(name = "idxName", columnList = "name, product_description, state_view, collection_type"),
})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Biller implements Serializable {

    private static final long serialVersionUID = 9123981636L;

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

    @Column(name = "category_id")
    private Integer categoryId;

    @Column(name = "min_value", nullable = false)
    private Integer minValue;

    @Column(name = "max_value", nullable = false)
    private Integer maxValue;

    @Column(name = "multiple")
    private Integer multiple;

    @Column(name = "partial_payment")
    private Boolean partialPayment;

    @Enumerated
    @Column(name = "collection_type")
    private CollectionType collectionType = CollectionType.BOTH;

    @Column(name = "overdue_bill")
    private Integer overdueBill;

    @Column(name = "reference_position1")
    private Integer referencePosition1;

    @Column(name = "reference_length1")
    private Integer referenceLength1;

    @Column(name = "third_party_code")
    private String internalCode;

    @Column(name = "reg_exp")
    private String regExp;

    @Enumerated
    @Column(name = "weft_type")
    private WeftType weftType;

    @Enumerated
    @Column(name = "modality")
    private Modality modality;

    @Enumerated
    @Column(nullable = false)
    private ItemStatus status = ItemStatus.ENABLED;

    @Column(name = "state_view")
    private Boolean stateView = true;

    @Column(name = "is_special_bill")
    private boolean isSpecialBill = false;

    @Column(name = "only_payment ")
    private boolean onlyPayment;

    @Column(name = "fields")
    private String fields;

    @Transient
    private JsonItemField[] listFields;

    @Column(name = "place_holder")
    private String placeHolder;

    @OneToMany(mappedBy = "biller")
    private List<SellerBiller> listSeller;

    public final void fieldsAsJsonArray() {
        try {
            if (fields != null && !fields.trim().isEmpty()) {
                listFields = new JsonMapper().readValue(fields, JsonItemField[].class);
                fields = null;
            }
        } catch (Exception e) {
            log.error(e.toString());
        }
    }

    public final void toPublic() {
        this.id = null;
        this.productCode = null;
        this.categoryId = null;
        this.minValue = null;
        this.maxValue = null;
        this.multiple = null;
        this.overdueBill = null;
        this.referencePosition1 = null;
        this.referenceLength1 = null;
        this.internalCode = null;
        this.weftType = null;
        this.status = null;
        this.stateView = null;
        this.listSeller = null;
        this.fieldsAsJsonArray();
        if (this.partialPayment == null) {
            this.partialPayment = false;
        }
    }

}

