package co.moviired.mahindrafacade.domain.provider;

import co.moviired.base.domain.exception.ParsingException;
import co.moviired.mahindrafacade.domain.enums.OperationType;
import co.moviired.mahindrafacade.domain.parser.AuthpinreqParser;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
@AllArgsConstructor
public final class ParserFactory implements Serializable {

    private static final long serialVersionUID = -8111145829471817085L;

    private final AuthpinreqParser authpinreqParser;

    public IParser getParser(OperationType operationType) throws ParsingException {
        IParser parser;
        switch (operationType) {
            case AUTHPINRESP:
            case USRQRYINFO:
            case AUTHPINREQ:
                parser = this.authpinreqParser;
                break;

            default:
                throw new ParsingException();
        }

        return parser;
    }
}


