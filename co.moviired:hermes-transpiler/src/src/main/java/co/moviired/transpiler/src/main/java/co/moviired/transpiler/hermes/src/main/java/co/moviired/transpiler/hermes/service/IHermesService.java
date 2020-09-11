package co.moviired.transpiler.hermes.service;

import co.moviired.transpiler.jpa.movii.domain.dto.hermes.IHermesRequest;
import co.moviired.transpiler.jpa.movii.domain.dto.hermes.IHermesResponse;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

public interface IHermesService extends Serializable {

    Mono<IHermesResponse> service(@NotNull Mono<IHermesRequest> request);

}

