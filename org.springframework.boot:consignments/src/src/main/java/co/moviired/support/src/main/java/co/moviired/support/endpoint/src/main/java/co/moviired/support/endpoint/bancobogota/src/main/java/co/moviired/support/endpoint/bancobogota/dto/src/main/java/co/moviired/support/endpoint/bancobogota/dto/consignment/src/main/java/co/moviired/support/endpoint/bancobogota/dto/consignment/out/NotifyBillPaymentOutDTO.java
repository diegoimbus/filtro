package co.moviired.support.endpoint.bancobogota.dto.consignment.out;

import co.moviired.support.endpoint.bancobogota.dto.generics.ConsignmentResponseDTO;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "notifyBillPaymentOutDTO",
        propOrder = {"bankNit",
                "systemDate",
                "transactionDate",
                "authorizationNumber",
                "idMahindra",
                "authorizationCode",
                "correlationId"}
)
@XmlRootElement
public class NotifyBillPaymentOutDTO extends ConsignmentResponseDTO {
    private static final long serialVersionUID = 1L;
    protected String bankNit;
    protected String systemDate;
    protected String transactionDate;
    protected String authorizationNumber;
    protected String idMahindra;
    protected String authorizationCode;
    protected String correlationId;

    public NotifyBillPaymentOutDTO() {
        // Do nothing
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public String getBankNit() {
        return this.bankNit;
    }

    public void setBankNit(String value) {
        this.bankNit = value;
    }

    public String getSystemDate() {
        return this.systemDate;
    }

    public void setSystemDate(String value) {
        this.systemDate = value;
    }

    public String getTransactionDate() {
        return this.transactionDate;
    }

    public void setTransactionDate(String value) {
        this.transactionDate = value;
    }

    public String getAuthorizationNumber() {
        return this.authorizationNumber;
    }

    public void setAuthorizationNumber(String value) {
        this.authorizationNumber = value;
    }

    public String getIdMahindra() {
        return this.idMahindra;
    }

    public void setIdMahindra(String idMahindra) {
        this.idMahindra = idMahindra;
    }

    public String getAuthorizationCode() {
        return this.authorizationCode;
    }

    public void setAuthorizationCode(String value) {
        this.authorizationCode = value;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder(87);
        builder.append("NotifyBillPaymentOutDTO [bankNit=").append(this.bankNit).append(", systemDate=").append(this.systemDate).append(", transactionDate=").append(this.transactionDate).append(", authorizationNumber=").append(this.authorizationNumber).append(", idMahindra=").append(this.idMahindra).append(", authorizationCode=").append(this.authorizationCode).append(", correlationId=").append(this.correlationId).append(']');
        return builder.toString();
    }
}

