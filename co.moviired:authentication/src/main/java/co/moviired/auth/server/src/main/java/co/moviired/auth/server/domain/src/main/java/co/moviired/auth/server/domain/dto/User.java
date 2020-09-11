package co.moviired.auth.server.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.io.Serializable;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class User implements Serializable {

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
    private String nacimiento;
    private String email;
    private Profile profile;
    private String cellphone;
    private String mahindraUser;
    private String mahindraPassword;
    private String status;
    private String sign;
    private String changePasswordRequired;
    private String userId;
    private String grade;
    private String tcp;
    private String walletNumber;
    private String lastLogin;
    private String exempted;
    private Integer cmlUserId;
    private String createUser;
    private String updateUser;

}

