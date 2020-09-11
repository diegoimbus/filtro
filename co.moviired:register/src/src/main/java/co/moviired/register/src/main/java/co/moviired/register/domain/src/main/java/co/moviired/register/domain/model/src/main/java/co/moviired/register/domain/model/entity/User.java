package co.moviired.register.domain.model.entity;

import co.moviired.register.domain.BaseModel;
import co.moviired.register.domain.enums.ado.AdoCaseStatus;
import co.moviired.register.domain.enums.register.ActiveStatus;
import co.moviired.register.domain.enums.register.Status;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

import static co.moviired.register.helper.ConstantsHelper.*;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Table(name = REGISTER_TABLE_USER)
public class User extends BaseModel implements Serializable {

    // IDs
    @Id
    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = REGISTER_PARAMETER_ID, length = 11, nullable = false)
    private Integer id;

    @Column(name = REGISTER_PARAMETER_ADO_TRANSACTION_ID, length = 2, nullable = false)
    private Integer adoTransactionId;

    // Statuses
    @Column(name = REGISTER_PARAMETER_STATUS, length = 1, nullable = false)
    private Integer status;

    @JsonIgnore
    @Column(name = REGISTER_PARAMETER_IS_ACTIVE, length = 1, nullable = false)
    private Integer isActive;

    @Column(name = REGISTER_PARAMETER_ADO_STATUS, length = 2, nullable = false)
    private Integer adoStatus;

    // User parameters
    @Column(name = REGISTER_PARAMETER_PHONE_NUMBER, length = 15, nullable = false)
    private String phoneNumber;

    @Column(name = REGISTER_PARAMETER_PHONE_SERIAL_NUMBER, length = 50, nullable = false)
    private String phoneSerialNumber;

    @Column(name = REGISTER_PARAMETER_IDENTIFICATION_TYPE_ID, length = 2)
    private Integer identificationTypeId;

    @Column(name = REGISTER_PARAMETER_IDENTIFICATION_NAME, length = 50)
    private String identificationName;

    @Column(name = REGISTER_PARAMETER_IDENTIFICATION_NUMBER, length = 15)
    private String identificationNumber;

    @Column(name = REGISTER_PARAMETER_FIRST_NAME, length = 50)
    private String firstName;

    @Column(name = REGISTER_PARAMETER_SECOND_NAME, length = 50)
    private String secondName;

    @Column(name = REGISTER_PARAMETER_FIRST_SURNAME, length = 50)
    private String firstSurname;

    @Column(name = REGISTER_PARAMETER_SECOND_SURNAME, length = 50)
    private String secondSurname;

    @Column(name = REGISTER_PARAMETER_GENDER, length = 1)
    private String gender;

    @Column(name = REGISTER_PARAMETER_BIRTH_DATE)
    private String birthDate;

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
    @Column(name = REGISTER_PARAMETER_SIGNATURE, nullable = false)
    private String signature;

    @Column(name = REGISTER_PARAMETER_PROCESS, nullable = false)
    private Integer process;

    @Column(name = REGISTER_PARAMETER_IS_ORDINARY_DEPOSIT_FORM_COMPLETE, nullable = false, columnDefinition = "TINYINT")
    private boolean isOrdinaryDepositFormCompleted;

    public void setIsActiveEnum(ActiveStatus isActiveEnum) {
        setIsActive(isActiveEnum.getId());
    }

    private void setIsActive(Integer pIsActive) {
        this.isActive = pIsActive;
    }


    // Status
    @JsonProperty(STATUS_ENUM)
    public Status getStatusEnum() {
        return status == null ? Status.UNKNOWN : Status.getById(getStatus());
    }

    public void setStatusEnum(Status statusEnum) {
        setStatus(statusEnum.getId());
    }

    private void setStatus(Integer pStatus) {
        this.status = pStatus;
    }

    // ADO Status

    public void setAdoCaseStatus(AdoCaseStatus adoCaseStatus) {
        setAdoStatus(adoCaseStatus.getId());
    }

    public void setAdoStatus(Integer pAdoStatus) {
        this.adoStatus = pAdoStatus;
    }

    /**
     * Add data for create register signature in case of null field
     *
     * @return user instance
     */
    public User setFillInNull() {
        if (identificationTypeId == null) {
            identificationTypeId = -1;
        }
        if (identificationName == null) {
            identificationName = "";
        }
        if (identificationNumber == null) {
            identificationNumber = "";
        }
        if (firstName == null) {
            firstName = "";
        }
        if (secondName == null) {
            secondName = "";
        }
        if (firstSurname == null) {
            firstSurname = "";
        }
        if (secondSurname == null) {
            secondSurname = "";
        }
        if (gender == null) {
            gender = "";
        }
        if (birthDate == null) {
            birthDate = "";
        }
        if (dateUpdate == null) {
            dateUpdate = registrationDate;
        }
        return this;
    }
}

