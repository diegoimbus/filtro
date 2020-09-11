package co.movii.auth.server.properties;

import co.moviired.connector.connector.ReactiveConnector;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;

import static co.movii.auth.server.helper.ConstantsHelper.REGISTER_API;

@Component
@Getter
public class PropertiesFactory {
    private final SupportSmsProperties supportSmsProperties;
    private final GlobalProperties globalProperties;
    private final ExtraValidationsProperties extraValidations;
    private final RegisterProperties registerProperties;
    private final ReactiveConnector registerConnector;

    public PropertiesFactory(@NotNull SupportSmsProperties psupportSmsProperties,
                             @NotNull GlobalProperties pglobalProperties,
                             @NotNull ExtraValidationsProperties pextraValidations,
                             @NotNull RegisterProperties registerPropertiesI,
                             @NotNull @Qualifier(REGISTER_API) ReactiveConnector registerConnectorI) {
        this.supportSmsProperties = psupportSmsProperties;
        this.globalProperties = pglobalProperties;
        this.extraValidations = pextraValidations;
        this.registerProperties = registerPropertiesI;
        this.registerConnector = registerConnectorI;
    }

}

