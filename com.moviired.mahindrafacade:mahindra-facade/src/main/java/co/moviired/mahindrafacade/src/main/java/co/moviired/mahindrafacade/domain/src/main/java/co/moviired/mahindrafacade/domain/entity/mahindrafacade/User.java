package co.moviired.mahindrafacade.domain.entity.mahindrafacade;

import co.moviired.mahindrafacade.client.mahindra.Response;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;


@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Document
public class User {

    @Id
    private String id;
    private String userId;
    private String msisdn;
    private String idType;
    private String idNo;
    private String firstName;
    private String lastName;
    private String gender;
    private String userType;
    private String email;
    private String modifiedOn;
    private String mpin;
    private String agentCode;
    private String dob;
    private String nacimiento;
    private String cellphone;
    private String sign;
    private String changePasswordRequired;
    private String grade;
    private String tcp;
    private String walletNumber;
    private String lastLogin;
    private String exempted;
    private String cmlUserId;
    private String securityQuestionsFlag;
    private String birthplace;
    private String trid;
    private String langcode;
    private String provider;
    private String txnstatus;
    private String message;
    private String fname;
    private String lname;
    private String emailid;
    private String status;
    private String city;
    private String barredtype;
    private String preflanguage;

    public static User parse(@NotNull Response response) {
        User user = new User();
        user.setMsisdn(response.getMsisdn());
        user.setFirstName(response.getFirstname());
        user.setLastName(response.getLastname());
        user.setGender(response.getGender());
        user.setEmail(response.getEmail());
        user.setIdNo(response.getIdno());
        user.setIdType(response.getIdtype());
        user.setMsisdn(response.getMsisdn());
        user.setDob(response.getDob());
        user.setUserType(response.getUsertype());
        user.setUserId(response.getUserid());
        user.setGrade(response.getGrade());
        user.setTcp(response.getTcp());
        user.setWalletNumber(response.getWalletnumber());
        user.setLastLogin(response.getLastlogin());
        user.setExempted(response.getExempted());
        user.setTxnstatus(response.getTxnstatus());
        user.setProvider(response.getProvider());
        user.setLangcode(response.getLangcode());
        user.setBirthplace(response.getBirthplace());
        user.setSecurityQuestionsFlag(response.getSecurityquestionsflag());
        user.setTxnstatus(response.getTxnstatus());
        user.setMessage(response.getMessage());
        user.setTrid(response.getTrid());
        user.setFname(response.getFirstname());
        user.setLname(response.getLastname());
        user.setCity(response.getCity());
        user.setEmailid(response.getEmail());
        user.setStatus(response.getStatus());
        user.setBarredtype(response.getBarredtype());
        user.setPreflanguage(response.getPreflanguage());
        user.setAgentCode(response.getAgentcode());
        return user;
    }

}


