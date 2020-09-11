package co.moviired.support.endpoint.bancobogota.dto.consignment.in;

import javax.xml.bind.annotation.*;
import java.io.Serializable;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "revertBillPaymentInDTO",
        propOrder = {"accountNumberRevert", "accountTypeRevert", "accountantDateRevert", "agreementCodeRevert", "bankNit", "bankNitRevert", "billNumberRevert", "billingCompanyAgreement", "billingCompanyAgreementRevert", "billingCompanyName1", "billingCompanyNameRevert", "checkAmountRevert", "clientDocumentNumber", "clientDocumentNumberRevert", "companyDocumentTypeRevert", "compensationCode", "compensationCodeRevertOne", "compensationCodeRevertTwo", "currencyTypeRevert", "documentType", "effectiveAmountRevert", "officeCode", "officeCodeRevert", "paymentChannelRevert", "referenceFieldFive", "referenceFieldFour", "referenceFieldThree", "referenceFieldTwo", "totalBillAmountRevert", "transaccionChannel", "transactionDate", "uuidRevert", "workingDayRevert"}
)
@XmlRootElement(name = "body")
public class RevertBillPaymentInDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    @XmlElement(required = true)
    private String billingCompanyName1;
    @XmlElement(required = true)
    private String billingCompanyAgreement;
    @XmlElement(required = true)
    private String bankNit;
    @XmlElement(required = true)
    private String transactionDate;
    @XmlElement(required = true)
    private String transaccionChannel;
    @XmlElement(required = true)
    private String officeCode;
    @XmlElement(required = true)
    private String compensationCode;
    @XmlElement(required = true)
    private String documentType;
    @XmlElement(required = true)
    private String clientDocumentNumber;
    @XmlElement(required = true)
    private String uuidRevert;
    @XmlElement(required = true)
    private String paymentChannelRevert;
    @XmlElement(required = true)
    private String officeCodeRevert;
    @XmlElement(required = true)
    private String compensationCodeRevertOne;
    @XmlElement(required = true)
    private String companyDocumentTypeRevert;
    @XmlElement(required = true)
    private String clientDocumentNumberRevert;
    @XmlElement(required = true)
    private String accountantDateRevert;
    @XmlElement(required = true)
    private String agreementCodeRevert;
    @XmlElement(required = true)
    private String billNumberRevert;
    @XmlElement(required = true)
    private String referenceFieldTwo;
    @XmlElement(required = true)
    private String referenceFieldThree;
    @XmlElement(required = true)
    private String referenceFieldFour;

    private String referenceFieldFive;
    @XmlElement(required = true)
    private String currencyTypeRevert;
    @XmlElement(required = true)
    private String totalBillAmountRevert;
    @XmlElement(required = true)
    private String effectiveAmountRevert;

    private String checkAmountRevert;
    @XmlElement(required = true)
    private String accountNumberRevert;
    @XmlElement(required = true)
    private String accountTypeRevert;
    @XmlElement(required = true)
    private String workingDayRevert;
    @XmlElement(required = true)
    private String compensationCodeRevertTwo;
    @XmlElement(required = true)
    private String billingCompanyNameRevert;
    @XmlElement(required = true)
    private String billingCompanyAgreementRevert;
    @XmlElement(required = true)
    private String bankNitRevert;

    public RevertBillPaymentInDTO() {
        // Do nothing
    }

    public String getBillingCompanyName1() {
        return this.billingCompanyName1;
    }

    public void setBillingCompanyName1(String billingCompanyName1) {
        this.billingCompanyName1 = billingCompanyName1;
    }

    public String getBillingCompanyAgreement() {
        return this.billingCompanyAgreement;
    }

    public void setBillingCompanyAgreement(String billingCompanyAgreement) {
        this.billingCompanyAgreement = billingCompanyAgreement;
    }

    public String getBankNit() {
        return this.bankNit;
    }

    public void setBankNit(String bankNit) {
        this.bankNit = bankNit;
    }

    public String getTransactionDate() {
        return this.transactionDate;
    }

    public void setTransactionDate(String transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getTransaccionChannel() {
        return this.transaccionChannel;
    }

    public void setTransaccionChannel(String transaccionChannel) {
        this.transaccionChannel = transaccionChannel;
    }

    public String getOfficeCode() {
        return this.officeCode;
    }

    public void setOfficeCode(String officeCode) {
        this.officeCode = officeCode;
    }

    public String getCompensationCode() {
        return this.compensationCode;
    }

    public void setCompensationCode(String compensationCode) {
        this.compensationCode = compensationCode;
    }

    public String getDocumentType() {
        return this.documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public String getClientDocumentNumber() {
        return this.clientDocumentNumber;
    }

    public void setClientDocumentNumber(String clientDocumentNumber) {
        this.clientDocumentNumber = clientDocumentNumber;
    }

    public String getUuidRevert() {
        return this.uuidRevert;
    }

    public void setUuidRevert(String uuidRevert) {
        this.uuidRevert = uuidRevert;
    }

    public String getPaymentChannelRevert() {
        return this.paymentChannelRevert;
    }

    public void setPaymentChannelRevert(String paymentChannelRevert) {
        this.paymentChannelRevert = paymentChannelRevert;
    }

    public String getOfficeCodeRevert() {
        return this.officeCodeRevert;
    }

    public void setOfficeCodeRevert(String officeCodeRevert) {
        this.officeCodeRevert = officeCodeRevert;
    }

    public String getCompensationCodeRevertOne() {
        return this.compensationCodeRevertOne;
    }

    public void setCompensationCodeRevertOne(String compensationCodeRevertOne) {
        this.compensationCodeRevertOne = compensationCodeRevertOne;
    }

    public String getCompanyDocumentTypeRevert() {
        return this.companyDocumentTypeRevert;
    }

    public void setCompanyDocumentTypeRevert(String companyDocumentTypeRevert) {
        this.companyDocumentTypeRevert = companyDocumentTypeRevert;
    }

    public String getClientDocumentNumberRevert() {
        return this.clientDocumentNumberRevert;
    }

    public void setClientDocumentNumberRevert(String clientDocumentNumberRevert) {
        this.clientDocumentNumberRevert = clientDocumentNumberRevert;
    }

    public String getAccountantDateRevert() {
        return this.accountantDateRevert;
    }

    public void setAccountantDateRevert(String accountantDateRevert) {
        this.accountantDateRevert = accountantDateRevert;
    }

    public String getAgreementCodeRevert() {
        return this.agreementCodeRevert;
    }

    public void setAgreementCodeRevert(String agreementCodeRevert) {
        this.agreementCodeRevert = agreementCodeRevert;
    }

    public String getBillNumberRevert() {
        return this.billNumberRevert;
    }

    public void setBillNumberRevert(String billNumberRevert) {
        this.billNumberRevert = billNumberRevert;
    }

    public String getReferenceFieldTwo() {
        return this.referenceFieldTwo;
    }

    public void setReferenceFieldTwo(String referenceFieldTwo) {
        this.referenceFieldTwo = referenceFieldTwo;
    }

    public String getReferenceFieldThree() {
        return this.referenceFieldThree;
    }

    public void setReferenceFieldThree(String referenceFieldThree) {
        this.referenceFieldThree = referenceFieldThree;
    }

    public String getReferenceFieldFour() {
        return this.referenceFieldFour;
    }

    public void setReferenceFieldFour(String referenceFieldFour) {
        this.referenceFieldFour = referenceFieldFour;
    }

    public String getReferenceFieldFive() {
        return this.referenceFieldFive;
    }

    public void setReferenceFieldFive(String referenceFieldFive) {
        this.referenceFieldFive = referenceFieldFive;
    }

    public String getCurrencyTypeRevert() {
        return this.currencyTypeRevert;
    }

    public void setCurrencyTypeRevert(String currencyTypeRevert) {
        this.currencyTypeRevert = currencyTypeRevert;
    }

    public String getTotalBillAmountRevert() {
        return this.totalBillAmountRevert;
    }

    public void setTotalBillAmountRevert(String totalBillAmountRevert) {
        this.totalBillAmountRevert = totalBillAmountRevert;
    }

    public String getCheckAmountRevert() {
        return this.checkAmountRevert;
    }

    public void setCheckAmountRevert(String checkAmountRevert) {
        this.checkAmountRevert = checkAmountRevert;
    }

    public String getAccountNumberRevert() {
        return this.accountNumberRevert;
    }

    public void setAccountNumberRevert(String accountNumberRevert) {
        this.accountNumberRevert = accountNumberRevert;
    }

    public String getAccountTypeRevert() {
        return this.accountTypeRevert;
    }

    public void setAccountTypeRevert(String accountTypeRevert) {
        this.accountTypeRevert = accountTypeRevert;
    }

    public String getWorkingDayRevert() {
        return this.workingDayRevert;
    }

    public void setWorkingDayRevert(String workingDayRevert) {
        this.workingDayRevert = workingDayRevert;
    }

    public String getCompensationCodeRevertTwo() {
        return this.compensationCodeRevertTwo;
    }

    public void setCompensationCodeRevertTwo(String compensationCodeRevertTwo) {
        this.compensationCodeRevertTwo = compensationCodeRevertTwo;
    }

    public String getBillingCompanyNameRevert() {
        return this.billingCompanyNameRevert;
    }

    public void setBillingCompanyNameRevert(String billingCompanyNameRevert) {
        this.billingCompanyNameRevert = billingCompanyNameRevert;
    }

    public String getBillingCompanyAgreementRevert() {
        return this.billingCompanyAgreementRevert;
    }

    public void setBillingCompanyAgreementRevert(String billingCompanyAgreementRevert) {
        this.billingCompanyAgreementRevert = billingCompanyAgreementRevert;
    }

    public String getBankNitRevert() {
        return this.bankNitRevert;
    }

    public void setBankNitRevert(String bankNitRevert) {
        this.bankNitRevert = bankNitRevert;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("RevertBillPaymentInDTO [billingCompanyName1=").append(this.billingCompanyName1).append(", billingCompanyAgreement=").append(this.billingCompanyAgreement).append(", bankNit=").append(this.bankNit).append(", transactionDate=").append(this.transactionDate).append(", transaccionChannel=").append(this.transaccionChannel).append(", officeCode=").append(this.officeCode).append(", compensationCode=").append(this.compensationCode).append(", documentType=").append(this.documentType).append(", clientDocumentNumber=").append(this.clientDocumentNumber).append(", uuidRevert=").append(this.uuidRevert).append(", paymentChannelRevert=").append(this.paymentChannelRevert).append(", officeCodeRevert=").append(this.officeCodeRevert).append(", compensationCodeRevertOne=").append(this.compensationCodeRevertOne).append(", companyDocumentTypeRevert=").append(this.companyDocumentTypeRevert).append(", clientDocumentNumberRevert=").append(this.clientDocumentNumberRevert).append(", accountantDateRevert=").append(this.accountantDateRevert).append(", agreementCodeRevert=").append(this.agreementCodeRevert).append(", billNumberRevert=").append(this.billNumberRevert).append(", referenceFieldTwo=").append(this.referenceFieldTwo).append(", referenceFieldThree=").append(this.referenceFieldThree).append(", referenceFieldFour=").append(this.referenceFieldFour).append(", referenceFieldFive=").append(this.referenceFieldFive).append(", currencyTypeRevert=").append(this.currencyTypeRevert).append(", totalBillAmountRevert=").append(this.totalBillAmountRevert).append(", affectiveAmountRevert=").append(this.effectiveAmountRevert).append(", checkAmountRevert=").append(this.checkAmountRevert).append(", accountNumberRevert=").append(this.accountNumberRevert).append(", accountTypeRevert=").append(this.accountTypeRevert).append(", workingDayRevert=").append(this.workingDayRevert).append(", compensationCodeRevertTwo=").append(this.compensationCodeRevertTwo).append(", billingCompanyNameRevert=").append(this.billingCompanyNameRevert).append(", billingCompanyAgreementRevert=").append(this.billingCompanyAgreementRevert).append(", bankNitRevert=").append(this.bankNitRevert).append("]");
        return builder.toString();
    }

    public String getEffectiveAmountRevert() {
        return this.effectiveAmountRevert;
    }

    public void setEffectiveAmountRevert(String effectiveAmountRevert) {
        this.effectiveAmountRevert = effectiveAmountRevert;
    }
}

