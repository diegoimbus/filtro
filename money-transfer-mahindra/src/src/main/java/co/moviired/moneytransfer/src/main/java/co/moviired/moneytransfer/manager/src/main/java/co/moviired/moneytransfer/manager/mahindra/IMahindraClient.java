package co.moviired.moneytransfer.manager.mahindra;

import co.moviired.moneytransfer.client.mahindra.Request;
import co.moviired.moneytransfer.client.mahindra.Response;
import co.moviired.moneytransfer.domain.model.request.MoneyTransferRequest;
import reactor.core.publisher.Mono;

import java.io.IOException;

public interface IMahindraClient {

    Mono<Response> sendMahindraRequest(Request request, MoneyTransferRequest moneyTransferRequest) throws IOException;

}

