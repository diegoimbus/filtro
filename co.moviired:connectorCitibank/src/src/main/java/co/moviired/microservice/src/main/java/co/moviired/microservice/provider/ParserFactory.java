package co.moviired.microservice.provider;

import co.moviired.base.domain.exception.ParsingException;
import co.moviired.microservice.domain.enums.OperationType;
import co.moviired.microservice.provider.citibank.DepositParser;
import co.moviired.microservice.provider.citibank.QueryParser;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ParserFactory {

    private final QueryParser queryParser;
    private final DepositParser depositParser;

    public final IParser getParser(OperationType operationType) throws ParsingException {
        IParser parser;
        switch (operationType) {
            case PAYMENT:
                parser = depositParser;
                break;
            case MANUAL:
            case AUTOMATIC:
                parser = queryParser;
                break;
            default:
                throw new ParsingException();
        }
        return parser;
    }

}

