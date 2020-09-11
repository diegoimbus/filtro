package co.moviired.acquisition.common.provider.mahindra;

import co.moviired.acquisition.common.config.GlobalProperties;
import co.moviired.acquisition.common.model.network.HttpRequest;
import co.moviired.acquisition.common.util.UtilsHelper;
import co.moviired.acquisition.common.provider.IProviderConnector;
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;

import static co.moviired.acquisition.util.ConstantsHelper.MAHINDRA_LOGIN_PROCESS;

public class MahindraConnector extends IProviderConnector<MahindraProperties, MahindraDTO, MahindraFactory> {

    public MahindraConnector(@NotNull GlobalProperties globalProperties, @NotNull MahindraProperties properties) {
        super(globalProperties, properties, new MahindraFactory(properties), UtilsHelper.getXmlMapper(), MahindraDTO.class);
    }

    public final Mono<MahindraDTO> invokeLogin(String correlative, String phoneNumber, String password) {
        return invoke(
                HttpRequest.<MahindraDTO>builder()
                        .mediaType(MediaType.APPLICATION_XML)
                        .body(getFactory().getLoginRequest(phoneNumber, password))
                        .build(),
                correlative, MAHINDRA_LOGIN_PROCESS);
    }
}

