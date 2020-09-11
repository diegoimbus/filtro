package co.moviired.transaction.conf.client.connectors.mahindra;


import co.moviired.transaction.domain.request.Request;
import co.moviired.transaction.domain.response.Response;
import reactor.core.publisher.Mono;

import java.io.IOException;

public interface IMahindraClient {

    Mono<Response> sendMahindraRequest(Request request) throws IOException;

}

