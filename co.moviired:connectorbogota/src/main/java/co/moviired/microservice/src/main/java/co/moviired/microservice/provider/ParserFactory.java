package co.moviired.microservice.provider;

/*
 * Copyright @2019. Movii, SAS. Todos los derechos reservados.
 *
 * @author Cindy Bejarano, Oscar Lopez
 * @since 1.0.0
 */

import co.moviired.base.domain.exception.ParsingException;
import co.moviired.microservice.domain.enums.OperationType;
import co.moviired.microservice.provider.bogota.DepositSwitchParser;
import co.moviired.microservice.provider.bogota.QuerySwitchParser;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
public class ParserFactory implements Serializable {

    private static final long serialVersionUID = -8111145829471817085L;

    private final DepositSwitchParser depositSwitchParser;
    private final QuerySwitchParser querySwitchParser;


    public ParserFactory(DepositSwitchParser pdepositSwitchParser, QuerySwitchParser pquerySwitchParser) {
        super();
        this.depositSwitchParser = pdepositSwitchParser;
        this.querySwitchParser = pquerySwitchParser;
    }

    public final IParser getParser(OperationType operationType) throws ParsingException {
        IParser parser;
        switch (operationType) {
            case PAY_BILL:
                parser = this.depositSwitchParser;
                break;
            case QUERY:
                parser = this.querySwitchParser;
                break;
            default:
                throw new ParsingException();
        }
        return parser;
    }
}


