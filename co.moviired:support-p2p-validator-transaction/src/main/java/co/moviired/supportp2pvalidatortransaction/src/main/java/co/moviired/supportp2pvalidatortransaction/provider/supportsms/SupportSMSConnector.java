package co.moviired.supportp2pvalidatortransaction.provider.supportsms;

import co.moviired.supportp2pvalidatortransaction.common.config.GlobalProperties;
import co.moviired.supportp2pvalidatortransaction.common.model.network.HttpRequest;
import co.moviired.supportp2pvalidatortransaction.common.provider.IProviderConnector;
import co.moviired.supportp2pvalidatortransaction.common.util.Utils;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;

public class SupportSMSConnector extends IProviderConnector<SupportSMSProperties, SupportSMSDTO, SupportSMSFactory> {

    public SupportSMSConnector(@NotNull GlobalProperties globalProperties, @NotNull SupportSMSProperties properties) {
        super(globalProperties, properties, new SupportSMSFactory(properties), Utils.getJsonMapper(), SupportSMSDTO.class);
    }

    public Mono<SupportSMSDTO> invoke(String phoneNumber, String message, String correlative, String identification) {
        return invoke(HttpRequest.<SupportSMSDTO>builder()
                .body(factory.getRequestSendSMS(phoneNumber, message))
                .build(), correlative, identification);
    }
}

