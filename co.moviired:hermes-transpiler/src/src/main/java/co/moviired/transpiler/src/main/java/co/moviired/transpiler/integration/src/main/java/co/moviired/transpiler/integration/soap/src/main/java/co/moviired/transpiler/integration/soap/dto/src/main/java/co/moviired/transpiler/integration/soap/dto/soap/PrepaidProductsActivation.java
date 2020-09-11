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
 * <p>Clase Java para prepaidProductsActivation complex type.
 *
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 *
 * <pre>
 * &lt;complexType name="prepaidProductsActivation"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="amount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="deviceNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="transactionId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="product" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="productType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="destinition" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="userName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="password" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="externalReference1" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="externalReference2" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="externalReference3" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="userId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="session" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="systemId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="transactionDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="hash" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 */
@Data
@XmlRootElement(name = "prepaidProductsActivation", namespace = SoapConfig.NAMESPACE_URI)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "prepaidProductsActivation", propOrder = {
        "amount",
        "deviceNumber",
        "transactionId",
        "product",
        "productType",
        "destinition",
        "userName",
        "password",
        "externalReference1",
        "externalReference2",
        "externalReference3",
        "userId",
        "session",
        "systemId",
        "transactionDate",
        "hash"
})
public class PrepaidProductsActivation {

    @XmlElement(name = "amount")
    private String amount;

    @XmlElement(name = "deviceNumber")
    private String deviceNumber;

    @XmlElement(name = "transactionId")
    private String transactionId;

    @XmlElement(name = "product")
    private String product;

    @XmlElement(name = "productType")
    private String productType;

    @XmlElement(name = "destinition")
    private String destinition;

    @XmlElement(name = "userName")
    private String userName;

    @XmlElement(name = "password")
    private String password;

    @XmlElement(name = "externalReference1")
    private String externalReference1;

    @XmlElement(name = "externalReference2")
    private String externalReference2;

    @XmlElement(name = "externalReference3")
    private String externalReference3;

    @XmlElement(name = "userId")
    private String userId;

    @XmlElement(name = "session")
    private String session;

    @XmlElement(name = "systemId")
    private String systemId;

    @XmlElement(name = "transactionDate")
    private String transactionDate;

    @XmlElement(name = "hash")
    private String hash;

    public PrepaidProductsActivation(PrepaidProductsActivation prepaidProductsActivation) {
        this.amount = prepaidProductsActivation.getAmount();
        this.deviceNumber = prepaidProductsActivation.getDeviceNumber();
        this.transactionId = prepaidProductsActivation.getTransactionId();
        this.product = prepaidProductsActivation.getProduct();
        this.productType = prepaidProductsActivation.getProductType();
        this.destinition = prepaidProductsActivation.getDestinition();
        this.userName = prepaidProductsActivation.getUserName();
        this.password = prepaidProductsActivation.getPassword();
        this.externalReference1 = prepaidProductsActivation.getExternalReference1();
        this.externalReference2 = prepaidProductsActivation.getExternalReference2();
        this.externalReference3 = prepaidProductsActivation.getExternalReference3();
        this.userId = prepaidProductsActivation.getUserId();
        this.session = prepaidProductsActivation.getSession();
        this.systemId = prepaidProductsActivation.getSystemId();
        this.transactionDate = prepaidProductsActivation.getTransactionDate();
        this.hash = prepaidProductsActivation.getHash();
    }

    public PrepaidProductsActivation() {
        super();
    }
}
