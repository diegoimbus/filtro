package co.moviired.supportp2pvalidatortransaction.common.provider.mahindra;

import co.moviired.supportp2pvalidatortransaction.common.config.GlobalProperties;
import co.moviired.supportp2pvalidatortransaction.common.model.network.HttpRequest;
import co.moviired.supportp2pvalidatortransaction.common.provider.IProviderConnector;
import co.moviired.supportp2pvalidatortransaction.common.util.Utils;
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;

public class MahindraConnector extends IProviderConnector<MahindraProperties, MahindraDTO, MahindraFactory> {

    public MahindraConnector(@NotNull GlobalProperties globalProperties, @NotNull MahindraProperties properties) {
        super(globalProperties, properties, new MahindraFactory(properties), Utils.getXmlMapper(), MahindraDTO.class);
    }

    public Mono<MahindraDTO> invoke(MahindraDTO request, String correlative, String identification) {
        return invoke(HttpRequest.<MahindraDTO>builder()
                .mediaType(MediaType.APPLICATION_XML)
                .body(request)
                .build(), correlative, identification);
    }
}

