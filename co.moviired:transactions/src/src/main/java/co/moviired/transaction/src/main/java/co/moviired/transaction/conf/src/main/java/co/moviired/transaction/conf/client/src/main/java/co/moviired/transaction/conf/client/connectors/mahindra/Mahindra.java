package co.moviired.transaction.conf.client.connectors.mahindra;

import co.moviired.base.domain.enumeration.ErrorType;
import co.moviired.base.domain.exception.ServiceException;
import co.moviired.base.helper.CommandHelper;
import co.moviired.connector.connector.ReactiveConnector;
import co.moviired.transaction.domain.request.Request;
import co.moviired.transaction.domain.response.Response;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.Serializable;

@Slf4j
@Data
@Service
@AllArgsConstructor
public class Mahindra implements IMahindraClient, Serializable {

    private static final long serialVersionUID = 1396082230671426136L;
    private final ReactiveConnector mahindraClient;

    private static Response processResponse(String responseBody) throws IOException {
        return CommandHelper.readXML(responseBody, Response.class);
    }

    @Override
    public Mono<Response> sendMahindraRequest(Request request) throws JsonProcessingException {

        String xml = CommandHelper.writeAsXML(request);
        log.info("Request ws mahindra :{}", CommandHelper.printIgnore(xml, "MPIN"));

        return mahindraClient.post(xml, String.class, MediaType.APPLICATION_XML, null)
                .flatMap(responseMahindra -> {

                    log.info("Response ws mahindra :{}", CommandHelper.printIgnore(responseMahindra.toString(), "MPIN"));

                    try {

                        Response response = processResponse((String) responseMahindra);
                        if (!response.getTxnstatus().equals("200")) {
                            return Mono.error(new ServiceException(ErrorType.PROCESSING, "401", "Acceso Denegado"));
                        }

                        return Mono.just(response);

                    } catch (IOException e) {
                        log.error("Error:{}", e.getMessage());
                        return Mono.error(e);
                    }

                }).onErrorResume(Mono::error);
    }

}

