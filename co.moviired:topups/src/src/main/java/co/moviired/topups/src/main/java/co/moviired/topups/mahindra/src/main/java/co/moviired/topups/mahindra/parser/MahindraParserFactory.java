package co.moviired.topups.mahindra.parser;

import co.moviired.topups.exception.ParseException;
import co.moviired.topups.mahindra.parser.impl.RechargeMahindraParser;
import co.moviired.topups.model.enums.OperationType;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author carlossaul.ramirez
 * @version 0.0.1
 */
@Component
public class MahindraParserFactory implements Serializable {

    private static final long serialVersionUID = 7358896064653591179L;

    private final RechargeMahindraParser rechargeMahindra;

    public MahindraParserFactory(@NotNull RechargeMahindraParser rechargeMahindra) {
        super();
        this.rechargeMahindra = rechargeMahindra;
    }

    public final IMahindraParser getParser(OperationType type) throws ParseException {
        if (!OperationType.RTMMREQ.equals(type)) {
            throw new ParseException("Invalid operation");
        }

        return rechargeMahindra;
    }

}

