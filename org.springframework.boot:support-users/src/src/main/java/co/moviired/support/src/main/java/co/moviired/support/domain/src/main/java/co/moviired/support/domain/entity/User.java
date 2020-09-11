package co.moviired.support.domain.entity;

import co.moviired.support.domain.dto.UserDto;
import co.moviired.support.domain.dto.enums.Gender;
import co.moviired.support.domain.dto.enums.Status;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;


@Data
@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
@Table(name = "user")
public final class User implements Serializable {
    private static final int LENGTH_11 = 11;
    private static final int LENGTH_50 = 50;
    private static final int LENGTH_15 = 15;
    private static final int LENGTH_100 = 100;
    private static final int LENGTH_30 = 30;
    private static final int LENGTH_5 = 5;
    private static final int LENGTH_17 = 17;
    private static final int LENGTH_37 = 37;


    // IDs
    @Id
    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", length = User.LENGTH_11, nullable = false)
    private Integer id;

    @Column(name = "first_name", length = User.LENGTH_50)
    private String firstName;

    @Column(name = "last_name", length = User.LENGTH_50)
    private String lastName;

    @Column(name = "user_login", length = User.LENGTH_50, unique = true)
    private String msisdn;

    @Column(name = "pin", length = User.LENGTH_50)
    private String mpin;

    @Column(name = "user_type", length = User.LENGTH_50)
    private String userType;

    @Column(name = "agent_code", length = User.LENGTH_50)
    private String agentCode;

    @Column(name = "idType", length = User.LENGTH_50)
    private String idtype;

    @Column(name = "idno", length = User.LENGTH_50)
    private String idno;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", length = User.LENGTH_50)
    private Gender gender;

    @Column(name = "dob", length = User.LENGTH_30)
    private String dob;

    @Column(name = "email", length = User.LENGTH_50)
    private String email;

    @Column(name = "cellphone", length = User.LENGTH_15)
    private String cellphone;

    @Column(name = "mahindraUser", length = User.LENGTH_15)
    private String mahindraUser;

    @Column(name = "mahindraPassword", length = User.LENGTH_100)
    private String mahindraPassword;

    @Column(name = "changePasswordRequired", length = User.LENGTH_15)
    private String changePasswordRequired;

    @Column(name = "grade", length = User.LENGTH_15)
    private String grade;

    @Column(name = "walletNumber", length = User.LENGTH_15)
    private String walletNumber;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "lastLogin")
    private Date lastLogin;


    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;

    @Column(name = "signature", nullable = false)
    private int sign;

    @Column(name = "cml_user_id", length = User.LENGTH_5)
    private Integer cmlUserId;

    // Logs parameters
    @JsonIgnore
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "Registration_date", nullable = false)
    private Date registrationDate;

    @JsonIgnore
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updater_date")
    private Date dateUpdate;

    @Column(name = "update_user", length = User.LENGTH_50)
    private String updateUser;

    @Column(name = "create_user", length = User.LENGTH_50)
    private String createUser;

    public UserDto toPublic() {
        UserDto userDto = new UserDto();
        userDto.setFirstName(this.firstName);
        userDto.setLastName(this.lastName);
        userDto.setMsisdn(this.msisdn);
        userDto.setMpin(this.mpin);
        userDto.setUserType(this.userType);
        userDto.setAgentCode(this.agentCode);
        userDto.setIdtype(this.idtype);
        userDto.setIdno(this.idno);
        userDto.setGender(this.gender.name());
        userDto.setDob(this.dob);
        userDto.setEmail(this.email);
        userDto.setCellphone(this.cellphone);
        userDto.setMahindraUser(this.mahindraUser);
        userDto.setMahindraPassword(this.mahindraPassword);
        userDto.setStatus(this.status.name());
        userDto.setChangePasswordRequired(this.changePasswordRequired);
        userDto.setGrade(this.grade);
        userDto.setWalletNumber(this.walletNumber);
        userDto.setLastLogin((this.lastLogin == null) ? null : this.lastLogin.toString());
        userDto.setSign(this.sign);
        userDto.setCmlUserId(this.cmlUserId);
        userDto.setCreateUser(this.createUser);
        userDto.setUpdateUser(this.updateUser);
        return userDto;
    }

    public int hashCode() {
        return new HashCodeBuilder(User.LENGTH_17, User.LENGTH_37).
                append(msisdn).
                append(mpin).
                append(userType).
                append(agentCode).
                append(mahindraUser).
                append(mahindraPassword).
                append(idno).
                append(idtype).
                append(email).
                append(status).
                append(cellphone).
                append(gender).
                toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj;
    }

}

