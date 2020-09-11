package co.moviired.moneytransfer.manager.notifier;

import co.moviired.moneytransfer.domain.model.dto.PersonDTO;
import co.moviired.moneytransfer.domain.model.request.MoneyTransferRequest;

import java.io.Serializable;

public interface INotifier extends Serializable {

    void notify(MoneyTransferRequest moneyTransferRequest, PersonDTO personDTO);

}

