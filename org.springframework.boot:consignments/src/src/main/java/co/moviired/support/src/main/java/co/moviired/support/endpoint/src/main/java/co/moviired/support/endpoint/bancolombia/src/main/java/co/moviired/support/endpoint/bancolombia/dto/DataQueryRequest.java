package co.moviired.support.endpoint.bancolombia.dto;

import lombok.Data;
import javax.xml.bind.annotation.*;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "dataQueryRequest", propOrder = {
        "phoneNumber",
        "provider",
        "usertype"
})
@XmlRootElement(name = "body")
public class DataQueryRequest {

    @XmlElement(required = true)
    private String phoneNumber;

    @XmlElement(required = true)
    private String provider;

    @XmlElement(required = true)
    private String usertype;

}

