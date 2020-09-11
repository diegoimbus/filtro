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
        name = "notifyBillPaymentInCoreDTO",
        propOrder = {"accountNumber", "accountType", "accountantDate", "agreementCode", "bankNitOne", "bankNitTwo", "billNumber", "billTotalAmount", "billingCompanyAgreementOne", "billingCompanyAgreementTwo", "billingCompanyNameOne", "billingCompanyNameTwo", "checkAmount", "compensationCodeOne", "compensationCodeTwo", "currencyType", "eanCode", "effectiveAmount", "officeCode", "referenceFieldFive", "referenceFieldFour", "referenceFieldThree", "referenceFieldTwo", "transactionChannel", "transactionDate", "uuid", "workingDay"}
)
@XmlRootElement
public class NotifyBillPaymentInCoreDTO extends RequestCoreDTO {
    protected static final long serialVersionUID = 1L;
    protected String billingCompanyNameOne;
    protected String billingCompanyAgreementOne;
    protected String bankNitOne;
    protected String transactionDate;
    protected String uuid;
    protected String transactionChannel;
    protected String officeCode;
    protected String compensationCodeOne;
    protected String eanCode;
    protected String agreementCode;
    protected String currencyType;
    protected String billTotalAmount;
    protected String effectiveAmount;
    protected String checkAmount;
    protected String accountNumber;
    protected String accountType;
    protected String workingDay;
    protected String compensationCodeTwo;
    protected String accountantDate;
    protected String billNumber;
    protected String referenceFieldTwo;
    protected String referenceFieldThree;
    protected String referenceFieldFour;
    protected String referenceFieldFive;
    protected String billingCompanyNameTwo;
    protected String billingCompanyAgreementTwo;
    protected String bankNitTwo;

    public NotifyBillPaymentInCoreDTO() {
        // Do nothing
    }

    @Override
    public String toString() {
        return "NotifyBillPaymentInCoreDTO [billingCompanyNameOne=" + this.billingCompanyNameOne + ", billingCompanyAgreementOne=" + this.billingCompanyAgreementOne + ", bankNitOne=" + this.bankNitOne + ", transactionDate=" + this.transactionDate + ", uuid=" + this.uuid + ", transactionChannel=" + this.transactionChannel + ", officeCode=" + this.officeCode + ", compensationCodeOne=" + this.compensationCodeOne + ", eanCode=" + this.eanCode + ", agreementCode=" + this.agreementCode + ", currencyType=" + this.currencyType + ", billTotalAmount=" + this.billTotalAmount + ", effectiveAmount=" + this.effectiveAmount + ", checkAmount=" + this.checkAmount + ", accountNumber=" + this.accountNumber + ", accountType=" + this.accountType + ", workingDay=" + this.workingDay + ", compensationCodeTwo=" + this.compensationCodeTwo + ", accountantDate=" + this.accountantDate + ", billNumber=" + this.billNumber + ", referenceFieldTwo=" + this.referenceFieldTwo + ", referenceFieldThree=" + this.referenceFieldThree + ", referenceFieldFour=" + this.referenceFieldFour + ", referenceFieldFive=" + this.referenceFieldFive + ", billingCompanyNameTwo=" + this.billingCompanyNameTwo + ", billingCompanyAgreementTwo=" + this.billingCompanyAgreementTwo + ", bankNitTwo=" + this.bankNitTwo + "]";
    }
}

