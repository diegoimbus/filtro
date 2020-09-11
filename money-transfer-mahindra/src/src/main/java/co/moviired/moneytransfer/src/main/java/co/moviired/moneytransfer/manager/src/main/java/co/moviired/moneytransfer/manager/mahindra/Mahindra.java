package co.moviired.moneytransfer.manager.mahindra;

import co.moviired.base.helper.CommandHelper;
import co.moviired.connector.connector.ReactiveConnector;
import co.moviired.moneytransfer.client.mahindra.Request;
import co.moviired.moneytransfer.client.mahindra.Response;
import co.moviired.moneytransfer.domain.model.request.MoneyTransferRequest;
import co.moviired.moneytransfer.helper.ConstanHelper;
import co.moviired.moneytransfer.helper.UtilHelper;
import co.moviired.moneytransfer.properties.StatusCodeConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.IOException;

@Slf4j
@Data
@Service
@AllArgsConstructor
public class Mahindra implements IMahindraClient {

    private static final String LBLREQUEST = "Request enviado a Mahindra: ";
    private static final String LBLRESPONSE = "Respuesta de Mahindra: ";

    private final StatusCodeConfig statusCodeConfig;
    private final ReactiveConnector mahindraClient;

    private static Response processResponse(String responseBody) throws IOException {
        return CommandHelper.readXML(responseBody, Response.class);
    }

    @Override
    public Mono<Response> sendMahindraRequest(Request request, MoneyTransferRequest moneyTransferRequest) throws JsonProcessingException {

        String xml = CommandHelper.writeAsXML(request);
        log.info("Request ws mahindra :{}", CommandHelper.printIgnore(xml, ConstanHelper.PASSCODE, ConstanHelper.PIN, ConstanHelper.MPIN));

        return Mono.just(request).flatMap(response -> this.mahindraClient.post(xml, String.class, MediaType.APPLICATION_XML, null))
                .flatMap(responseMahindra -> {

                    UtilHelper.assignCorrelative(moneyTransferRequest.getCorrelationId());
                    log.info("Response ws mahindra :{}", responseMahindra.toString());
                    try {

                        Response response = processResponse((String) responseMahindra);
                        return Mono.just(response);

                    } catch (IOException e) {
                        log.error("Error:{}", e.getMessage());
                        return Mono.error(e);
                    }

                }).onErrorResume(Mono::error);
    }

}

