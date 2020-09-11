package co.moviired.support.domain.client.mahindra;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

import static co.moviired.support.util.ConstantsHelper.ASTERISK;
import static co.moviired.support.util.ConstantsHelper.REGEX_ALL;

@Slf4j
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonRootName("COMMAND")
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class MahindraDTO implements Serializable {

    private static final long serialVersionUID = 7500068325600247510L;

    @JsonProperty("TYPE")
    private String type;

    @JsonProperty("BANKID")
    private String bankId;

    @JsonProperty("MSISDN")
    private String msisdn;

    @JsonProperty("AMOUNT")
    private String amount;

    @JsonProperty("FTXNID")
    private String ftxId;

    @JsonProperty("REFERENCEID")
    private String referenceId;

    @JsonProperty("MSISDN2")
    private String msisdn2;

    @JsonProperty("IDNO")
    private String idNo;

    @JsonProperty("MPIN")
    private String mPin;

    @JsonProperty("PIN")
    private String pin;

    @JsonProperty("SNDPROVIDER")
    private String sdnProvider;

    @JsonProperty("RCVPROVIDER")
    private String rcvProvider;

    @JsonProperty("SNDINSTRUMENT")
    private String sdnInstrument;

    @JsonProperty("RCVINSTRUMENT")
    private String rcvInstrument;

    @JsonProperty("BLOCKSMS")
    private String blockSms;

    @JsonProperty("TXNMODE")
    private String txnMode;

    @JsonProperty("LANGUAGE1")
    private String language1;

    @JsonProperty("LANGUAGE2")
    private String language2;

    @JsonProperty("CELLID")
    private String cellId;

    @JsonProperty("REMARKS")
    private String remarks;

    @JsonProperty("TXNID")
    private String txnId;

    @JsonProperty("TXNSTATUS")
    private String txnStatus;

    @JsonProperty("MESSAGE")
    private String message;

    @JsonProperty("FTXNID_ORG")
    private String ftxnIdOrg;

    @JsonProperty("TXNID_ORG")
    private String txnIdOrg;

    @JsonProperty("TRID")
    private String trId;

    @JsonProperty("TXNSTATUSEIG")
    private String txnStatusEig;

    @JsonProperty("DATE")
    private String date;

    @JsonProperty("TIME")
    private String time;

    @JsonProperty("NEXTLEVEL")
    private String nextLevel;

    @JsonProperty("ISPINCHECKREQ")
    private String isPinCheckReq;

    @JsonProperty("OTPREQ")
    private String otpReq;

    @JsonProperty("PROVIDER")
    private String provider;

    @JsonProperty("SOURCE")
    private String source;

    @JsonProperty("PAYID")
    private String payId;

    @JsonProperty("USERID")
    private String userId;

    @JsonProperty("USERTYPE")
    private String userType;

    @JsonProperty("NATIONALIDTYPE")
    private String nationalIdType;

    @JsonProperty("IDNUMBER")
    private String idNumber;

    @JsonProperty("REGTYPEID")
    private String regTypeId;

    @JsonProperty("ISSUEDATE")
    private String issueDate;

    @JsonProperty("LOGINID")
    private String loginId;

    @JsonProperty("FNAME")
    private String fName;

    @JsonProperty("LNAME")
    private String lName;

    @JsonProperty("EMAILID")
    private String emailId;

    @JsonProperty("DOB")
    private String dob;

    @JsonProperty("CITY")
    private String city;

    @JsonProperty("GENDER")
    private String gender;

    @JsonProperty("STATUS")
    private String status;

    @JsonProperty("BARREDTYPE")
    private String barredType;

    @JsonProperty("PREFLANGUAGE")
    private String prefLanguage;

    @JsonProperty("FIRSTNAME")
    private String firstName;

    @JsonProperty("LASTNAME")
    private String lastName;

    @JsonProperty("LANGCODE")
    private String langCode;

    @JsonProperty("GRADE")
    private String grade;

    @JsonProperty("TCP")
    private String tcp;

    @JsonProperty("WALLETNUMBER")
    private String walletNumber;

    @JsonProperty("IDTYPE")
    private String idType;

    @JsonProperty("BIRTHPLACE")
    private String birthPlace;

    @JsonProperty("SECURITYQUESTIONSFLAG")
    private String securityQuestionsFlag;

    @JsonProperty("EMAIL")
    private String email;

    @JsonProperty("LASTLOGIN")
    private String lastLogin;

    @JsonProperty("MSISDN1")
    private String msisdn1;

    @JsonProperty("PHONENUMBER")
    private String phoneNumber;

    @JsonProperty("CUSTOMERDATE")
    private String customerDate;

    @JsonProperty("PAYMENT_INSTRUMENT")
    private String paymentInstrument;

    @JsonProperty("BPROVIDER")
    private String bProvider;

    @JsonProperty("PAYID2")
    private String payId2;

    @JsonProperty("PAYMENTTYPE")
    private String paymentType;

    @JsonProperty("PROVIDER2")
    private String provider2;

    @JsonProperty("OPERATORID")
    private String operatorId;

    @JsonProperty("OPERATORNAME")
    private String operatorName;

    @JsonProperty("PRODUCTID")
    private String productId;

    @JsonProperty("EANCODE")
    private String eanCode;

    @JsonProperty("EAN13BILLERCODE")
    private String ean13BillerCode;

    @JsonProperty("BILLREFERENCENUMBER")
    private String billReferenceNumber;

    @JsonProperty("BILLERCODE")
    private String billerCode;

    @JsonProperty("SHORTREFERENCENUMBER")
    private String shortReferenceNumber;

    @JsonProperty("BNAME")
    private String bName;

    @JsonProperty("ECHODATA")
    private String echoData;

    @JsonProperty("BILLDUEDATE")
    private String billDueDate;

    @JsonProperty("IMEI")
    private String imei;

    @JsonProperty("SUBTYPE")
    private String subType;

    @JsonProperty("AGNTCODE")
    private String agntCode;

    @JsonProperty("AGENTCODE")
    private String agentCode;

    @JsonProperty("CONFIRM_REQ")
    private String confirmReq;

    @JsonProperty("TRANSACTIONDATE")
    private String transactionDate;

    @JsonProperty("TRANSACTIONID")
    private String transactionId;

    @JsonProperty("AUTHORIZATIONCODE")
    private String authorizationCode;

    @JsonProperty("BANKTRANSACTIONID")
    private String bankTransactionId;

    @JsonProperty("BALANCE")
    private String balance;

    @JsonProperty("FRBALANCE")
    private String frBalance;

    @JsonProperty("FICBALANCE")
    private String ficBalance;

    @JsonProperty("OTHERWALLETS")
    private CommandConsultBalanceOtherWalletsResponse otherWallets;

    @JsonProperty("IVR-RESPONSE")
    private String ivrResponse;

    @JsonProperty("EXEMPTED")
    private String exempted;

    @JsonProperty("BILLCCODE")
    private String billCCode;

    @JsonProperty("BILLANO")
    private String billANo;

    @JsonProperty("BILLNO")
    private String billNo;

    @JsonProperty("REFNO")
    private String refNo;

    public String toStringProtected() throws CloneNotSupportedException {
        MahindraDTO toPrint = (MahindraDTO) this.clone();
        if (toPrint.mPin != null && !toPrint.mPin.isEmpty()) {
            toPrint.mPin = mPin.replaceAll(REGEX_ALL, ASTERISK);
        }
        if (toPrint.pin != null && !toPrint.pin.isEmpty()) {
            toPrint.pin = pin.replaceAll(REGEX_ALL, ASTERISK);
        }
        try {
            return new ObjectMapper().writeValueAsString(toPrint);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            return "";
        }
    }
}

