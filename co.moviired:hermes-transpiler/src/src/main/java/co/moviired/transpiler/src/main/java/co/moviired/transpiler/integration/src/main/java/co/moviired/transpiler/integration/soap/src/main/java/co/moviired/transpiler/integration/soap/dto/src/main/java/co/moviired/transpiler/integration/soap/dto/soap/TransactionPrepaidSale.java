//
// Este archivo ha sido generado por la arquitectura JavaTM para la implantación de la referencia de enlace (JAXB) XML v2.3.0
// Visite <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a>
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen.
// Generado el: 2018.11.16 a las 11:30:51 AM COT
//


package co.moviired.transpiler.integration.soap.dto.soap;

import co.moviired.transpiler.conf.soap.SoapConfig;
import lombok.Data;

import javax.xml.bind.annotation.*;


/**
 * <p>Clase Java para transactionPrepaidSale complex type.
 *
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 *
 * <pre>
 * &lt;complexType name="transactionPrepaidSale"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="answerCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="errorDescription" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="balance" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="billNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="dueDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="messageId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="profit" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="errorDesc" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="errorNo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="activationCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="transactionId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="available1" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="available2" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="available3" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 */
@Data
@XmlRootElement(name = "transactionPrepaidSale", namespace = SoapConfig.NAMESPACE_URI)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "transactionPrepaidSale", propOrder = {
        "answerCode",
        "errorDesc",
        "errorNo",
        "balance",
        "billNumber",
        "dueDate",
        "profit",
        "activationCode",
        "transactionId",
        "available1",
        "available2",
        "available3",
        "errorDescription",
        "messageId"
})
public class TransactionPrepaidSale {

    @XmlElement(name = "answerCode")
    private String answerCode;

    @XmlElement(name = "errorDescription")
    private String errorDescription;

    @XmlElement(name = "balance")
    private String balance;

    @XmlElement(name = "billNumber")
    private String billNumber;

    @XmlElement(name = "dueDate")
    private String dueDate;

    @XmlElement(name = "messageId")
    private String messageId;

    @XmlElement(name = "profit")
    private String profit;

    @XmlElement(name = "errorDesc")
    private String errorDesc;

    @XmlElement(name = "errorNo")
    private String errorNo;

    @XmlElement(name = "activationCode")
    private String activationCode;

    @XmlElement(name = "transactionId")
    private String transactionId;

    @XmlElement(name = "available1")
    private String available1;

    @XmlElement(name = "available2")
    private String available2;

    @XmlElement(name = "available3")
    private String available3;

}

