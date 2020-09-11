package co.moviired.supportp2pvalidatortransaction.provider.supportsms;

import co.moviired.supportp2pvalidatortransaction.common.provider.IProviderFactory;

public class SupportSMSFactory extends IProviderFactory<SupportSMSProperties> {

    public SupportSMSFactory(SupportSMSProperties providerProperties) {
        super(providerProperties);
    }

    public SupportSMSDTO getRequestSendSMS(String phoneNumber, String message) {
        return SupportSMSDTO.builder()
                .data(SupportSMSDTO.SMSData.builder()
                        .phoneNumber(phoneNumber)
                        .messageContent(message)
                        .build())
                .build();
    }
}

