package co.moviired.supportp2pvalidatortransaction.common.provider.mahindra;

import co.moviired.supportp2pvalidatortransaction.common.provider.IProviderFactory;

import java.text.SimpleDateFormat;
import java.util.Date;

import static co.moviired.supportp2pvalidatortransaction.common.util.Constants.EMPTY_STRING;

public class MahindraFactory extends IProviderFactory<MahindraProperties> {

    private final SimpleDateFormat dateFormat;

    public MahindraFactory(MahindraProperties mahindraProperties) {
        super(mahindraProperties);
        this.dateFormat = new SimpleDateFormat("yyyyMMddHHmmss.SSS");
    }

    public MahindraDTO getLoginRequest(String phoneNumber, String password) {
        return MahindraDTO.builder()
                .isPinCheckReq(providerProperties.getLoginIsPinCheckReq())
                .language1(providerProperties.getLoginLanguage1())
                .mPin(password)
                .msisdn(phoneNumber)
                .otpReq(providerProperties.getLoginOtpReq())
                .provider(providerProperties.getLoginProvider())
                .source(providerProperties.getLoginSource())
                .type(providerProperties.getLoginType())
                .build();
    }

    public MahindraDTO getBalanceRequest(String phoneNumber, String password) {
        return MahindraDTO.builder()
                .type(providerProperties.getGetBalanceType())
                .msisdn(phoneNumber)
                .provider(providerProperties.getGetBalanceProvider())
                .payId(providerProperties.getGetBalancePayId())
                .mPin(password)
                .pin(password)
                .language1(providerProperties.getGetBalanceLanguage1())
                .source(providerProperties.getGetBalanceSource())
                .build();
    }

    public MahindraDTO createValidateUser(String msisdn) {
        return MahindraDTO.builder()
                .msisdn(msisdn)
                .provider(providerProperties.getUserQueryInfoProvider())
                .type(providerProperties.getUserQueryInfoType())
                .userType(providerProperties.getUserQueryInfoUserType())
                .build();
    }

    public MahindraDTO requestFrozenBalance(Double amount, String msisdn, String ftxId) {
        return MahindraDTO.builder()
                .amount(String.valueOf(amount))
                .ftxnId(ftxId)
                .language1(providerProperties.getFrozenBalanceLanguage1())
                .msisdn(msisdn)
                .payId(providerProperties.getFrozenBalancePayId())
                .providerId(providerProperties.getFrozenBalanceProviderId())
                .releaseAfterDays(providerProperties.getFrozenBalanceReleaseAfterDay())
                .type(providerProperties.getFrozenBalanceType())
                .userType(providerProperties.getFrozenBalanceUserType())
                .build();
    }

    public MahindraDTO requestSendMoney(Double amount, String msisdn, String msisdn2) {
        return MahindraDTO.builder()
                .amount(String.valueOf(amount))
                .language1(providerProperties.getSendMoneyLanguage1())
                .msisdn(msisdn)
                .msisdn2(msisdn2)
                .payId(providerProperties.getSendMoneyPayId())
                .payId2(providerProperties.getSendMoneyPayId2())
                .provider(providerProperties.getSendMoneyProvider())
                .provider2(providerProperties.getSendMoneyProvider2())
                .type(providerProperties.getSendMoneyType())
                .build();
    }

    public MahindraDTO requestAskForMoney(Double amount, String msisdn, String msisdn2, String pin) {
        return MahindraDTO.builder()
                .amount(String.valueOf(amount))
                .blockSms(providerProperties.getAskForMoneyBlockSms())
                .language1(providerProperties.getAskForMoneyLanguage1())
                .language2(providerProperties.getAskForMoneyLanguage2())
                .msisdn(msisdn)
                .msisdn2(msisdn2)
                .payeeInstr(providerProperties.getAskForMoneyPayeeInstr())
                .payeeProvider(providerProperties.getAskForMoneyPayeeProvider())
                .payerInstr(providerProperties.getAskForMoneyPayerInst())
                .payerProvider(providerProperties.getAskForMoneyPayerProvider())
                .txnMode(EMPTY_STRING)
                .pin(pin)
                .mPin(pin)
                .type(providerProperties.getAskForMoneyType())
                .build();
    }

    public MahindraDTO requestUFrozenBalance(String txid) {
        return MahindraDTO.builder()
                .ftxnId(dateFormat.format(new Date()))
                .holdTxnId(txid)
                .language1(providerProperties.getDefrostBalanceLanguage1())
                .type(providerProperties.getDefrostBalanceType())
                .build();
    }

    public MahindraDTO getRequestMoneyConfirmation(String msisdn, String msisdn2, String status, String txid, String pin) {
        return MahindraDTO.builder()
                .language1(providerProperties.getAskForMoneyConfirmationLanguage1())
                .language2(providerProperties.getAskForMoneyConfirmationLanguage2())
                .provider(providerProperties.getAskForMoneyConfirmationProvider())
                .type(providerProperties.getAskForMoneyConfirmationType())
                .msisdn(msisdn)
                .msisdn2(msisdn2)
                .status(status)
                .pin(pin)
                .mPin(pin)
                .txnId(txid)
                .blockSms(providerProperties.getAskForMoneyBlockSms())
                .build();
    }
}

