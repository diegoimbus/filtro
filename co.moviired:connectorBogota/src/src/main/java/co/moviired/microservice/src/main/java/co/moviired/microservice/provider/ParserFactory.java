package co.moviired.microservice.provider;

import co.moviired.base.domain.exception.ParsingException;
import co.moviired.microservice.domain.enums.OperationType;
import co.moviired.microservice.provider.bogota.DepositParser;
import co.moviired.microservice.provider.bogota.QueryParser;
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
            case PAY_BILL:
                parser = this.depositParser;
                break;
            case QUERY:
                parser = this.queryParser;
                break;
            default:
                throw new ParsingException();
        }
        return parser;
    }

}

