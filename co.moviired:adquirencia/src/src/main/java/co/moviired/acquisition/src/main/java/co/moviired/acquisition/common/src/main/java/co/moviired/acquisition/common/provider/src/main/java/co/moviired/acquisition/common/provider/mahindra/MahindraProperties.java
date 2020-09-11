package co.moviired.acquisition.common.provider.mahindra;

import co.moviired.acquisition.common.provider.IProviderProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;

import static co.moviired.acquisition.common.util.ConstantsHelper.MAHINDRA_PROPERTIES_PREFIX;

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
}
