package co.moviired.register.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "spring.application.services.activation")
public final class ServiceActivationProperties {

    private Boolean uploadSubsidizedDocuments;
    private Boolean changeHashSubsidy;
}

