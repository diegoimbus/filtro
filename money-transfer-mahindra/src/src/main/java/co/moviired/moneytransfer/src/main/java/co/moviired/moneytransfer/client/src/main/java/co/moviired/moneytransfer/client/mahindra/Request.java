package co.moviired.moneytransfer.client.mahindra;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Data;

import java.io.Serializable;


@Data
@JsonRootName("COMMAND")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Request implements Serializable {

    @JsonProperty("TYPE")
    private String type;

    @JsonProperty("PROVIDER")
    private String provider;

    @JsonProperty("PROVIDER2")
    private String provider2;

    @JsonProperty("USERTYPE")
    private String userType;

    @JsonProperty("MSISDN")
    private String msisdn;

    @JsonProperty("MSISDN2")
    private String msisdn2;

    @JsonProperty("MPIN")
    private String mpin;

    @JsonProperty("PIN")
    private String pin;

    @JsonProperty("PAYID")
    private String payid;

    @JsonProperty("PAYID2")
    private String payid2;

    @JsonProperty("SNDMSISDN")
    private String sndmsisdn;

    @JsonProperty("SNDEMAIL")
    private String sndemail;

    @JsonProperty("SNDIDTYPE")
    private String sndidtype;

    @JsonProperty("SNDIDNO")
    private String sndidno;

    @JsonProperty("SNDNAME")
    private String sndname;

    @JsonProperty("RCVMSISDN")
    private String rcvmsisdn;

    @JsonProperty("RCVEMAIL")
    private String rcvemail;

    @JsonProperty("RCVIDTYPE")
    private String rcvidtype;

    @JsonProperty("RCVIDNO")
    private String rcvidno;

    @JsonProperty("RCVNAME")
    private String rcvname;

    @JsonProperty("AMOUNT")
    private String amount;

    @JsonProperty("LANGUAGE")
    private String language;

    @JsonProperty("LANGUAGE2")
    private String language2;

    @JsonProperty("PASSCODE")
    private String passcode;

    @JsonProperty("PASSCODETXN")
    private String passCodeTxn;

    @JsonProperty("TXNID")
    private String txnId;

    @JsonProperty("REMARKS")
    private String remarks;

    @JsonProperty("FTXNID")
    private String ftxnId;

}

