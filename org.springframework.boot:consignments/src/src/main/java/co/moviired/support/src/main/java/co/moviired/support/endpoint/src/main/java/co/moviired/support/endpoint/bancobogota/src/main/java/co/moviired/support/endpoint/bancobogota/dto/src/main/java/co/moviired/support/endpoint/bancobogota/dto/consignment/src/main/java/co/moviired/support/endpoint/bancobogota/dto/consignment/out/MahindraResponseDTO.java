package co.moviired.support.endpoint.bancobogota.dto.consignment.out;

import lombok.Data;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@Data
@XmlRootElement(
        name = "COMMAND"
)
public class MahindraResponseDTO {
    private String responseType;
    private String transactionIdentificator;
    private String transactionStatus;
    private String executionResponse;
    private String client;
    private String date;
    private String transferIdentification;
    private String time;
    private String amount;
    private String nextLevelValue;
    private String transactionMode;
    private String message;
    private String firtsName;
    private String lastName;
    private String email;
    private String dob;
    private String city;
    private String gender;
    private String status;

    public MahindraResponseDTO() {
        // Do nothing
    }

    @XmlElement(name = "STATUS")
    public void setStatus(String status) {this.status = status;}

    public String getStatus() {return this.status;}

    @XmlElement(name = "GENDER")
    public void setGender(String gender) {this.gender = gender;}

    public String getGender() {return this.gender;}

    @XmlElement(name = "CITY")
    public void setCity(String city) {this.city = city;}

    public String getCity() {return this.city;}

    @XmlElement(name = "DOB")
    public void setDob(String dob) {this.dob = dob;}

    public String getDob() {return this.dob;}

    @XmlElement(name = "EMAILID")
    public void setEmail(String email) {this.email = email;}

    public String getEmail() {return this.email;}

    @XmlElement(name = "LNAME")
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getLastName() {
        return this.lastName;
    }

    @XmlElement(name = "FNAME")
    public void setFirtsName(String firtsName) {
        this.firtsName = firtsName;
    }

    public String getFirtsName() {
        return this.firtsName;
    }

    public String getResponseType() {
        return this.responseType;
    }

    @XmlElement(name = "TYPE")
    public void setResponseType(String responseType) {
        this.responseType = responseType;
    }

    public String getTransactionIdentificator() {
        return this.transactionIdentificator;
    }

    @XmlElement(name = "TXNID")
    public void setTransactionIdentificator(String transactionIdentificator) {this.transactionIdentificator = transactionIdentificator;}

    public String getTransactionStatus() {
        return this.transactionStatus;
    }

    @XmlElement(name = "TXNSTATUS")
    public void setTransactionStatus(String transactionStatus) {
        this.transactionStatus = transactionStatus;
    }

    public String getExecutionResponse() {
        return this.executionResponse;
    }

    @XmlElement(name = "TXNSTATUSEIG")
    public void setExecutionResponse(String executionResponse) {
        this.executionResponse = executionResponse;
    }

    public String getClient() {
        return this.client;
    }

    @XmlElement(name = "MSISDN")
    public void setClient(String client) {
        this.client = client;
    }

    public String getDate() {
        return this.date;
    }

    @XmlElement(name = "DATE")
    public void setDate(String date) {
        this.date = date;
    }

    public String getTransferIdentification() {
        return this.transferIdentification;
    }

    @XmlElement(name = "TRID")
    public void setTransferIdentification(String transferIdentification) {this.transferIdentification = transferIdentification;}

    public String getTime() {
        return this.time;
    }

    @XmlElement(name = "TIME")
    public void setTime(String time) {
        this.time = time;
    }

    public String getAmount() {
        return this.amount;
    }

    @XmlElement(name = "AMOUNT")
    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getNextLevelValue() {
        return this.nextLevelValue;
    }

    @XmlElement(name = "NEXTLEVEL")
    public void setNextLevelValue(String nextLevelValue) {
        this.nextLevelValue = nextLevelValue;
    }

    public String getTransactionMode() {
        return this.transactionMode;
    }

    @XmlElement(name = "TXNMODE")
    public void setTransactionMode(String transactionMode) {
        this.transactionMode = transactionMode;
    }

    public String getMessage() {
        return this.message;
    }

    @XmlElement(name = "MESSAGE")
    public void setMessage(String message) {
        this.message = message;
    }

    public String toString() {
        return "ConsignmentOutDTO [responseType=" + this.responseType + ", transactionIdentificator=" + this.transactionIdentificator + ", transactionStatus=" + this.transactionStatus + ", executionResponse=" + this.executionResponse + ", client=" + this.client + ", date=" + this.date + ", transferIdentification=" + this.transferIdentification + ", time=" + this.time + ", amount=" + this.amount + ", nextLevelValue=" + this.nextLevelValue + ", transactionMode=" + this.transactionMode + ", message=" + this.message + "]";
    }
}

