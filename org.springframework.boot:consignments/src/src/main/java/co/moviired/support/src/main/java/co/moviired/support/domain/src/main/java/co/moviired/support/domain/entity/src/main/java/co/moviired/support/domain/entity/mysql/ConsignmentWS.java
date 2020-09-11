package co.moviired.support.domain.entity.mysql;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;


@Entity
@Data
@Table(name = "ws_consignment")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ConsignmentWS implements Serializable {
    private static final long serialVersionUID = 527701445404960468L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", length = 11)
    private Integer id;

    @Column(name = "account_number", length = 12)
    private String accountNumber;

    @Column(name = "account_type", length = 2)
    private String accountType;

    @Column(name = "accountant_date", length = 10)
    private String accountantDate;

    @Column(name = "agreement_code", length = 5)
    private String agreementCode;

    @Column(name = "bank_nit_one", length = 12)
    private String bankNitOne;

    @Column(name = "bank_nit_two", length = 12)
    private String bankNitTwo;

    @Column(name = "bill_number", length = 15)
    private String billNumber;

    @Column(name = "bill_total_amount", length = 30)
    private String billTotalAmount;

    @Column(name = "billing_company_agreement_one", length = 15)
    private String billingCompanyAgreementOne;

    @Column(name = "billing_company_agreement_two", length = 15)
    private String billingCompanyAgreementTwo;

    @Column(name = "billing_company_name_one", length = 30)
    private String billingCompanyNameOne;

    @Column(name = "billing_company_name_two", length = 30)
    private String billingCompanyNameTwo;

    @Column(name = "check_amount", length = 15)
    private String checkAmount;

    @Column(name = "compensation_code_one", length = 20)
    private String compensationCodeOne;

    @Column(name = "compensation_code_two", length = 20)
    private String compensationCodeTwo;

    @Column(name = "currency_type", length = 5)
    private String currencyType;

    @Column(name = "ean_code", length = 20)
    private String eanCode;

    @Column(name = "effective_amount", length = 15)
    private String effectiveAmount;

    @Column(name = "office_code", length = 5)
    private String officeCode;

    @Column(name = "reference_field_five", length = 25)
    private String referenceFieldFive;

    @Column(name = "reference_field_four", length = 25)
    private String referenceFieldFour;

    @Column(name = "reference_field_three", length = 25)
    private String referenceFieldThree;

    @Column(name = "reference_field_two", length = 25)
    private String referenceFieldTwo;

    @Column(name = "transaction_channel", length = 5)
    private String transactionChannel;

    @Column(name = "transaction_date", length = 15)
    private String transactionDate;

    @Column(name = "uuid", length = 40)
    private String uuid;

    @Column(name = "working_day", length = 15)
    private String workingDay;

    @Column(name="create_date")
    private Timestamp createDate;

    @Column(name = "mahindra_transaction_id")
    private String mahindraTransactionId;

    @Column(name = "status_code", length = 2)
    private String statusCode;

    @Column(name = "revert_transaction_id")
    private String revertTransactionId;

    @Column(name = "correlation_id", length = 40)
    private String correlationId;

    @Column(name = "correlation_id_revert", length = 40)
    private String correlationIdRevert;

}

