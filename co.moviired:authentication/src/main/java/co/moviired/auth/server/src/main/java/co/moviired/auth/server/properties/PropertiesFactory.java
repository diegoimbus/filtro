package co.moviired.auth.server.properties;

import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;

@Component
public class PropertiesFactory {
    private final SupportSmsProperties supportSmsProperties;
    private final GlobalProperties globalProperties;
    private final ExtraValidationsProperties extraValidations;

    public PropertiesFactory(@NotNull SupportSmsProperties psupportSmsProperties,
                             @NotNull GlobalProperties pglobalProperties,
                             @NotNull ExtraValidationsProperties pextraValidations) {
        this.supportSmsProperties = psupportSmsProperties;
        this.globalProperties = pglobalProperties;
        this.extraValidations = pextraValidations;
    }

    public SupportSmsProperties getSupportSmsProperties() {
        return supportSmsProperties;
    }

    public GlobalProperties getGlobalProperties() {
        return globalProperties;
    }

    public ExtraValidationsProperties getExtraValidations() {
        return extraValidations;
    }
}

