package co.moviired.support.endpoint.bancolombia.dto;

import lombok.Data;

import javax.xml.bind.annotation.*;

/**
 * Copyright @2019. MOVIIRED. Todos los derechos reservados.
 *
 * @author Suarez, Juan
 * @version 1, 2019
 * @since 1.0
 */

@Data
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "type",
        "txnid",
        "txnstatus",
        "txnstatuseig",
        "msisdn",
        "date",
        "time",
        "amount",
        "nextlevel",
        "trid",

        "lname",
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
        "gender",
        "birthplace",
        "dob",
        "securityquestionflag",
        "email",
        "lastlogin",
        "agentcode",
        "message",

        "fname",
        "emailid",
        "city",
        "status",
        "barredtype",
        "preflanguage"

})
@XmlRootElement(name = "COMMAND")
public class ResponseConsignment {

    @XmlElement(name ="TYPE")
    private String type;

    @XmlElement(name ="TXNID", required = true)
    private String txnid;

    @XmlElement(name ="TXNSTATUS", required = true)
    private String txnstatus;

    @XmlElement(name ="TXNSTATUSEIG", required = true)
    private String txnstatuseig;

    @XmlElement(name ="MSISDN", required = true)
    private String msisdn;

    @XmlElement(name ="DATE", required = true)
    private String date;

    @XmlElement(name ="TIME", required = true)
    private String time;

    @XmlElement(name ="AMOUNT", required = true)
    private String amount;

    @XmlElement(name ="NEXTLEVEL", required = true)
    private String nextlevel;

    @XmlElement(name ="TRID", required = true)
    private String trid;

    @XmlElement(name ="LNAME")
    private String lname;

    @XmlElement(name ="USERID")
    private String userid;

    @XmlElement(name ="USERTYPE")
    private String usertype;

    @XmlElement(name ="FIRSTNAME")
    private String firstname;

    @XmlElement(name ="LASTNAME")
    private String lastname;

    @XmlElement(name ="PROVIDER")
    private String provider;

    @XmlElement(name ="LANGCODE")
    private String langcode;

    @XmlElement(name ="GRADE")
    private String grade;

    @XmlElement(name ="TCP")
    private String tcp;

    @XmlElement(name ="WALLETNUMBER")
    private String walletnumber;

    @XmlElement(name ="IDTYPE")
    private String idtype;

    @XmlElement(name ="IDNO")
    private String idno;

    @XmlElement(name ="GENDER")
    private String gender;

    @XmlElement(name ="BIRTHPLACE")
    private String birthplace;

    @XmlElement(name ="DOB")
    private String dob;

    @XmlElement(name ="SECURITYQUESTIONSFLAG")
    private String securityquestionflag;

    @XmlElement(name ="EMAIL")
    private String email;

    @XmlElement(name ="LASTLOGIN")
    private String lastlogin;

    @XmlElement(name ="AGENTCODE")
    private String agentcode;

    @XmlElement(name ="MESSAGE")
    private String message;

    @XmlElement(name ="FNAME")
    private String fname;

    @XmlElement(name ="EMAILID")
    private String emailid;

    @XmlElement(name ="CITY")
    private String city;

    @XmlElement(name ="STATUS")
    private String status;

    @XmlElement(name ="BARREDTYPE")
    private String barredtype;

    @XmlElement(name ="PREFLANGUAGE")
    private String preflanguage;
}

