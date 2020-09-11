package co.moviired.register.config;

import co.moviired.register.providers.ClientFactory;
import co.moviired.register.providers.ParserFactory;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@AllArgsConstructor
@Component
public class FactoryHandler {

    private final ClientFactory clientFactory;
    private final ParserFactory parserFactory;
}

