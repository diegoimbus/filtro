package co.movii.auth.server.providers.mahindrafacade.client;

import co.movii.auth.server.domain.dto.Request;
import reactor.core.publisher.Mono;

import java.io.Serializable;

public interface IMahindraFacadeClient extends Serializable {

    Mono<String> sendValidateUserMHFacade(String command, Request request);

}

