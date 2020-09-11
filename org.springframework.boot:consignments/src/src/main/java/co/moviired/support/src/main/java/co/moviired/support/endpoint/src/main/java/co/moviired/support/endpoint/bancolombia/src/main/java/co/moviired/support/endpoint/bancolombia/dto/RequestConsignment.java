package co.moviired.support.endpoint.bancolombia.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlElement;

/**
 * Copyright @2019. MOVIIRED. Todos los derechos reservados.
 *
 * @author Suarez, Juan
 * @version 1, 2019
 * @since 1.0
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({
        "TYPE",
        "MSISDN",
        "AMOUNT",
        "BANKID",
        "REFERENCEID",
        "BLOCKSMS",
        "TXNMODE",
        "CELLID",
        "FTXNID",
        "REMARKS",

        "MPIN",
        "PROVIDER",
        "OTPREQ",
        "ISPINCHECKREQ",
        "SOURCE",

        "USERTYPE",

})
@JsonRootName("COMMAND")
public class RequestConsignment {

    @XmlElement(name = "TYPE")
    private String type;

    @XmlElement(name = "MSISDN", required = true)
    private String msisdn;

    @XmlElement(name = "AMOUNT", required = true)
    private String amount;

    @XmlElement(name = "BANKID", required = true)
    private String bankid;

    @XmlElement(name = "REFERENCEID", required = true)
    private String referenceid;

    @XmlElement(name = "BLOCKSMS", required = true)
    private String blocksms;

    @XmlElement(name = "TXNMODE", required = true)
    private String txnmode;

    @XmlElement(name = "CELLID", required = true)
    private String cellid;

    @XmlElement(name = "FTXNID", required = true)
    private String ftxnid;

    @XmlElement(name = "REMARKS", required = true)
    private String remarks;

    @XmlElement(name = "MPIN")
    private String mpin;

    @XmlElement(name = "PROVIDER")
    private String provider;

    @XmlElement(name = "OTPREQ")
    private String otpreq;

    @XmlElement(name = "ISPINCHECKREQ")
    private String ispincheckreq;

    @XmlElement(name = "SOURCE")
    private String source;

    @XmlElement(name = "USERTYPE")
    private String usertype;

    @Override
    public String toString() {
        return "RequestConsignment{" +
                "type='" + type + '\'' +
                ", msisdn='" + msisdn + '\'' +
                ", amount='" + amount + '\'' +
                ", bankid='" + bankid + '\'' +
                ", referenceid='" + referenceid + '\'' +
                ", blocksms='" + blocksms + '\'' +
                ", txnmode='" + txnmode + '\'' +
                ", cellid='" + cellid + '\'' +
                ", ftxnid='" + ftxnid + '\'' +
                ", remarks='" + remarks + '\'' +
                ", mpin='" + mpin + '\'' +
                ", provider='" + provider + '\'' +
                ", otpreq='" + otpreq + '\'' +
                ", ispincheckreq='" + ispincheckreq + '\'' +
                ", source='" + source + '\'' +
                ", usertype='" + usertype + '\'' +
                '}';
    }
}

