package co.moviired.support.domain.dto;
/*
 * Copyright @2019. Movii, SAS. Todos los derechos reservados.
 *
 * @author Bejarano, Cindy
 * @version 1, 2019
 * @since 1.0
 */

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.io.Serializable;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class UserDto implements  Serializable {

    private static final long serialVersionUID = -2381315378760910835L;

    private String firstName;
    private String lastName;
    private String msisdn;
    private String mpin;
    private String userType;
    private String agentCode;
    private String idtype;
    private String idno;
    private String gender;
    private String dob;
    private String email;
    private String cellphone;
    private String mahindraUser;
    private String mahindraPassword;
    private String status;
    private String changePasswordRequired;
    private String grade;
    private String userId;
    private String tcp;
    private String walletNumber;
    private String lastLogin;
    private String exempted;
    private int sign;
    private Integer cmlUserId;
    private String createUser;
    private String updateUser;


    public UserDto(UserDto user) {
        this.firstName=user.firstName;
        this.lastName=user.lastName;
        this.msisdn=user.msisdn;
        this.mpin=user.mpin;
        this.userType=user.userType;
        this.agentCode=user.agentCode;
        this.idtype=user.idtype;
        this.idno=user.idno;
        this.gender=user.gender;
        this.dob=user.dob;
        this.email=user.email;
        this.cellphone=user.cellphone;
        this.mahindraUser=user.mahindraUser;
        this.mahindraPassword=user.mahindraPassword;
        this.status=user.status;
        this.changePasswordRequired=user.changePasswordRequired;
        this.grade=user.grade;
        this.walletNumber=user.walletNumber;
        this.lastLogin=user.lastLogin;
        this.exempted=user.exempted;
        this.sign=user.sign;
        this.cmlUserId=user.cmlUserId;
        this.createUser=user.createUser;
        this.updateUser=user.updateUser;
    }
}

