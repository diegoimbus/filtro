package co.moviired.support.endpoint.bancolombia.dto;

import lombok.Data;
import javax.xml.bind.annotation.*;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "dataConsignmentResponse", propOrder = {
        "responseCode",
        "responseMessage",
        "phoneNumber",
        "amount",
        "txnId",
        "txnStatuseig",
        "date",
        "time",
        "nextLevel",
        "correlationId"
})
@XmlRootElement(name = "body")
public class DataConsignmentResponse {

    @XmlElement(required = true)
    private String responseCode;

    @XmlElement(required = true)
    private String responseMessage;

    @XmlElement(required = true)
    private String txnId;

    @XmlElement(required = true)
    private String phoneNumber;

    @XmlElement(required = true)
    private String txnStatuseig;

    @XmlElement(required = true)
    private String time;

    @XmlElement(required = true)
    private String date;

    @XmlElement(required = true)
    private String amount;

    @XmlElement(required = true)
    private String correlationId;

    @XmlElement(required = true)
    private String nextLevel;

}

