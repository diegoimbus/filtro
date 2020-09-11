package co.moviired.moneytransfer.manager.registraduria;

import co.moviired.connector.connector.ReactiveConnector;
import co.moviired.moneytransfer.client.registraduria.Data;
import co.moviired.moneytransfer.client.registraduria.RegisterRequest;
import co.moviired.moneytransfer.client.registraduria.RegisterResponse;
import co.moviired.moneytransfer.domain.model.request.MoneyTransferRequest;
import co.moviired.moneytransfer.helper.UtilHelper;
import co.moviired.moneytransfer.properties.RegistraduriaProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;

@Slf4j
@Service
public class Registraduria implements IRegistraduria {

    private final RegistraduriaProperties registraduriaProperties;
    private final ReactiveConnector registraduriaClient;


    public Registraduria(@NotNull RegistraduriaProperties registraduriaProperties,
                         @NotNull ReactiveConnector registraduriaClient) {
        this.registraduriaProperties = registraduriaProperties;
        this.registraduriaClient = registraduriaClient;
    }

    @Override
    public Mono<RegisterResponse> searchRegistraduria(RegisterRequest params, MoneyTransferRequest moneyTransferRequest) {

        Data data = new Data();
        data.setDocumentNumber(params.getDocumentNumber());
        data.setForced(0);
        data.setIdentificationType(params.getIdentificationType());
        log.info("Request ws registraduria :{}", params.toString());

        return registraduriaClient.post(registraduriaProperties.getUrl(), data, RegisterResponse.class, MediaType.APPLICATION_JSON, null)
                .flatMap(responseRegister -> {

                    UtilHelper.assignCorrelative(moneyTransferRequest.getCorrelationId());
                    log.info("Response ws registraduria :{}", responseRegister.toString());

                    return Mono.just((RegisterResponse) responseRegister);

                }).onErrorResume(Mono::error);
    }
}

