package co.moviired.digitalcontent.business.provider.parser;

import co.moviired.base.domain.exception.ParsingException;
import co.moviired.connector.connector.ReactiveConnector;
import co.moviired.digitalcontent.business.domain.enums.OperationType;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Component
public class ClientFactory implements Serializable {

    private static final long serialVersionUID = -8111145829471817085L;

    private final ReactiveConnector mhClient;
    private final ReactiveConnector integradorClient;
    private final ReactiveConnector mailClient;

    public ClientFactory(@NotNull ReactiveConnector mhTransactionalClient,
                         @NotNull ReactiveConnector integratorClient,
                         @NotNull ReactiveConnector emailClient) {
        super();
        this.mhClient = mhTransactionalClient;
        this.integradorClient = integratorClient;
        this.mailClient = emailClient;
    }

    public final ReactiveConnector getClient(OperationType operationType) throws ParsingException {
        ReactiveConnector client;

        switch (operationType) {
            case DIGITAL_CONTENT_CARD_ACTIVATE:
            case DIGITAL_CONTENT_PINES_SALE:
            case LOGIN_USER:
                client = this.mhClient;
                break;
            case DIGITAL_CONTENT_CARD_INACTIVATE:
                client = this.integradorClient;
                break;
            case EMAIL:
                client = this.mailClient;
                break;
            default:
                throw new ParsingException("99", "Operación inválida");
        }

        return client;
    }
}

