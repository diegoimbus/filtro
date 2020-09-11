package co.moviired.transpiler.integration.soap.parser;

import co.moviired.transpiler.exception.ParseException;
import co.moviired.transpiler.integration.IHermesParser;
import co.moviired.transpiler.integration.soap.parser.impl.TopUpParser;
import co.moviired.transpiler.jpa.movii.domain.enums.OperationType;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
public class SoapParserFactory implements Serializable {

    private static final long serialVersionUID = 4411687968907774444L;

    private final TopUpParser topUpParser;

    public SoapParserFactory(TopUpParser ptopUpParser) {
        super();
        this.topUpParser = ptopUpParser;
    }

    public final IHermesParser getParser(OperationType operationType) throws ParseException {
        IHermesParser parser;

        if (operationType.equals(OperationType.TOPUP)) {
            parser = this.topUpParser;
        } else {
            throw new ParseException("Operación inválida");
        }

        return parser;
    }

}

