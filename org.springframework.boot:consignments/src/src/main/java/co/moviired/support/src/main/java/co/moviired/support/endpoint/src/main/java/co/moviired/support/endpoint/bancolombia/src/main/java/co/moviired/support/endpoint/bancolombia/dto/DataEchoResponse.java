package co.moviired.support.endpoint.bancolombia.dto;

import lombok.Data;
import javax.xml.bind.annotation.*;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "dataEchoResponse", propOrder = {
        "responsecode",
        "responsemessage",
})
@XmlRootElement(name = "body")
public class DataEchoResponse {

   @XmlElement(required = true)
    private String responsecode;

    @XmlElement(required = true)
    private String responsemessage;

    @Override
    public String toString() {
        return "DataEchoResponse{" +
                "responsecode='" + responsecode + '\'' +
                ", responsemessage='" + responsemessage + '\'' +
                '}';
    }
}

