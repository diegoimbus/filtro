package co.moviired.register.providers;


import co.moviired.register.domain.enums.register.OperationType;
import co.moviired.register.exceptions.ParseException;
import co.moviired.register.providers.mahindra.parser.RegistryMahindraParser;
import co.moviired.register.providers.mahindra.parser.UserQueryInfoMahindraParser;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
public class ParserFactory implements Serializable {

    private static final String INVALID_OPERATION = "Operación inválida";

    private final UserQueryInfoMahindraParser userQueryInfoMahindraParser;
    private final RegistryMahindraParser registryMahindraParser;

    public ParserFactory(UserQueryInfoMahindraParser pUserQueryInfoMahindraParser, RegistryMahindraParser pRegistryMahindraParser) {
        super();
        this.userQueryInfoMahindraParser = pUserQueryInfoMahindraParser;
        this.registryMahindraParser = pRegistryMahindraParser;
    }

    public final IParser getParser(OperationType operationType) throws ParseException {
        IParser parser;

        switch (operationType) {

            case USER_QUERY_INFO:
                parser = this.userQueryInfoMahindraParser;
                break;

            case REGISTRY_MERCHANT:
                parser = this.registryMahindraParser;
                break;

            default:
                throw new ParseException(INVALID_OPERATION);
        }
        return parser;
    }
}

