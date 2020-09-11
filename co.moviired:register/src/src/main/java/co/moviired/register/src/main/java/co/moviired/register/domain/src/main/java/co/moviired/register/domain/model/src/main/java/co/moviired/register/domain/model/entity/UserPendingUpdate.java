package co.moviired.register.domain.model.entity;

import co.moviired.register.domain.BaseModel;
import co.moviired.register.domain.enums.register.Status;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

import static co.moviired.register.helper.ConstantsHelper.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Table(name = REGISTER_TABLE_USER_PENDING_UPDATE)
public class UserPendingUpdate extends BaseModel implements Serializable {

    // ID
    @Id
    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = REGISTER_PARAMETER_ID, length = 11, nullable = false)
    private Integer id;

    // shopName
    @Column(name = REGISTER_PARAMETER_PENDING_UPDATE_IDNO, length = 50)
    private String idno;

    @Column(name = REGISTER_PARAMETER_PENDING_UPDATE_PHONE_NUMBER, length = 15)
    private String phoneNumber;

    // shopName
    @Column(name = REGISTER_PARAMETER_PENDING_UPDATE_SHOP_NAME, length = 50)
    private String shopName;

    // gender
    @Column(name = REGISTER_PARAMETER_PENDING_UPDATE_GENDER, length = 50)
    private String gender;

    // address
    @Column(name = REGISTER_PARAMETER_PENDING_UPDATE_ADDRESS, length = 50)
    private String address;

    // district
    @Column(name = REGISTER_PARAMETER_PENDING_UPDATE_DISTRICT, length = 50)
    private String district;

    // city
    @Column(name = REGISTER_PARAMETER_PENDING_UPDATE_CITY, length = 50)
    private String city;

    // rut
    @Column(name = REGISTER_PARAMETER_PENDING_UPDATE_RUT, length = 50)
    private String rut;

    // digitVerification
    @Column(name = REGISTER_PARAMETER_PENDING_UPDATE_DIGIT_VERIFICATION, length = 10)
    private String digitVerification;

    // activityEconomic
    @Column(name = REGISTER_PARAMETER_PENDING_UPDATE_ACTIVITY_ECONOMIC)
    private String activityEconomic;

    // RegistrationDate
    @JsonIgnore
    @Builder.Default
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = REGISTER_PARAMETER_REGISTRATION_DATE, nullable = false)
    private Date registrationDate = new Date();

    // DateUpdate
    @JsonIgnore
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = REGISTER_PARAMETER_DATE_UPDATE)
    private Date dateUpdate;

    // Status
    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = REGISTER_PARAMETER_STATUS, nullable = false)
    private Status status = Status.PENDING;
}

