package co.moviired.support.endpoint.bancobogota.dto.consignment.in;

import co.moviired.support.endpoint.bancobogota.dto.generics.RequestCoreDTO;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "RegisterConsignmentInCoreDTO",
        propOrder = {"billNumber", "compensationCode", "billTotalAmount", "transactionDate", "uuid", "source", "accountNumber", "status", "response", "trnsId", "client"}
)
@XmlRootElement
public class RegisterConsignmentInCoreDTO extends RequestCoreDTO {
    private static final long serialVersionUID = 1L;
    protected String billNumber;
    protected String compensationCode;
    protected String billTotalAmount;
    protected String transactionDate;
    protected String uuid;
    protected String source;
    protected String accountNumber;
    private String status;
    private String response;
    private String trnsId;
    private String client;

    public RegisterConsignmentInCoreDTO() {
        // Do nothing
    }

    public String getTransactionDate() {
        return this.transactionDate;
    }

    public void setTransactionDate(String value) {
        this.transactionDate = value;
    }

    public String getUuid() {
        return this.uuid;
    }

    public void setUuid(String value) {
        this.uuid = value;
    }

    public String getCompensationCode() {
        return this.compensationCode;
    }

    public void setCompensationCode(String value) {
        this.compensationCode = value;
    }

    public String getBillTotalAmount() {
        return this.billTotalAmount;
    }

    public void setBillTotalAmount(String value) {
        this.billTotalAmount = value;
    }

    public String getBillNumber() {
        return this.billNumber;
    }

    public void setBillNumber(String value) {
        this.billNumber = value;
    }

    public String getSource() {
        return this.source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getAccountNumber() {
        return this.accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getResponse() {
        return this.response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getTrnsId() {
        return this.trnsId;
    }

    public void setTrnsId(String trnsId) {
        this.trnsId = trnsId;
    }

    public String getClient() {
        return this.client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("RegisterConsignmentInCoreDTO [billNumber=").append(this.billNumber).append(", compensationCode=").append(this.compensationCode).append(", billTotalAmount=").append(this.billTotalAmount).append(", transactionDate=").append(this.transactionDate).append(", uuid=").append(this.uuid).append(", source=").append(this.source).append(", accountNumber=").append(this.accountNumber).append(", status=").append(this.status).append(", response=").append(this.response).append(", trnsId=").append(this.trnsId).append("]").append(", client=").append(this.client).append("]");
        return builder.toString();
    }
}

