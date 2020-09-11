package co.moviired.moneytransfer.manager.supportauthentication;

import co.moviired.moneytransfer.client.supportathentication.SupportAuthenticationRequest;
import co.moviired.moneytransfer.client.supportathentication.SupportAuthenticationResponse;
import co.moviired.moneytransfer.domain.model.request.MoneyTransferRequest;
import reactor.core.publisher.Mono;

import java.io.Serializable;

public interface ISupportAuthentication extends Serializable {
    Mono<SupportAuthenticationResponse> userQueryInfo(SupportAuthenticationRequest supportAuthenticationRequest, MoneyTransferRequest moneyTransferRequest);
}

