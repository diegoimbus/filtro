package co.movii.auth.server.providers.mahindrafacade.client;

import co.movii.auth.server.domain.dto.Request;
import co.movii.auth.server.helper.UtilHelper;
import co.movii.auth.server.properties.MahindraFacadeProperties;
import co.moviired.connector.connector.ReactiveConnector;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;


@Slf4j
@Service
@AllArgsConstructor
public final class MahindraFacadeClient implements IMahindraFacadeClient {

    private static final long serialVersionUID = 2512053335621609262L;

    private final ReactiveConnector mhFacadeClient;
    private final MahindraFacadeProperties mahindraFacadeProperties;


    @Override
    public Mono<String> sendValidateUserMHFacade(String command, Request request) {

        log.info("*********** START PROCESS WS MAHINDRA-FACADE ***********");
        log.info("URL :{}", mahindraFacadeProperties.getUrl());
        log.info("Request WS MahindraFacade :{}", command);

        return mhFacadeClient.post(mahindraFacadeProperties.getUrl(), command, String.class, MediaType.APPLICATION_XML, null)
                .flatMap(response -> {
                    UtilHelper.asignarCorrelativo(request.getCorrelationId());
                    log.info("Response WS MahindraFacade :{}", response.toString());

                    return Mono.just(response.toString());
                }).onErrorResume(Mono::error)
                .doOnTerminate(() -> log.info("*********** END PROCESS WS MAHINDRA-FACADE ***********"));
    }


}

