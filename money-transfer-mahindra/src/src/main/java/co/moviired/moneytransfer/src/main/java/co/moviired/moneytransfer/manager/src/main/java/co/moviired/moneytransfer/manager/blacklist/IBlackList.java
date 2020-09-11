package co.moviired.moneytransfer.manager.blacklist;

import co.moviired.moneytransfer.client.blacklist.BlackListRequest;
import co.moviired.moneytransfer.client.blacklist.BlackListResponse;
import co.moviired.moneytransfer.domain.model.request.MoneyTransferRequest;
import reactor.core.publisher.Mono;

import java.io.Serializable;

public interface IBlackList extends Serializable {

    Mono<BlackListResponse> searchBlackList(BlackListRequest blackListRequest, MoneyTransferRequest moneyTransferRequest);

}

