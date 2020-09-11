package co.moviired.support.domain.entity;

import co.moviired.support.domain.dto.UserDto;
import co.moviired.support.domain.dto.enums.Gender;
import co.moviired.support.domain.dto.enums.Status;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;


@Data
@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
@Table(name = "user_tmp")
public final class UserTmp implements Serializable {
    private static final int LENGTH_11 = 11 ;
    private static final int LENGTH_50 = 50 ;
    private static final int LENGTH_15 = 15 ;
    private static final int LENGTH_100 = 100 ;
    private static final int LENGTH_30 = 30 ;
    private static final int LENGTH_5 = 5 ;
    // IDs
    @Id
    @Column(name = "id_tmp", length = UserTmp.LENGTH_11, nullable = false)
    private Integer id;

    @Column(name = "first_name", length = UserTmp.LENGTH_50)
    private String firstName;

    @Column(name = "last_name", length = UserTmp.LENGTH_50)
    private String lastName;

    @Column(name = "user_login", length = UserTmp.LENGTH_50)
    private String msisdn;

    @Column(name = "pin", length = UserTmp.LENGTH_50)
    private String mpin;

    @Column(name = "user_type", length = UserTmp.LENGTH_50)
    private String userType;

    @Column(name = "agent_code", length = UserTmp.LENGTH_50)
    private String agentCode;

    @Column(name = "idType", length = UserTmp.LENGTH_50)
    private String idtype;

    @Column(name = "idno", length = UserTmp.LENGTH_50)
    private String idno;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", length = UserTmp.LENGTH_50)
    private Gender gender;

    @Column(name = "dob", length = UserTmp.LENGTH_30)
    private String dob;

    @Column(name = "email", length = UserTmp.LENGTH_50)
    private String email;

    @Column(name = "cellphone", length = UserTmp.LENGTH_15)
    private String cellphone;

    @Column(name = "mahindraUser", length = UserTmp.LENGTH_15)
    private String mahindraUser;

    @Column(name = "mahindraPassword", length = UserTmp.LENGTH_100)
    private String mahindraPassword;

    @Column(name = "changePasswordRequired", length = UserTmp.LENGTH_15)
    private String changePasswordRequired;

    @Column(name = "grade", length = UserTmp.LENGTH_15)
    private String grade;

    @Column(name = "walletNumber", length = UserTmp.LENGTH_15)
    private String walletNumber;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "lastLogin")
    private Date lastLogin;


    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;

    @Column(name = "signature", nullable = false)
    private int sign;

    @Column(name = "cml_user_id", length = UserTmp.LENGTH_5)
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

    @Column(name = "update_user", length = UserTmp.LENGTH_50)
    private String updateUser;

    @Column(name = "create_user", length = UserTmp.LENGTH_50)
    private String createUser;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JsonIgnore
    private User user;

    public UserDto toPublic(){
        UserDto puser= new UserDto();
        puser.setFirstName(this.firstName);
        puser.setLastName(this.lastName);
        puser.setMsisdn(this.msisdn);
        puser.setMpin(this.mpin);
        puser.setUserType(this.userType);
        puser.setAgentCode(this.agentCode);
        puser.setIdtype(this.idtype);
        puser.setIdno(this.idno);
        puser.setGender(this.gender.name());
        puser.setDob(this.dob);
        puser.setEmail(this.email);
        puser.setCellphone(this.cellphone);
        puser.setMahindraUser(this.mahindraUser);
        puser.setMahindraPassword(this.mahindraPassword);
        puser.setStatus(this.status.name());
        puser.setChangePasswordRequired(this.changePasswordRequired);
        puser.setGrade(this.grade);
        puser.setWalletNumber(this.walletNumber);
        puser.setLastLogin((this.lastLogin == null)?null: this.lastLogin.toString());
        puser.setSign(this.sign);
        puser.setCmlUserId(this.cmlUserId);
        puser.setCreateUser(this.createUser);
        puser.setUpdateUser(this.updateUser);
        return puser;
    }

}

