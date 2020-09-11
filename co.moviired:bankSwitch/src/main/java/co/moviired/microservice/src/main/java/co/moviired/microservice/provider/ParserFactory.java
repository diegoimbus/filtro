package co.moviired.microservice.provider;
/*
 * Copyright @2019. Movii, SAS. Todos los derechos reservados.
 *
 * @author Bejarano, Cindy
 * @version 1, 2019
 * @since 1.0
 */

import co.moviired.microservice.domain.enums.OperationType;
import co.moviired.microservice.exception.ParseException;
import co.moviired.microservice.provider.switchprovider.CashOutSwitchParser;
import co.moviired.microservice.provider.switchprovider.DepositSwitchParser;
import co.moviired.microservice.provider.switchprovider.QuerySwitchParser;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
public class ParserFactory implements Serializable {

    private static final long serialVersionUID = -8111145829471817085L;

    private final DepositSwitchParser depositSwitchParser;
    private final CashOutSwitchParser cashOutswitchParser;
    private final QuerySwitchParser querySwitchParser;


    public ParserFactory(DepositSwitchParser pdepositSwitchParser, CashOutSwitchParser pcashOutswitchParser, QuerySwitchParser pquerySwitchParser) {
        super();
        this.depositSwitchParser = pdepositSwitchParser;
        this.cashOutswitchParser = pcashOutswitchParser;
        this.querySwitchParser = pquerySwitchParser;
    }

    public final IParser getParser(OperationType operationType) throws ParseException {
        IParser parser;

        switch (operationType) {

            case CASH_IN:
                parser = this.depositSwitchParser;
                break;

            case CASH_OUT:
                parser = this.cashOutswitchParser;
                break;

            case QUERY:
                parser = this.querySwitchParser;
                break;

            default:
                throw new ParseException("Operación inválida");
        }

        return parser;
    }
}

