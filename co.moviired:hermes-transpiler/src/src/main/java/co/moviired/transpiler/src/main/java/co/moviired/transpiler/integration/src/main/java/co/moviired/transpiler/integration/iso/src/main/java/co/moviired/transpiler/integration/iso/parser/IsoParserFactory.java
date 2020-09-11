package co.moviired.transpiler.integration.iso.parser;

import co.moviired.transpiler.conf.DigitalContentProperties;
import co.moviired.transpiler.exception.ParseException;
import co.moviired.transpiler.helper.ErrorHelper;
import co.moviired.transpiler.integration.IHermesParser;
import co.moviired.transpiler.integration.iso.parser.impl.DigitalContentParser;
import co.moviired.transpiler.integration.iso.parser.impl.EchoParser;
import co.moviired.transpiler.integration.iso.parser.impl.TopUpParser;
import co.moviired.transpiler.jpa.getrax.repository.IProductGetraxRepository;
import co.moviired.transpiler.jpa.movii.domain.enums.OperationType;
import co.moviired.transpiler.jpa.movii.repository.IProductRepository;
import co.moviired.transpiler.jpa.movii.repository.IUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.jpos.iso.ISOException;
import org.springframework.context.ApplicationContext;

import javax.validation.constraints.NotNull;
import java.io.IOException;

@Slf4j
public class IsoParserFactory {

    private final TopUpParser topUpParser;
    private final DigitalContentParser digitalContentParser;
    private final EchoParser echoParser;

    public IsoParserFactory(
            @NotNull ApplicationContext ctx,
            @NotNull String xmlPackager) throws IOException, ISOException {
        super();

        // Cargar los servicios desde el contexto
        IUserRepository piUserRepository = ctx.getBean(IUserRepository.class);
        IProductRepository piProductRepository = ctx.getBean(IProductRepository.class);
        IProductGetraxRepository piProductGetraxRepository = ctx.getBean(IProductGetraxRepository.class);
        DigitalContentProperties digitalContentProperties = ctx.getBean(DigitalContentProperties.class);
        ErrorHelper errorHelper = ctx.getBean(ErrorHelper.class);

        // Instanciar los parsers
        this.topUpParser = new TopUpParser(xmlPackager, piUserRepository, piProductRepository, piProductGetraxRepository, errorHelper);
        this.digitalContentParser = new DigitalContentParser(xmlPackager, piUserRepository, piProductRepository, piProductGetraxRepository, errorHelper, digitalContentProperties);
        this.echoParser = new EchoParser(xmlPackager);
    }

    public final IHermesParser getParser(OperationType operationType) throws ParseException {
        IHermesParser parser;

        switch (operationType) {
            case TOPUP:
                parser = this.topUpParser;
                break;

            case ECHO:
                parser = this.echoParser;
                break;

            case DIGITAL_CONTENT_CARD:
            case DIGITAL_CONTENT_PINES:
                parser = this.digitalContentParser;
                break;

            default:
                throw new ParseException("Operación inválida");
        }

        return parser;
    }

}

