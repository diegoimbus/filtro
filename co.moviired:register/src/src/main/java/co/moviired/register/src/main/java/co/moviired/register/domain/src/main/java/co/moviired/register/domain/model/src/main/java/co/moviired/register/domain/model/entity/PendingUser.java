package co.moviired.register.domain.model.entity;

import co.moviired.register.domain.BaseModel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import static co.moviired.register.helper.ConstantsHelper.*;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Table(name = REGISTER_TABLE_PENDING_USER)
public class PendingUser extends BaseModel implements Serializable {

    @Id
    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = REGISTER_PARAMETER_ID, length = 11, nullable = false)
    private Integer id;

    @Column(name = REGISTER_PARAMETER_PHONE_NUMBER, length = 15)
    private String phoneNumber;

    @Column(nullable = false)
    private String type;

    // Statuses
    @Column(nullable = false)
    private boolean status;

    // Logs parameters
    @JsonIgnore
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = REGISTER_PARAMETER_REGISTRATION_DATE, nullable = false)
    private Date registrationDate;

    @JsonIgnore
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = REGISTER_PARAMETER_DATE_UPDATE)
    private Date dateUpdate;

    @JsonIgnore
    @Column(name = REGISTER_PARAMETER_SIGNATURE, nullable = false, length = 350)
    private String signature;

    @Column(nullable = false)
    private boolean altered;

    @Column
    private String referralCode;

    @Column
    private String documentType;

    @Column
    private Long documentNumber;

    @Column
    private String subsidyCode;

    @Column
    @Enumerated(EnumType.STRING)
    private ProcessType processType;

    @Column
    private boolean infoPersonIsComplete;

    @Column
    private String taken;

    @Column
    private Boolean validationBlackList;

    @Column
    private String phoneNumberHash;

    @Column
    private BigDecimal subsidyValue;

    @Column
    private boolean subsidyApplied;

    @Column
    private String transactionId;

    @Column
    private String statusTransaction;

    public enum ProcessType {
        NORMAL_REGISTRATION,
        SUBSIDY_REGISTRATION
    }
}

