//
// Este archivo ha sido generado por la arquitectura JavaTM para la implantaci\u00f3n de la referencia de enlace (JAXB) XML v2.3.0 
// Visite <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Todas las modificaciones realizadas en este archivo se perder\u00e1n si se vuelve a compilar el esquema de origen. 
// Generado el: 2018.11.25 a las 10:59:37 AM COT 
//


package co.moviired.transpiler.integration.soap.dto.soap;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each
 * Java content interface and Java element interface
 * generated in the co.movii.transpiler.integration.soap.dto package.
 * <p>An ObjectFactory allows you to programatically
 * construct new instances of the Java representation
 * for XML content. The Java representation of XML
 * content can consist of schema derived interfaces
 * and classes representing the binding of schema
 * type definitions, element declarations and model
 * groups.  Factory methods for each of these are
 * provided in this class.
 */
@XmlRegistry
public class ObjectFactory {
    private static final String NAMESPACE_URI = "http://ws.prepaidsale.solidda.koghi.com/";

    private static final QName PREPAID_PRODUCTS_ACTIVATION_QNAME = new QName(NAMESPACE_URI, "prepaidProductsActivation");
    private static final QName PREPAID_PRODUCTS_ACTIVATION_RESPONSE_QNAME = new QName(NAMESPACE_URI, "prepaidProductsActivationResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: co.movii.transpiler.integration.soap.dto
     */
    public ObjectFactory() {
        super();
    }

    /**
     * Create an instance of {@link PrepaidProductsActivation }
     */
    public final PrepaidProductsActivation createPrepaidProductsActivation() {
        return new PrepaidProductsActivation();
    }

    /**
     * Create an instance of {@link PrepaidProductsActivationResponse }
     */
    public final PrepaidProductsActivationResponse createPrepaidProductsActivationResponse() {
        return new PrepaidProductsActivationResponse();
    }

    /**
     * Create an instance of {@link TransactionPrepaidSale }
     */
    public final TransactionPrepaidSale createTransactionPrepaidSale() {
        return new TransactionPrepaidSale();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PrepaidProductsActivation }{@code >}
     *
     * @param value Java instance representing xml element's value.
     * @return the new instance of {@link JAXBElement }{@code <}{@link PrepaidProductsActivation }{@code >}
     */
    @XmlElementDecl(namespace = NAMESPACE_URI, name = "prepaidProductsActivation")
    public final JAXBElement<PrepaidProductsActivation> createPrepaidProductsActivation(PrepaidProductsActivation value) {
        return new JAXBElement<>(PREPAID_PRODUCTS_ACTIVATION_QNAME, PrepaidProductsActivation.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PrepaidProductsActivationResponse }{@code >}
     *
     * @param value Java instance representing xml element's value.
     * @return the new instance of {@link JAXBElement }{@code <}{@link PrepaidProductsActivationResponse }{@code >}
     */
    @XmlElementDecl(namespace = NAMESPACE_URI, name = "prepaidProductsActivationResponse")
    public final JAXBElement<PrepaidProductsActivationResponse> createPrepaidProductsActivationResponse(PrepaidProductsActivationResponse value) {
        return new JAXBElement<>(PREPAID_PRODUCTS_ACTIVATION_RESPONSE_QNAME, PrepaidProductsActivationResponse.class, null, value);
    }

}

