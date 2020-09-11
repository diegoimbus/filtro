package co.moviired.support.endpoint.bancobogota.dto.consignment.in;

import co.moviired.support.endpoint.bancobogota.dto.generics.RequestCoreDTO;

import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "getBillAmountInDTO",
        propOrder = {"accountantDate", "agreementCode", "bankNitOne", "bankNitTwo", "billNumber", "billingCompanyAgreementOne", "billingCompanyAgreementTwo", "billingCompanyNameThree", "billingCompanyNameOne", "billingCompanyNameTwo", "compensationCode", "officeCode", "referenceFieldFive", "referenceFieldFour", "referenceFieldThree", "referenceFieldTwo", "transactionChannel", "transactionDate"}
)
@XmlRootElement(name = "body")
public class GetBillAmountInDTO extends RequestCoreDTO {
    protected static final long serialVersionUID = 1L;
    protected String accountantDate;
    protected String agreementCode;
    protected String bankNitOne;
    protected String bankNitTwo;
    protected String billNumber;
    protected String billingCompanyAgreementOne;
    protected String billingCompanyAgreementTwo;
    protected String billingCompanyNameThree;
    protected String billingCompanyNameOne;
    protected String billingCompanyNameTwo;
    protected String compensationCode;
    protected String officeCode;
    protected String referenceFieldFive;
    protected String referenceFieldFour;
    protected String referenceFieldThree;
    protected String referenceFieldTwo;
    protected String transactionChannel;
    protected String transactionDate;

    public GetBillAmountInDTO() {
        // Do nothing
    }

    public String getAccountantDate() {
        return this.accountantDate;
    }

    public void setAccountantDate(String accountantDate) {
        this.accountantDate = accountantDate;
    }

    public String getAgreementCode() {
        return this.agreementCode;
    }

    public void setAgreementCode(String agreementCode) {
        this.agreementCode = agreementCode;
    }

    public String getBankNitOne() {
        return this.bankNitOne;
    }

    public void setBankNitOne(String bankNitOne) {
        this.bankNitOne = bankNitOne;
    }

    public String getBankNitTwo() {
        return this.bankNitTwo;
    }

    public void setBankNitTwo(String bankNitTwo) {
        this.bankNitTwo = bankNitTwo;
    }

    public String getBillNumber() {
        return this.billNumber;
    }

    public void setBillNumber(String billNumber) {
        this.billNumber = billNumber;
    }

    public String getBillingCompanyAgreementOne() {
        return this.billingCompanyAgreementOne;
    }

    public void setBillingCompanyAgreementOne(String billingCompanyAgreementOne) {
        this.billingCompanyAgreementOne = billingCompanyAgreementOne;
    }

    public String getBillingCompanyAgreementTwo() {
        return this.billingCompanyAgreementTwo;
    }

    public void setBillingCompanyAgreementTwo(String billingCompanyAgreementTwo) {
        this.billingCompanyAgreementTwo = billingCompanyAgreementTwo;
    }

    public String getBillingCompanyNameThree() {
        return this.billingCompanyNameThree;
    }

    public void setBillingCompanyNameThree(String billingCompanyNameThree) {
        this.billingCompanyNameThree = billingCompanyNameThree;
    }

    public String getBillingCompanyNameOne() {
        return this.billingCompanyNameOne;
    }

    public void setBillingCompanyNameOne(String billingCompanyNameOne) {
        this.billingCompanyNameOne = billingCompanyNameOne;
    }

    public String getBillingCompanyNameTwo() {
        return this.billingCompanyNameTwo;
    }

    public void setBillingCompanyNameTwo(String billingCompanyNameTwo) {
        this.billingCompanyNameTwo = billingCompanyNameTwo;
    }

    public String getCompensationCode() {
        return this.compensationCode;
    }

    public void setCompensationCode(String compensationCode) {
        this.compensationCode = compensationCode;
    }

    public String getOfficeCode() {
        return this.officeCode;
    }

    public void setOfficeCode(String officeCode) {
        this.officeCode = officeCode;
    }

    public String getReferenceFieldFive() {
        return this.referenceFieldFive;
    }

    public void setReferenceFieldFive(String referenceFieldFive) {
        this.referenceFieldFive = referenceFieldFive;
    }

    public String getReferenceFieldFour() {
        return this.referenceFieldFour;
    }

    public void setReferenceFieldFour(String referenceFieldFour) {
        this.referenceFieldFour = referenceFieldFour;
    }

    public String getReferenceFieldThree() {
        return this.referenceFieldThree;
    }

    public void setReferenceFieldThree(String referenceFieldThree) {
        this.referenceFieldThree = referenceFieldThree;
    }

    public String getReferenceFieldTwo() {
        return this.referenceFieldTwo;
    }

    public void setReferenceFieldTwo(String referenceFieldTwo) {
        this.referenceFieldTwo = referenceFieldTwo;
    }

    public String getTransactionChannel() {
        return this.transactionChannel;
    }

    public void setTransactionChannel(String transactionChannel) {
        this.transactionChannel = transactionChannel;
    }

    public String getTransactionDate() {
        return this.transactionDate;
    }

    public void setTransactionDate(String transactionDate) {
        this.transactionDate = transactionDate;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("GetBillAmountInDTO [accountantDate=").append(this.accountantDate).append(", agreementCode=").append(this.agreementCode).append(", bankNitOne=").append(this.bankNitOne).append(", bankNitTwo=").append(this.bankNitTwo).append(", billNumber=").append(this.billNumber).append(", billingCompanyAgreementOne=").append(this.billingCompanyAgreementOne).append(", billingCompanyAgreementTwo=").append(this.billingCompanyAgreementTwo).append(", billingCompanyNameThree=").append(this.billingCompanyNameThree).append(", billingCompanyNameOne=").append(this.billingCompanyNameOne).append(", billingCompanyNameTwo=").append(this.billingCompanyNameTwo).append(", compensationCode=").append(this.compensationCode).append(", officeCode=").append(this.officeCode).append(", referenceFieldFive=").append(this.referenceFieldFive).append(", referenceFieldFour=").append(this.referenceFieldFour).append(", referenceFieldThree=").append(this.referenceFieldThree).append(", referenceFieldTwo=").append(this.referenceFieldTwo).append(", transactionChannel=").append(this.transactionChannel).append(", transactionDate=").append(this.transactionDate).append(']');
        return builder.toString();
    }
}

