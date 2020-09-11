package co.moviired.moneytransfer.manager.supportauthentication;

import co.moviired.connector.connector.ReactiveConnector;
import co.moviired.moneytransfer.client.supportathentication.SupportAuthenticationRequest;
import co.moviired.moneytransfer.client.supportathentication.SupportAuthenticationResponse;
import co.moviired.moneytransfer.domain.model.request.MoneyTransferRequest;
import co.moviired.moneytransfer.helper.UtilHelper;
import co.moviired.moneytransfer.properties.SupportAuthenticationProperties;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@AllArgsConstructor
public class SupportAuthentication implements ISupportAuthentication {

    private final ReactiveConnector supportAuthenticationClient;
    private final SupportAuthenticationProperties supportAuthenticationProperties;

    @Override
    public Mono<SupportAuthenticationResponse> userQueryInfo(SupportAuthenticationRequest supportAuthenticationRequest, MoneyTransferRequest moneyTransferRequest) {

        String url = supportAuthenticationProperties.getPathQueryUserInfo(supportAuthenticationRequest.getUserLogin(), supportAuthenticationRequest.getUserType());
        log.info("Request ws supportAuthentication :{}", url);

        Map<String, String> header = new HashMap<>();
        header.put("correlationId", supportAuthenticationRequest.getCorrelationId());

        return supportAuthenticationClient.get(url, SupportAuthenticationResponse.class, MediaType.APPLICATION_JSON, header)
                .flatMap(response -> {

                    UtilHelper.assignCorrelative(moneyTransferRequest.getCorrelationId());
                    log.info("Response ws supportAuthentication :{}", response);

                    return Mono.just((SupportAuthenticationResponse) response);

                }).onErrorResume(Mono::error);

    }
}

