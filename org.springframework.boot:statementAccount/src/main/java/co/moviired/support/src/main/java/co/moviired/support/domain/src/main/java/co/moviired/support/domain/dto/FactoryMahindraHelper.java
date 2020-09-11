package co.moviired.support.domain.dto;

import co.moviired.support.domain.client.mahindra.MahindraDTO;
import co.moviired.support.properties.MahindraProperties;

public final class FactoryMahindraHelper {

    private FactoryMahindraHelper() {
        // Not is necessary this implementation
    }

    public static MahindraDTO getLoginRequest(MahindraProperties mahindraProperties, String phoneNumber, String password) {
        return MahindraDTO.builder()
                .isPinCheckReq(mahindraProperties.getLoginIsPinCheckReq())
                .language1(mahindraProperties.getLoginLanguage1())
                .mPin(password)
                .msisdn(phoneNumber)
                .otpReq(mahindraProperties.getLoginOtpReq())
                .provider(mahindraProperties.getLoginProvider())
                .source(mahindraProperties.getLoginSource())
                .type(mahindraProperties.getLoginType())
                .build();
    }
}

