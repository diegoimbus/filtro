package com.moviired.client.mahindra;

import lombok.Data;

import javax.xml.bind.annotation.*;


/**
 * <p>Java class for anonymous complex type.
 * <p>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="TYPE" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="DATE" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="EXTNWCODE" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="MSISDN" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="PIN" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="LOGINID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="PASSWORD" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="EXTCODE" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="EXTREFNUM" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="MSISDN2" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="AMOUNT" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="TXNID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="LANGUAGE1" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="LANGUAGE2" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="SELECTOR" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="INFO1" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="INFO2" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="INFO3" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="INFO4" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */

@Data
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "type",
        "date",
        "extnwcode",
        "msisdn",
        "pin",
        "loginid",
        "password",
        "extcode",
        "extrefnum",
        "msisdn2",
        "amount",
        "txnid",
        "language1",
        "language2",
        "selector",
        "info1",
        "info2",
        "info3",
        "info4"
})
@XmlRootElement(name = "Request")
public class Request {

    @XmlElement(name = "TYPE", required = true)
    private String type;

    //Fecha y Hora
    @XmlElement(name = "DATE", required = true)
    private String date;

    // Codigo de Red
    @XmlElement(name = "EXTNWCODE", required = true)
    private String extnwcode;

    @XmlElement(name = "MSISDN", required = true)
    private String msisdn;

    // PIN del Usuario de Canal
    @XmlElement(name = "PIN", required = true)
    private String pin;

    // ID de inicio de sesión
    @XmlElement(name = "LOGINID")
    private String loginid;

    // Contraseña
    @XmlElement(name = "PASSWORD")
    private String password;

    // Código Externo del Usuario de Canal
    @XmlElement(name = "EXTCODE")
    private String extcode;

    // Número de referencia externo
    @XmlElement(name = "EXTREFNUM")
    private String extrefnum;

    // Numero MSISDN del Subscriptor Final
    @XmlElement(name = "MSISDN2")
    private String msisdn2;

    // ID de Transacción
    @XmlElement(name = "TXNID")
    private String txnid;

    @XmlElement(name = "AMOUNT")
    private String amount;

    // Lenguaje del Usuario de Canal
    @XmlElement(name = "LANGUAGE1")
    private String language1;

    //Lenguaje del Subscriptor Final
    @XmlElement(name = "LANGUAGE2")
    private String language2;

    @XmlElement(name = "SELECTOR")
    private String selector;

    // Código de Municipio
    @XmlElement(name = "INFO1")
    private String info1;

    // Código de comercio
    @XmlElement(name = "INFO2")
    private String info2;

    // Código Sucursal
    @XmlElement(name = "INFO3")
    private String info3;

    // Código Punto de venta
    @XmlElement(name = "INFO4")
    private String info4;

}

