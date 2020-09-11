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
@Table(name = REGISTER_TABLE_USER_CHANNEL)
public class UserMoviired extends BaseModel implements Serializable {

    // IDs
    @Id
    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = REGISTER_PARAMETER_ID, length = 11, nullable = false)
    private Integer id;

    @Column(name = REGISTER_PARAMETER_FIRST_NAME, length = 50)
    private String firstName;

    @Column(name = REGISTER_PARAMETER_SECOND_NAME, length = 50)
    private String secondName;

    @Column(name = REGISTER_PARAMETER_FIRST_SURNAME, length = 50)
    private String firstSurname;

    @Column(name = REGISTER_PARAMETER_SECOND_SURNAME, length = 50)
    private String secondSurname;

    // User parameters
    @Column(name = REGISTER_PARAMETER_PHONE_NUMBER, length = 15, nullable = false)
    private String phoneNumber;

    @JsonIgnore
    @Column(name = "otp")
    private String otp;

    // Logs parameters
    @JsonIgnore
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = REGISTER_PARAMETER_REGISTRATION_DATE, nullable = false)
    private Date registrationDate;

    @JsonIgnore
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = REGISTER_PARAMETER_DATE_UPDATE)
    private Date dateUpdate;

    // Status
    @Enumerated(EnumType.STRING)
    @Column(name = REGISTER_PARAMETER_STATUS, nullable = false)
    private Status status;

    @JsonIgnore
    @Column(name = REGISTER_PARAMETER_SIGNATURE, nullable = false)
    private String signature;

    @Builder.Default
    @Column(name = "taken")
    private boolean taken = Boolean.FALSE;

    @Column(name = "rut")
    private String rut;

    @Column(name = "digit_verification")
    private String digitVerification;

    @JsonIgnore
    @Column(name = "activity_economic")
    private String activityEconomic;

    /**
     * Add data for create register signature in case of null field
     *
     * @return user instance
     */
    public UserMoviired setFillInNull() {
        if (firstName == null) {
            firstName = "";
        }
        if (firstSurname == null) {
            firstSurname = "";
        }
        if (dateUpdate == null) {
            dateUpdate = registrationDate;
        }
        return this;
    }
}

