package co.moviired.digitalcontent.business.provider.parser;


import co.moviired.base.domain.exception.ParsingException;
import co.moviired.digitalcontent.business.domain.enums.OperationType;
import co.moviired.digitalcontent.business.provider.integrator.parser.InactivateCardServiceIntegratorParser;
import co.moviired.digitalcontent.business.provider.mahindra.parser.ActivateCardServiceMahindraParser;
import co.moviired.digitalcontent.business.provider.mahindra.parser.LoginServiceMahindraParser;
import co.moviired.digitalcontent.business.provider.mahindra.parser.PinesServiceMahindraParser;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Component
public class ParserFactory implements Serializable {

    private static final long serialVersionUID = -8111145829471817085L;

    private final LoginServiceMahindraParser loginServiceMahindraParser;
    private final PinesServiceMahindraParser pinesServiceMahindraParser;
    private final ActivateCardServiceMahindraParser activateCardServiceMahindraParser;
    private final InactivateCardServiceIntegratorParser inactivateCardServiceIntegratorParser;

    public ParserFactory(
            @NotNull LoginServiceMahindraParser ploginServiceMahindraParser,
            @NotNull PinesServiceMahindraParser ppinesServiceMahindraParser,
            @NotNull InactivateCardServiceIntegratorParser pinactivateCardServiceIntegratorParser,
            @NotNull ActivateCardServiceMahindraParser pactivateCardServiceMahindraParser) {
        super();
        this.loginServiceMahindraParser = ploginServiceMahindraParser;
        this.pinesServiceMahindraParser = ppinesServiceMahindraParser;
        this.activateCardServiceMahindraParser = pactivateCardServiceMahindraParser;
        this.inactivateCardServiceIntegratorParser = pinactivateCardServiceIntegratorParser;

    }

    public final IParser getParser(OperationType operationType) throws ParsingException {
        IParser parser;

        switch (operationType) {

            case DIGITAL_CONTENT_CARD_ACTIVATE:
                parser = this.activateCardServiceMahindraParser;
                break;

            case DIGITAL_CONTENT_CARD_INACTIVATE:
                parser = this.inactivateCardServiceIntegratorParser;
                break;

            case DIGITAL_CONTENT_PINES_SALE:
                parser = this.pinesServiceMahindraParser;
                break;

            case LOGIN_USER:
                parser = this.loginServiceMahindraParser;
                break;

            default:
                throw new ParsingException("99", "Operación inválida");
        }

        return parser;
    }
}

