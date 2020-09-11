package co.moviired.digitalcontent.business.provider;

import co.moviired.base.domain.exception.ParsingException;
import co.moviired.connector.connector.ReactiveConnector;
import co.moviired.digitalcontent.business.domain.enums.OperationType;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Component
public class ClientFactory implements Serializable {

    private static final long serialVersionUID = -8111145829471817085L;

    private final ReactiveConnector mhTransactionalClient;
    private final ReactiveConnector integratorClient;
    private final ReactiveConnector emailClient;


    public ClientFactory(@NotNull ReactiveConnector mhTransactionalClient,
                         @NotNull ReactiveConnector integratorClient,
                         @NotNull ReactiveConnector emailClient) {
        super();
        this.mhTransactionalClient = mhTransactionalClient;
        this.integratorClient = integratorClient;
        this.emailClient = emailClient;
    }

    public final ReactiveConnector getClient(OperationType operationType) throws ParsingException {
        ReactiveConnector client;

        switch (operationType) {
            case DIGITAL_CONTENT_CARD_ACTIVATE:
            case DIGITAL_CONTENT_PINES_SALE:
            case LOGIN_USER:
                client = this.mhTransactionalClient;
                break;
            case DIGITAL_CONTENT_CARD_INACTIVATE:
                client = this.integratorClient;
                break;
            case EMAIL:
                client = this.emailClient;
                break;
            default:
                throw new ParsingException("99", "Operación inválida");
        }

        return client;
    }
}

