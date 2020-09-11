package co.moviired.moneytransfer.manager.registraduria;

import co.moviired.moneytransfer.client.registraduria.RegisterRequest;
import co.moviired.moneytransfer.client.registraduria.RegisterResponse;
import co.moviired.moneytransfer.domain.model.request.MoneyTransferRequest;
import reactor.core.publisher.Mono;

import java.io.Serializable;


public interface IRegistraduria extends Serializable {

    Mono<RegisterResponse> searchRegistraduria(RegisterRequest paramas, MoneyTransferRequest moneyTransferRequest);

}

