package co.moviired.support.endpoint.bancobogota.dto.consignment.in;

import co.moviired.support.endpoint.bancobogota.dto.generics.RequestCoreDTO;
import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "revertBillPaymentInCoreDTO",
        propOrder = {"billingCompanyName1", "billingCompanyAgreement", "bankNit", "transactionDate", "transaccionChannel", "officeCode", "compensationCode", "documentType", "clientDocumentNumber", "uuidRevert", "paymentChannelRevert", "officeCodeRevert", "compensationCodeRevertOne", "companyDocumentTypeRevert", "clientDocumentNumberRevert", "accountantDateRevert", "agreementCodeRevert", "billNumberRevert", "referenceFieldTwo", "referenceFieldThree", "referenceFieldFour", "referenceFieldFive", "currencyTypeRevert", "totalBillAmountRevert", "affectiveAmountRevert", "checkAmountRevert", "accountNumberRevert", "accountTypeRevert", "workingDayRevert", "compensationCodeRevertTwo", "billingCompanyNameRevert", "billingCompanyAgreementRevert", "bankNitRevert", "trnsId", "message", "status"}
)
@XmlRootElement
public class RevertBillPaymentInCoreDTO extends RequestCoreDTO {
    protected static final long serialVersionUID = 1L;
    protected String billingCompanyName1;
    protected String billingCompanyAgreement;
    protected String bankNit;
    protected String transactionDate;
    protected String transaccionChannel;
    protected String officeCode;
    protected String compensationCode;
    protected String documentType;
    protected String clientDocumentNumber;
    protected String uuidRevert;
    protected String paymentChannelRevert;
    protected String officeCodeRevert;
    protected String compensationCodeRevertOne;
    protected String companyDocumentTypeRevert;
    protected String clientDocumentNumberRevert;
    protected String accountantDateRevert;
    protected String agreementCodeRevert;
    protected String billNumberRevert;
    protected String referenceFieldTwo;
    protected String referenceFieldThree;
    protected String referenceFieldFour;
    protected String referenceFieldFive;
    protected String currencyTypeRevert;
    protected String totalBillAmountRevert;
    protected String affectiveAmountRevert;
    protected String checkAmountRevert;
    protected String accountNumberRevert;
    protected String accountTypeRevert;
    protected String workingDayRevert;
    protected String compensationCodeRevertTwo;
    protected String billingCompanyNameRevert;
    protected String billingCompanyAgreementRevert;
    protected String bankNitRevert;
    protected String trnsId;
    protected String message;
    protected String status;

    public RevertBillPaymentInCoreDTO() {
        // Do nothing
    }

    @Override
    public String toString() {
        return "RevertBillPaymentInCoreDTO [billingCompanyName1=" + this.billingCompanyName1 + ", billingCompanyAgreement=" + this.billingCompanyAgreement + ", bankNit=" + this.bankNit + ", transactionDate=" + this.transactionDate + ", transaccionChannel=" + this.transaccionChannel + ", officeCode=" + this.officeCode + ", compensationCode=" + this.compensationCode + ", documentType=" + this.documentType + ", clientDocumentNumber=" + this.clientDocumentNumber + ", uuidRevert=" + this.uuidRevert + ", paymentChannelRevert=" + this.paymentChannelRevert + ", officeCodeRevert=" + this.officeCodeRevert + ", compensationCodeRevertOne=" + this.compensationCodeRevertOne + ", companyDocumentTypeRevert=" + this.companyDocumentTypeRevert + ", clientDocumentNumberRevert=" + this.clientDocumentNumberRevert + ", accountantDateRevert=" + this.accountantDateRevert + ", agreementCodeRevert=" + this.agreementCodeRevert + ", billNumberRevert=" + this.billNumberRevert + ", referenceFieldTwo=" + this.referenceFieldTwo + ", referenceFieldThree=" + this.referenceFieldThree + ", referenceFieldFour=" + this.referenceFieldFour + ", referenceFieldFive=" + this.referenceFieldFive + ", currencyTypeRevert=" + this.currencyTypeRevert + ", totalBillAmountRevert=" + this.totalBillAmountRevert + ", affectiveAmountRevert=" + this.affectiveAmountRevert + ", checkAmountRevert=" + this.checkAmountRevert + ", accountNumberRevert=" + this.accountNumberRevert + ", accountTypeRevert=" + this.accountTypeRevert + ", workingDayRevert=" + this.workingDayRevert + ", compensationCodeRevertTwo=" + this.compensationCodeRevertTwo + ", billingCompanyNameRevert=" + this.billingCompanyNameRevert + ", billingCompanyAgreementRevert=" + this.billingCompanyAgreementRevert + ", bankNitRevert=" + this.bankNitRevert + ", trnsId=" + this.trnsId + ", message=" + this.message + ", status=" + this.status + "]";
    }
}

