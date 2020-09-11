package co.moviired.support.endpoint.bancobogota.dto.consignment.in;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class NotifyBillPaymentRequestDTO {
    @XmlElement(required = true)
    private HeaderAuthentication headerAuthentication;

    @XmlElement(required = true)
    private NotifyBillPaymentInDTO notifyBillPaymentInDTO;

    public HeaderAuthentication getHeaderAuthentication() {
        return headerAuthentication;
    }

    public void setHeaderAuthentication(HeaderAuthentication headerAuthentication) {
        this.headerAuthentication = headerAuthentication;
    }

    public NotifyBillPaymentInDTO getNotifyBillPaymentInDTO() {
        return notifyBillPaymentInDTO;
    }

    public void setNotifyBillPaymentInDTO(NotifyBillPaymentInDTO notifyBillPaymentInDTO) {
        this.notifyBillPaymentInDTO = notifyBillPaymentInDTO;
    }
}

