package co.moviired.support.endpoint.bancobogota.dto.consignment.out;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "consignmentDispersedDTO",
        propOrder = {"consignmentType", "idConsignment", "response", "state", "trnsId"}
)
public class ConsignmentDispersedDTO {
    protected String consignmentType;
    protected String idConsignment;
    protected String response;
    protected String state;
    protected String trnsId;

    public ConsignmentDispersedDTO() {
        // Do nothing
    }

    public String getConsignmentType() {
        return this.consignmentType;
    }

    public void setConsignmentType(String value) {
        this.consignmentType = value;
    }

    public String getIdConsignment() {
        return this.idConsignment;
    }

    public void setIdConsignment(String value) {
        this.idConsignment = value;
    }

    public String getResponse() {
        return this.response;
    }

    public void setResponse(String value) {
        this.response = value;
    }

    public String getState() {
        return this.state;
    }

    public void setState(String value) {
        this.state = value;
    }

    public String getTrnsId() {
        return this.trnsId;
    }

    public void setTrnsId(String value) {
        this.trnsId = value;
    }

    public String toString() {
        return "ConsignmentDispersedDTO [consignmentType=" + this.consignmentType + ", idConsignment=" + this.idConsignment + ", response=" + this.response + ", state=" + this.state + ", trnsId=" + this.trnsId + "]";
    }
}

