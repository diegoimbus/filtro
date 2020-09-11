package co.moviired.digitalcontent.business.conf;

import co.moviired.digitalcontent.business.domain.dto.ValidatorFactory;
import co.moviired.digitalcontent.business.provider.parser.ClientFactory;
import co.moviired.digitalcontent.business.provider.parser.ParserFactory;
import lombok.Data;
import org.springframework.context.annotation.Configuration;

import javax.validation.constraints.NotNull;

@Data
@Configuration
public class FactoriesConfig {

    private final ClientFactory clientFactory;
    private final ParserFactory parserFactory;
    private final ValidatorFactory validatorFactory;

    public FactoriesConfig(@NotNull ClientFactory pclientFactory,
                           @NotNull ParserFactory pparserFactory,
                           @NotNull ValidatorFactory pvalidatorFactory) {
        super();
        this.clientFactory = pclientFactory;
        this.parserFactory = pparserFactory;
        this.validatorFactory = pvalidatorFactory;
    }
}

