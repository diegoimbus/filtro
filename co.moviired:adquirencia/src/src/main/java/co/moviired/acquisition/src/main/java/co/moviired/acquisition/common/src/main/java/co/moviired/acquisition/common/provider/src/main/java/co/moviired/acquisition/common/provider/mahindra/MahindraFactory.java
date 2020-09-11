package co.moviired.acquisition.common.provider.mahindra;

import co.moviired.acquisition.common.provider.IProviderFactory;

public class MahindraFactory extends IProviderFactory<MahindraProperties> {

    public MahindraFactory(MahindraProperties mahindraProperties) {
        super(mahindraProperties);
    }

    public final MahindraDTO getLoginRequest(String phoneNumber, String password) {
        return MahindraDTO.builder()
                .isPinCheckReq(getProviderProperties().getLoginIsPinCheckReq())
                .language1(getProviderProperties().getLoginLanguage1())
                .mPin(password)
                .msisdn(phoneNumber)
                .otpReq(getProviderProperties().getLoginOtpReq())
                .provider(getProviderProperties().getLoginProvider())
                .source(getProviderProperties().getLoginSource())
                .type(getProviderProperties().getLoginType())
                .build();
    }

    public final MahindraDTO getBalanceRequest(String phoneNumber, String password) {
        return MahindraDTO.builder()
                .type(getProviderProperties().getGetBalanceType())
                .msisdn(phoneNumber)
                .provider(getProviderProperties().getGetBalanceProvider())
                .payId(getProviderProperties().getGetBalancePayId())
                .mPin(password)
                .pin(password)
                .language1(getProviderProperties().getGetBalanceLanguage1())
                .source(getProviderProperties().getGetBalanceSource())
                .build();
    }
}

