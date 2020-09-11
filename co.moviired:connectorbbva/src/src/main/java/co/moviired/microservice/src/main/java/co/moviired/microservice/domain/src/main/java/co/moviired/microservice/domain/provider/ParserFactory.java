package co.moviired.microservice.domain.provider;

import co.moviired.base.domain.exception.ParsingException;
import co.moviired.microservice.domain.enums.OperationType;
import co.moviired.microservice.domain.provider.bbva.PaySwitchParser;
import co.moviired.microservice.domain.provider.bbva.QuerySwitchParser;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
public class ParserFactory implements Serializable {

    private static final long serialVersionUID = -8111145829471817085L;

    private final PaySwitchParser paySwitchParser;
    private final QuerySwitchParser querySwitchParser;


    public ParserFactory(PaySwitchParser paySwitchParser, QuerySwitchParser pquerySwitchParser) {
        super();
        this.paySwitchParser = paySwitchParser;
        this.querySwitchParser = pquerySwitchParser;
    }

    public final IParser getParser(OperationType operationType) throws ParsingException {
        IParser parser;
        switch (operationType) {
            case AUTOMATIC_QUERY:
            case MANUAL_QUERY:
                parser = this.querySwitchParser;
                break;
            case PAYMENT:
                parser = this.paySwitchParser;
                break;
            default:
                throw new ParsingException();
        }
        return parser;
    }
}


