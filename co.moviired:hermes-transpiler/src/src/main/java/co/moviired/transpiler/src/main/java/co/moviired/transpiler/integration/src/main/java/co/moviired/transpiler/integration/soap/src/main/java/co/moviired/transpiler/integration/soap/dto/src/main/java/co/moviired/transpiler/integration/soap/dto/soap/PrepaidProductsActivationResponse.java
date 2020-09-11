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
 * <p>Clase Java para prepaidProductsActivationResponse complex type.
 *
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 *
 * <pre>
 * &lt;complexType name="prepaidProductsActivationResponse"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="transactionPrepaidSale" type="{http://ws.prepaidsale.solidda.koghi.com/}transactionPrepaidSale"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 */
@Data
@XmlRootElement(name = "prepaidProductsActivationResponse", namespace = SoapConfig.NAMESPACE_URI)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "prepaidProductsActivationResponse", propOrder = {
        "transactionPrepaidSale"
})
public class PrepaidProductsActivationResponse {

    @XmlElement(required = true)
    private TransactionPrepaidSale transactionPrepaidSale;

}

