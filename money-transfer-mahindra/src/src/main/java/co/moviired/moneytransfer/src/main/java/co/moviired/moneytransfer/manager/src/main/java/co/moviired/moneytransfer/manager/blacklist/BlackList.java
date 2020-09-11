package co.moviired.moneytransfer.manager.blacklist;

import co.moviired.connector.connector.ReactiveConnector;
import co.moviired.moneytransfer.client.blacklist.BlackListRequest;
import co.moviired.moneytransfer.client.blacklist.BlackListResponse;
import co.moviired.moneytransfer.domain.model.request.MoneyTransferRequest;
import co.moviired.moneytransfer.helper.UtilHelper;
import co.moviired.moneytransfer.properties.BlackListProperties;
import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.encoder.org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;

@Slf4j
@Service
public class BlackList implements IBlackList {

    private final ReactiveConnector blackListCLient;
    private final BlackListProperties blackListProperties;

    public BlackList(@NotNull @Qualifier("blackListClient") ReactiveConnector blackListCLient,
                     @NotNull BlackListProperties blackListProperties) {
        this.blackListCLient = blackListCLient;
        this.blackListProperties = blackListProperties;
    }

    @Override
    public Mono<BlackListResponse> searchBlackList(BlackListRequest blackListRequest, MoneyTransferRequest moneyTransferRequest) {

        blackListRequest.setUserName(StringUtils.stripAccents(blackListRequest.getUserName()));
        log.info("Request ws BlackList :{}", blackListRequest.toString());

        return blackListCLient.post(blackListProperties.getUrl(), blackListRequest, BlackListResponse.class, MediaType.APPLICATION_JSON, null)
                .flatMap(response -> {

                    UtilHelper.assignCorrelative(moneyTransferRequest.getCorrelationId());
                    log.info("Response ws BlackList :{}", response.toString());

                    return Mono.just((BlackListResponse) response);

                }).onErrorResume(Mono::error);
    }
}

