package co.moviired.support.endpoint.bancobogota.dto.consignment.out;

import co.moviired.support.endpoint.bancobogota.dto.generics.ConsignmentResponseDTO;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "getBillAmountOutDTO",
        propOrder = {"bankNitOne", "systemDate", "transactionDate", "currencyType", "billAmount", "expirationDate", "billNumber", "referenceFieldTwo", "referenceFieldThree", "referenceFieldFour", "referenceFieldFive", "billingCompanyName", "billingCompanyAgreement", "bankNitTwo"}
)
@XmlRootElement
public class GetBillAmountOutDTO extends ConsignmentResponseDTO {
    private static final long serialVersionUID = 1L;
    protected String bankNitOne;
    protected String systemDate;
    protected String transactionDate;
    protected String currencyType;
    protected String billAmount;
    protected String expirationDate;
    protected String billNumber;
    protected String referenceFieldTwo;
    protected String referenceFieldThree;
    protected String referenceFieldFour;
    protected String referenceFieldFive;
    protected String billingCompanyName;
    protected String billingCompanyAgreement;
    protected String bankNitTwo;

    public GetBillAmountOutDTO() {
        // Do nothing
    }

    public String getBankNitOne() {
        return this.bankNitOne;
    }

    public void setBankNitOne(String value) {
        this.bankNitOne = value;
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

    public String getCurrencyType() {
        return this.currencyType;
    }

    public void setCurrencyType(String value) {
        this.currencyType = value;
    }

    public String getBillAmount() {
        return this.billAmount;
    }

    public void setBillAmount(String value) {
        this.billAmount = value;
    }

    public String getExpirationDate() {
        return this.expirationDate;
    }

    public void setExpirationDate(String value) {
        this.expirationDate = value;
    }

    public String getBillNumber() {
        return this.billNumber;
    }

    public void setBillNumber(String value) {
        this.billNumber = value;
    }

    public String getReferenceFieldTwo() {
        return this.referenceFieldTwo;
    }

    public void setReferenceFieldTwo(String value) {
        this.referenceFieldTwo = value;
    }

    public String getReferenceFieldThree() {
        return this.referenceFieldThree;
    }

    public void setReferenceFieldThree(String value) {
        this.referenceFieldThree = value;
    }

    public String getReferenceFieldFour() {
        return this.referenceFieldFour;
    }

    public void setReferenceFieldFour(String value) {
        this.referenceFieldFour = value;
    }

    public String getReferenceFieldFive() {
        return this.referenceFieldFive;
    }

    public void setReferenceFieldFive(String value) {
        this.referenceFieldFive = value;
    }

    public String getBillingCompanyName() {
        return this.billingCompanyName;
    }

    public void setBillingCompanyName(String value) {
        this.billingCompanyName = value;
    }

    public String getBillingCompanyAgreement() {
        return this.billingCompanyAgreement;
    }

    public void setBillingCompanyAgreement(String value) {
        this.billingCompanyAgreement = value;
    }

    public String getBankNitTwo() {
        return this.bankNitTwo;
    }

    public void setBankNitTwo(String value) {
        this.bankNitTwo = value;
    }

}

