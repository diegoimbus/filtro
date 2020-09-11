package co.moviired.mahindrafacade.client.mahindra;

import co.moviired.mahindrafacade.domain.entity.mahindrafacade.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@Slf4j
@Data
@AllArgsConstructor
@NoArgsConstructor
@XmlRootElement(name = "COMMAND")
@XmlAccessorType(XmlAccessType.FIELD)
public final class Response implements Serializable {

    private static final long serialVersionUID = 4512597599852333050L;

    @XmlElement(name = "TYPE")
    private String type;

    @XmlElement(name = "MSISDN")
    private String msisdn;

    @XmlElement(name = "USERID")
    private String userid;

    @XmlElement(name = "USERTYPE")
    private String usertype;

    @XmlElement(name = "FIRSTNAME")
    private String firstname;

    @XmlElement(name = "LASTNAME")
    private String lastname;

    @XmlElement(name = "PROVIDER")
    private String provider;

    @XmlElement(name = "LANGCODE")
    private String langcode;

    @XmlElement(name = "GRADE")
    private String grade;

    @XmlElement(name = "TCP")
    private String tcp;

    @XmlElement(name = "WALLETNUMBER")
    private String walletnumber;

    @XmlElement(name = "IDTYPE")
    private String idtype;

    @XmlElement(name = "IDNO")
    private String idno;

    @XmlElement(name = "GENDER")
    private String gender;

    @XmlElement(name = "STATUS")
    private String status;

    @XmlElement(name = "BIRTHPLACE")
    private String birthplace;

    @XmlElement(name = "DOB")
    private String dob;

    @XmlElement(name = "SECURITYQUESTIONSFLAG")
    private String securityquestionsflag;

    @XmlElement(name = "EMAIL")
    private String email;

    @XmlElement(name = "LASTLOGIN")
    private String lastlogin;

    @XmlElement(name = "EXEMPTED")
    private String exempted;

    @XmlElement(name = "TXNSTATUS")
    private String txnstatus;

    @XmlElement(name = "MESSAGE")
    private String message;

    @XmlElement(name = "TRID")
    private String trid;

    @XmlElement(name = "TXNSTATUSMF")
    private String txnstatusmf;

    @XmlElement(name = "MESSAGEMF")
    private String messagemf;

    @XmlElement(name = "AGENTCODE")
    private String agentcode;

    @XmlElement(name = "FNAME")
    private String fname;

    @XmlElement(name = "LNAME")
    private String lname;

    @XmlElement(name = "EMAILID")
    private String emailid;

    @XmlElement(name = "CITY")
    private String city;

    @XmlElement(name = "BARREDTYPE")
    private String barredtype;

    @XmlElement(name = "PREFLANGUAGE")
    private String preflanguage;

    public static Response parse(User user) {
        Response response = new Response();
        response.setMsisdn(user.getMsisdn());
        response.setFirstname(user.getFirstName());
        response.setLastname(user.getLastName());
        response.setGender(user.getGender());
        response.setEmail(user.getEmail());
        response.setIdno(user.getIdNo());
        response.setIdtype(user.getIdType());
        response.setMsisdn(user.getMsisdn());
        response.setDob(user.getDob());
        response.setUsertype(user.getUserType());
        response.setUserid(user.getUserId());
        response.setGrade(user.getGrade());
        response.setTcp(user.getTcp());
        response.setWalletnumber(user.getWalletNumber());
        response.setLastlogin(user.getLastLogin());
        response.setExempted(user.getExempted());
        response.setProvider(user.getProvider());
        response.setLangcode(user.getLangcode());
        response.setBirthplace(user.getBirthplace());
        response.setSecurityquestionsflag(user.getSecurityQuestionsFlag());
        response.setTxnstatus(user.getTxnstatus());
        response.setMessage(user.getMessage());
        response.setTrid(user.getTrid());
        response.setFname(user.getFname());
        response.setLname(user.getLname());
        response.setEmailid(user.getEmailid());
        response.setStatus(user.getStatus());
        response.setCity(user.getCity());
        response.setBarredtype(user.getBarredtype());
        response.setPreflanguage(user.getPreflanguage());
        response.setTrid(user.getTrid());
        return response;
    }

}

