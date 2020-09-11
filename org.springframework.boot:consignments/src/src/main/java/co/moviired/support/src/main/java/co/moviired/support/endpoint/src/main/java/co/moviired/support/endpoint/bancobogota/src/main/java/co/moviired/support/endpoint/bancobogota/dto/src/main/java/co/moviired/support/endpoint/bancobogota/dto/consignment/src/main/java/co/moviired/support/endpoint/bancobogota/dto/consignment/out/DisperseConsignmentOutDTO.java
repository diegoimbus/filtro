package co.moviired.support.endpoint.bancobogota.dto.consignment.out;

import co.moviired.support.endpoint.bancobogota.dto.generics.ResponseDTO;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "disperseConsignmentOutDTO",
        propOrder = {"idConsignment", "response", "trnsId", "consignmentType", "state"}
)
@XmlRootElement
public class DisperseConsignmentOutDTO extends ResponseDTO {
    private static final long serialVersionUID = 1L;
    protected String idConsignment;
    protected String response;
    protected String trnsId;
    protected String consignmentType;
    protected String state;

    public DisperseConsignmentOutDTO() {
        // Do nothing
    }

    public DisperseConsignmentOutDTO(ConsignmentDispersedDTO dto) {
        this.idConsignment = dto.getIdConsignment();
        this.response = dto.getResponse();
        this.trnsId = dto.getTrnsId();
        this.consignmentType = dto.getConsignmentType();
        this.state = dto.getState();
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

    public String getTrnsId() {
        return this.trnsId;
    }

    public void setTrnsId(String value) {
        this.trnsId = value;
    }

    public String getConsignmentType() {
        return this.consignmentType;
    }

    public void setConsignmentType(String value) {
        this.consignmentType = value;
    }

    public String getState() {
        return this.state;
    }

    public void setState(String value) {
        this.state = value;
    }

    @Override
    public String toString() {
        return "DisperseConsignmentOutDTO [idConsignment=" + this.idConsignment + ", response=" + this.response + ", trnsId=" + this.trnsId + ", consignmentType=" + this.consignmentType + ", state=" + this.state + "]";
    }
}

