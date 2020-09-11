package co.moviired.digitalcontent.business.provider.mahindra.response;

import co.moviired.digitalcontent.business.provider.IResponse;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)

@JsonRootName("COMMAND")
public class CommandResponse implements IResponse {

    private static final long serialVersionUID = -3023215829840709823L;

    @JsonProperty("TYPE")
    private String type;

    @JsonProperty("COMMISSION")
    private String commission;

    @JsonProperty("NEWBALANCE")
    private String newbalance;

    @JsonProperty("EXPIRATIONDATE")
    private String expirationdate;

    @JsonProperty("TRANSACTIONCODE")
    private String transactioncode;

    @JsonProperty("CUSTOMERBALANCE")
    private String customerbalance;

    @JsonProperty("INVOICENUMBER")
    private String invoicenumber;

    @JsonProperty("TERMSANDCONDITIONS")
    private String termsandconditions;

    @JsonProperty("AUTHORIZATIONCODE")
    private String authorizationcode;

    @JsonProperty("TRANSACTIONDATE")
    private String transactiondate;

    @JsonProperty("PRODUCTID")
    private String productid;

    @JsonProperty("AMOUNT")
    private String amount;

    @JsonProperty("SUBPRODUCTCODE")
    private String subproductcode;

    @JsonProperty("TXNID")
    private String txnid;

    @JsonProperty("FTXNID")
    private String ftxnid;

    @JsonProperty("FTXNID_ORG")
    private String ftxnidOrg;

    @JsonProperty("TXNID_ORG")
    private String txnidOrg;

    @JsonProperty("TXNSTATUS")
    private String txnstatus;

    @JsonProperty("MESSAGE")
    private String message;

    @JsonProperty("TRID")
    private String trid;

    @JsonProperty("MSISDN")
    private String msisdn;

    @JsonProperty("STATUS")
    private String status;

    @JsonProperty("FIRSTNAME")
    private String fname;

    @JsonProperty("LASTNAME")
    private String lname;

    @JsonProperty("STATUSVALUE")
    private String statusvalue;

    @JsonProperty("USERID")
    private String userid;

    @JsonProperty("USERTYPE")
    private String usertype;

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
    private String odno;

    @JsonProperty("GENDER")
    private String gender;

    @JsonProperty("BIRTHPLACE")
    private String birthplace;

    @JsonProperty("DOB")
    private String dob;

    @JsonProperty("SECURITYQUESTIONSFLAG")
    private String securityquestionsflag;

    @JsonProperty("EMAIL")
    private String email;

    @JsonProperty("LASTLOGIN")
    private String lastlogin;

    @JsonProperty("AGENTCODE")
    private String agentcode;


}

