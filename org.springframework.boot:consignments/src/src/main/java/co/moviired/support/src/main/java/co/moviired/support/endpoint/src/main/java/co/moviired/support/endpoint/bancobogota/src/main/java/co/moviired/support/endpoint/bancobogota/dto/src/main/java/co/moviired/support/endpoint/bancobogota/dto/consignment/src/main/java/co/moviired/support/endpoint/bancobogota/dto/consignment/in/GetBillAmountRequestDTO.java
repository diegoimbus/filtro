package co.moviired.support.endpoint.bancobogota.dto.consignment.in;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class GetBillAmountRequestDTO {
    @XmlElement(required = true)
    private HeaderAuthentication headerAuthentication;

    @XmlElement(required = true)
    private GetBillAmountInDTO getBillAmountInDTO;

    public HeaderAuthentication getHeaderAuthentication() {
        return headerAuthentication;
    }

    public void setHeaderAuthentication(HeaderAuthentication headerAuthentication) {
        this.headerAuthentication = headerAuthentication;
    }

    public GetBillAmountInDTO getGetBillAmountInDTO() {
        return getBillAmountInDTO;
    }

    public void setGetBillAmountInDTO(GetBillAmountInDTO getBillAmountInDTO) {
        this.getBillAmountInDTO = getBillAmountInDTO;
    }
}

