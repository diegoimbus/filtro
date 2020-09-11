package co.moviired.support.endpoint.bancobogota.dto.consignment.out;

import co.moviired.support.endpoint.bancobogota.dto.generics.ConsignmentResponseDTO;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "revertBillPaymentOutDTO",
        propOrder = {"bankNit", "systemDate", "uuid", "authorizationNumber", "transactionDate", "idMahindra", "correlationId"}
)
@XmlRootElement
public class RevertBillPaymentOutDTO extends ConsignmentResponseDTO {
    protected static final long serialVersionUID = 1L;
    protected String bankNit;
    protected String systemDate;
    protected String uuid;
    protected String authorizationNumber;
    protected String transactionDate;
    protected String idMahindra;
    protected String correlationId;

    public RevertBillPaymentOutDTO() {
        // Do nothing
    }

    public String getCorrelationId() { return correlationId; }

    public void setCorrelationId(String correlationId) { this.correlationId = correlationId; }

    public String getBankNit() {
        return this.bankNit;
    }

    public void setBankNit(String bankNit) {
        this.bankNit = bankNit;
    }

    public String getSystemDate() {
        return this.systemDate;
    }

    public void setSystemDate(String systemDate) {
        this.systemDate = systemDate;
    }

    public String getUuid() {
        return this.uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getAuthorizationNumber() {
        return this.authorizationNumber;
    }

    public void setAuthorizationNumber(String authorizationNumber) {
        this.authorizationNumber = authorizationNumber;
    }

    public String getTransactionDate() {
        return this.transactionDate;
    }

    public void setTransactionDate(String transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getIdMahindra() {
        return this.idMahindra;
    }

    public void setIdMahindra(String idMahindra) {
        this.idMahindra = idMahindra;
    }

    public String toString() {
        return "RevertBillPaymentOutDTO [bankNit=" + this.bankNit + ", systemDate=" + this.systemDate + ", uuid=" + this.uuid + ", authorizationNumber=" + this.authorizationNumber + ", transactionDate=" + this.transactionDate + ", idMahindra=" + this.idMahindra + ", correlationId=" + this.correlationId + "]";
    }
}

