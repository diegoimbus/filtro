package co.moviired.support.domain.client.mahindra;


import lombok.Data;

import javax.xml.bind.annotation.*;

/**
 * <p>
 * Java class for anonymous complex type.
 * <p>
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * <p>
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="TYPE" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="TXNSTATUS" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="DATE" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="EXTREFNUM" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="TXNID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="REQSTATUS" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="MESSAGE" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */

@Data
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "txnid",
        "txnamt",
        "from",
        "txndt",
        "payid",
        "servicetype",
        "txntype"
})
@XmlRootElement(name = "TRANSDETAILS")
public class ResponseDetail {

    @XmlElement(name = "TXNID", required = true)
    private String txnid;

    // Codigo Estado de la Transaccion
    @XmlElement(name = "TXNAMT", required = true)
    private String txnamt;

    //Tango Request ID
    @XmlElement(name = "FROM", required = true)
    private String from;

    // ID de Transacción
    @XmlElement(name = "TXNDT", required = true)
    private String txndt;

    // Mensaje
    @XmlElement(name = "PAYID", required = true)
    private String payid;

    @XmlElement(name = "SERVICETYPE", required = true)
    private String servicetype;

    @XmlElement(name = "TXNTYPE", required = true)
    private String txntype;

}

