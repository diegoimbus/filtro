package co.moviired.auth.server.providers.mahindrafacade.client;

import co.moviired.auth.server.domain.dto.Request;
import reactor.core.publisher.Mono;

import java.io.Serializable;

public interface IMahindraFacadeClient extends Serializable {

    Mono<String> sendValidateUserMHFacade(String command, Request request);

}

