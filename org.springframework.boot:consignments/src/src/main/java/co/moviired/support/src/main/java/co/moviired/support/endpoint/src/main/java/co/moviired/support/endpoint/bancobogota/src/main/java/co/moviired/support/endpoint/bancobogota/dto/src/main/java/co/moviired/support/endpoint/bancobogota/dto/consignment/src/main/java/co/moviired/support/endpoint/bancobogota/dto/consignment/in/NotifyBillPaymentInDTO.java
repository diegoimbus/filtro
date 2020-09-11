package co.moviired.support.endpoint.bancobogota.dto.consignment.in;

import javax.xml.bind.annotation.*;
import java.io.Serializable;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "notifyBillPaymentInDTO",
        propOrder = {"accountNumber", "accountType", "accountantDate", "agreementCode", "bankNitOne", "bankNitTwo", "billNumber", "billTotalAmount", "billingCompanyAgreementOne", "billingCompanyAgreementTwo", "billingCompanyNameOne", "billingCompanyNameTwo", "checkAmount", "compensationCodeOne", "compensationCodeTwo", "currencyType", "eanCode", "effectiveAmount", "officeCode", "referenceFieldFive", "referenceFieldFour", "referenceFieldThree", "referenceFieldTwo", "transactionChannel", "transactionDate", "uuid", "workingDay"}
)
@XmlRootElement(name = "body")
public class NotifyBillPaymentInDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    @XmlElement(required = true)
    private String accountNumber;
    @XmlElement(required = true)
    private String accountType;
    @XmlElement(required = true)
    private String accountantDate;
    @XmlElement(required = true)
    private String agreementCode;
    @XmlElement(required = true)
    private String bankNitOne;
    @XmlElement(required = true)
    private String bankNitTwo;
    @XmlElement(required = true)
    private String billNumber;
    @XmlElement(required = true)
    private String billTotalAmount;
    @XmlElement(required = true)
    private String billingCompanyAgreementOne;
    @XmlElement(required = true)
    private String billingCompanyAgreementTwo;
    @XmlElement(required = true)
    private String billingCompanyNameOne;
    @XmlElement(required = true)
    private String billingCompanyNameTwo;

    private String checkAmount;
    @XmlElement(required = true)
    private String compensationCodeOne;
    @XmlElement(required = true)
    private String compensationCodeTwo;
    @XmlElement(required = true)
    private String currencyType;
    @XmlElement(required = true)
    private String eanCode;
    @XmlElement(required = true)
    private String effectiveAmount;
    @XmlElement(required = true)
    private String officeCode;

    private String referenceFieldFive;
    @XmlElement(required = true)
    private String referenceFieldFour;
    @XmlElement(required = true)
    private String referenceFieldThree;
    @XmlElement(required = true)
    private String referenceFieldTwo;
    @XmlElement(required = true)
    private String transactionChannel;
    @XmlElement(required = true)
    private String transactionDate;
    @XmlElement(required = true)
    private String uuid;
    @XmlElement(required = true)
    private String workingDay;

    public NotifyBillPaymentInDTO() {
        // Do nothing
    }

    public String getBillingCompanyNameOne() {
        return this.billingCompanyNameOne;
    }

    public void setBillingCompanyNameOne(String billingCompanyNameOne) {
        this.billingCompanyNameOne = billingCompanyNameOne;
    }

    public String getBillingCompanyAgreementOne() {
        return this.billingCompanyAgreementOne;
    }

    public void setBillingCompanyAgreementOne(String billingCompanyAgreementOne) {
        this.billingCompanyAgreementOne = billingCompanyAgreementOne;
    }

    public String getBankNitOne() {
        return this.bankNitOne;
    }

    public void setBankNitOne(String bankNitOne) {
        this.bankNitOne = bankNitOne;
    }

    public String getTransactionDate() {
        return this.transactionDate;
    }

    public void setTransactionDate(String transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getUuid() {
        return this.uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getTransactionChannel() {
        return this.transactionChannel;
    }

    public void setTransactionChannel(String transactionChannel) {
        this.transactionChannel = transactionChannel;
    }

    public String getOfficeCode() {
        return this.officeCode;
    }

    public void setOfficeCode(String officeCode) {
        this.officeCode = officeCode;
    }

    public String getCompensationCodeOne() {
        return this.compensationCodeOne;
    }

    public void setCompensationCodeOne(String compensationCodeOne) {
        this.compensationCodeOne = compensationCodeOne;
    }

    public String getEanCode() {
        return this.eanCode;
    }

    public void setEanCode(String eanCode) {
        this.eanCode = eanCode;
    }

    public String getAgreementCode() {
        return this.agreementCode;
    }

    public void setAgreementCode(String agreementCode) {
        this.agreementCode = agreementCode;
    }

    public String getCurrencyType() {
        return this.currencyType;
    }

    public void setCurrencyType(String currencyType) {
        this.currencyType = currencyType;
    }

    public String getBillTotalAmount() {
        return this.billTotalAmount;
    }

    public void setBillTotalAmount(String billTotalAmount) {
        this.billTotalAmount = billTotalAmount;
    }

    public String getEffectiveAmount() {
        return this.effectiveAmount;
    }

    public void setEffectiveAmount(String effectiveAmount) {
        this.effectiveAmount = effectiveAmount;
    }

    public String getCheckAmount() {
        return this.checkAmount;
    }

    public void setCheckAmount(String checkAmount) {
        this.checkAmount = checkAmount;
    }

    public String getAccountNumber() {
        return this.accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getAccountType() {
        return this.accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getWorkingDay() {
        return this.workingDay;
    }

    public void setWorkingDay(String workingDay) {
        this.workingDay = workingDay;
    }

    public String getCompensationCodeTwo() {
        return this.compensationCodeTwo;
    }

    public void setCompensationCodeTwo(String compensationCodeTwo) {
        this.compensationCodeTwo = compensationCodeTwo;
    }

    public String getAccountantDate() {
        return this.accountantDate;
    }

    public void setAccountantDate(String accountantDate) {
        this.accountantDate = accountantDate;
    }

    public String getBillNumber() {
        return this.billNumber;
    }

    public void setBillNumber(String billNumber) {
        this.billNumber = billNumber;
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

    public String getBillingCompanyAgreementTwo() {
        return this.billingCompanyAgreementTwo;
    }

    public void setBillingCompanyAgreementTwo(String billingCompanyAgreementTwo) {
        this.billingCompanyAgreementTwo = billingCompanyAgreementTwo;
    }

    public String getBillingCompanyNameTwo() {
        return this.billingCompanyNameTwo;
    }

    public void setBillingCompanyNameTwo(String billingCompanyNameTwo) {
        this.billingCompanyNameTwo = billingCompanyNameTwo;
    }

    public String getBankNitTwo() {
        return this.bankNitTwo;
    }

    public void setBankNitTwo(String bankNitTwo) {
        this.bankNitTwo = bankNitTwo;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder(506);
        builder.append("NotifyBillPaymentInDTO [billingCompanyNameOne=").append(this.billingCompanyNameOne).append(", billingCompanyAgreementOne=").append(this.billingCompanyAgreementOne).append(", bankNitOne=").append(this.bankNitOne).append(", transactionDate=").append(this.transactionDate).append(", uuid=").append(this.uuid).append(", transaccionChannel=").append(this.transactionChannel).append(", officeCode=").append(this.officeCode).append(", compensationCodeOne=").append(this.compensationCodeOne).append(", eanCode=").append(this.eanCode).append(", agreementCode=").append(this.agreementCode).append(", currencyType=").append(this.currencyType).append(", billTotalAmount=").append(this.billTotalAmount).append(", effectiveAmount=").append(this.effectiveAmount).append(", checkAmount=").append(this.checkAmount).append(", accountNumber=").append(this.accountNumber).append(", accountType=").append(this.accountType).append(", workingDay=").append(this.workingDay).append(", compensationCodeTwo=").append(this.compensationCodeTwo).append(", accountantDate=").append(this.accountantDate).append(", billNumber=").append(this.billNumber).append(", referenceFieldTwo=").append(this.referenceFieldTwo).append(", referenceFieldThree=").append(this.referenceFieldThree).append(", referenceFieldFour=").append(this.referenceFieldFour).append(", referenceFieldFive=").append(this.referenceFieldFive).append(", billingCompanyNameTwo=").append(this.billingCompanyNameTwo).append(", billingCompanyAgreementTwo=").append(this.billingCompanyAgreementTwo).append(", bankNitTwo=").append(this.bankNitTwo).append(']');
        return builder.toString();
    }
}

