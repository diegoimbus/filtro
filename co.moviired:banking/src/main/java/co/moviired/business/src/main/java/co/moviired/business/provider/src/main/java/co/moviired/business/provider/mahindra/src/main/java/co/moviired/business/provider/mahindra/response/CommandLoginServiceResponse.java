package co.moviired.business.provider.mahindra.response;

import co.moviired.business.provider.IResponse;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({
        "type",
        "txnstatus",
        "message",
        "msisdn",
        "userid",
        "usertype",
        "firstname",
        "lastname",
        "provider",
        "langcode",
        "grade",
        "tcp",
        "walletnumber",
        "idtype",
        "idno",
        "email",
        "gender",
        "birthplace",
        "dob",
        "securityquestionsflag",
        "agentcode",
        "trid",
        "lastlogin"
})

@JsonRootName("command")
public class CommandLoginServiceResponse implements IResponse {

    private static final long serialVersionUID = -3023215829840709823L;

    @JsonProperty("TYPE")
    private String type;

    @JsonProperty("TXNSTATUS")
    private String txnstatus;

    @JsonProperty("MESSAGE")
    private String message;

    @JsonProperty("MSISDN")
    private String msisdn;

    @JsonProperty("USERID")
    private String userid;

    @JsonProperty("USERTYPE")
    private String usertype;

    @JsonProperty("FIRSTNAME")
    private String firstname;

    @JsonProperty("LASTNAME")
    private String lastname;

    @JsonProperty("PROVIDER")
    private String provider;

    @JsonProperty("LANGCODE")
    private String langcode;

    @JsonProperty("GRADE")
    private String grade;

    @JsonProperty("TCP")
    private String tcp;

    @JsonProperty("WALLETNUMBER")
    private String walletnumber;

    @JsonProperty("IDTYPE")
    private String idtype;

    @JsonProperty("IDNO")
    private String idno;

    @JsonProperty("EMAIL")
    private String email;

    @JsonProperty("GENDER")
    private String gender;

    @JsonProperty("BIRTHPLACE")
    private String birthplace;

    @JsonProperty("DOB")
    private String dob;

    @JsonProperty("SECURITYQUESTIONSFLAG")
    private String securityquestionsflag;

    @JsonProperty("AGENTCODE")
    private String agentcode;

    @JsonProperty("TRID")
    private String trid;

    @JsonProperty("LASTLOGIN")
    private String lastlogin;

    @JsonProperty("EXEMPTED")
    private String exempted;

}

