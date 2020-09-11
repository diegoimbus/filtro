package co.moviired.support.endpoint.bancolombia.dto;

import lombok.Data;

import javax.xml.bind.annotation.*;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "dataRevertRequest", propOrder = {
        "phoneNumber",
        "amount",
        "accountantDate",
        "bankId",
        "referenceId",
        "blockSms",
        "txnMode",
        "cellId",
        "ftxnId",
        "remarks"
})
@XmlRootElement(name = "body")
public class DataRevertRequest {

    @XmlElement(required = true)
    private String phoneNumber;

    @XmlElement(required = true)
    private String amount;

    @XmlElement(required = true)
    private String bankId;

    @XmlElement(required = true)
    private String referenceId;

    @XmlElement(required = true)
    private String blockSms;

    @XmlElement(required = true)
    private String txnMode;

    @XmlElement(required = true)
    private String cellId;

    @XmlElement(required = true)
    private String ftxnId;

    @XmlElement(required = true)
    private String remarks;

    @XmlElement(required = true)
    private String accountantDate;
}

