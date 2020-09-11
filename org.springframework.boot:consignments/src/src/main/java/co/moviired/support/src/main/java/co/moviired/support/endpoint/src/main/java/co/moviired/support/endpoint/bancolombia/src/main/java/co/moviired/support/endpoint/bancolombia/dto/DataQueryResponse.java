package co.moviired.support.endpoint.bancolombia.dto;

import lombok.Data;
import javax.xml.bind.annotation.*;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "dataQueryResponse", propOrder = {
        "responseCode",
        "responseMessage",
        "phoneNumber",
        "message",
        "firtsName",
        "lastName",
        "email",
        "dob",
        "city",
        "gender",
        "status"
})
@XmlRootElement(name = "body")
public class DataQueryResponse {

    @XmlElement(required = true)
    private String responseCode;

    @XmlElement(required = true)
    private String responseMessage;

    @XmlElement(required = true)
    private String phoneNumber;

    @XmlElement(required = true)
    private String firtsName;

    @XmlElement(required = true)
    private String lastName;

    @XmlElement(required = true)
    private String email;

    @XmlElement(required = true)
    private String dob;

    @XmlElement(required = true)
    private String city;

    @XmlElement(required = true)
    private String gender;

    @XmlElement(required = true)
    private String status;

    @XmlElement(required = true)
    private String message;

}

