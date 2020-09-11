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
@XmlType(name = "dataEchoRequest", propOrder = {
        "phonenumber",
        "provider",
        "pin",
        "otpreq",
        "ispincheckreq",
        "source"
})
@XmlRootElement(name = "body")
public class DataEchoRequest {

    @XmlElement(required = true)
    private String phonenumber;

    @XmlElement(required = true)
    private String provider;

    @XmlElement(required = true)
    private String pin;

    @XmlElement(required = true)
    private String otpreq;

    @XmlElement(required = true)
    private String ispincheckreq;

    @XmlElement(required = true)
    private String source;

    @Override
    public String toString() {
        return "DataEchoRequest{" +
                "phonenumber='" + phonenumber + '\'' +
                ", provider='" + provider + '\'' +
                ", pin='" + pin + '\'' +
                ", otpreq='" + otpreq + '\'' +
                ", ispincheckreq='" + ispincheckreq + '\'' +
                ", source='" + source + '\'' +
                '}';
    }
}

