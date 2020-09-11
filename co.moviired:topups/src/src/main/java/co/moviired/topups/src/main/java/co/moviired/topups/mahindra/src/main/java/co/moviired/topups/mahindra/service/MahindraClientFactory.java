package co.moviired.topups.mahindra.service;

import co.moviired.connector.connector.ReactiveConnector;
import co.moviired.topups.exception.ParseException;
import co.moviired.topups.model.enums.OperationType;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author carlossaul.ramirez
 * @version 0.0.1
 */
@Component
public class MahindraClientFactory implements Serializable {

    private static final long serialVersionUID = -8111145829471817085L;

    private final ReactiveConnector mhTransactionalClient;

    public MahindraClientFactory(@NotNull ReactiveConnector mhTransactionalClient) {
        super();
        this.mhTransactionalClient = mhTransactionalClient;
    }

    public final ReactiveConnector getClient(OperationType operationType) throws ParseException {
        if (!operationType.equals(OperationType.RTMMREQ)) {
            throw new ParseException("Invalid operation");
        }

        return this.mhTransactionalClient;
    }

}

