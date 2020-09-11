package co.moviired.supportp2pvalidatortransaction.common.provider.mahindra;

import co.moviired.supportp2pvalidatortransaction.common.provider.IProviderProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;

import static co.moviired.supportp2pvalidatortransaction.common.util.Constants.MAHINDRA_PROPERTIES_PREFIX;

@Data
@EqualsAndHashCode(callSuper = true)
@ConfigurationProperties(prefix = MAHINDRA_PROPERTIES_PREFIX)
public class MahindraProperties extends IProviderProperties {

    // Login
    private String loginType;
    private String loginSource;
    private String loginProvider;
    private String loginLanguage1;
    private String loginOtpReq;
    private String loginIsPinCheckReq;

    // Get Balance
    private String getBalanceType;
    private String getBalanceSource;
    private String getBalanceProvider;
    private String getBalancePayId;
    private String getBalanceLanguage1;

    // Pay bill
    private String payBillType;
    private String payBillSubType;
    private String payBillPaymentInstrument;
    private String payBillPayId;
    private String payBillProvider;
    private String payBillBProvider;
    private String payBillLanguage1;
    private String payBillSource;

    // Pay bill offline
    private String offlinePaymentType;
    private String offlinePaymentProvider;
    private String offlinePaymentBProvider;
    private String offlinePaymentPaymentInstrument;
    private String offlinePaymentPayId;
    private String offlinePaymentLanguage1;
    private String offlinePaymentRefNo;

    // Reverse transaction
    private String reverseType;
    private String reverseIsTcpCheckReq;

    // Force cash out
    private String forceCashOutType;
    private String forceCashOutProvider;
    private String forceCashOutPayId;
    private String forceCashOutMsisdn1;
    private String forceCashOutProvider2;
    private String forceCashOutPayId2;
    private String forceCashOutLanguage1;
    private String forceCashOutLanguage2;

    // User Query Info
    private String userQueryInfoType;
    private String userQueryInfoProvider;
    private String userQueryInfoUserType;

    // Frozen Balance
    private String frozenBalanceType;
    private String frozenBalanceUserType;
    private String frozenBalanceReleaseAfterDay;
    private String frozenBalancePayId;
    private String frozenBalanceLanguage1;
    private String frozenBalanceProviderId;

    // Defrost Balance
    private String defrostBalanceType;
    private String defrostBalanceLanguage1;

    // Send Money
    private String sendMoneyType;
    private String sendMoneyProvider;
    private String sendMoneyProvider2;
    private String sendMoneyPayId;
    private String sendMoneyPayId2;
    private String sendMoneyLanguage1;

    // Request Money
    private String askForMoneyType;
    private String askForMoneyPayeeProvider;
    private String askForMoneyPayeeInstr;
    private String askForMoneyPayerProvider;
    private String askForMoneyPayerInst;
    private String askForMoneyLanguage1;
    private String askForMoneyLanguage2;
    private String askForMoneyBlockSms;

    // Request Money confirmation
    private String askForMoneyConfirmationType;
    private String askForMoneyConfirmationProvider;
    private String askForMoneyConfirmationLanguage1;
    private String askForMoneyConfirmationLanguage2;
}
