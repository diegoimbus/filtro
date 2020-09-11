package co.moviired.mahindrafacade.domain.provider;

import co.moviired.mahindrafacade.client.mahindra.Request;
import co.moviired.mahindrafacade.domain.enums.OperationType;
import com.fasterxml.jackson.core.JsonProcessingException;
import reactor.core.publisher.Mono;

import java.io.Serializable;

public interface IProccesor extends Serializable {

    Mono<String> proccess(Request rqCommand, OperationType responseOperation, String correlationId) throws JsonProcessingException;

}

