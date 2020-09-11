package co.moviired.register.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;

@Data
@ConfigurationProperties(prefix = "spring.application.schedulers")
public final class SchedulersConfigurationProperties implements Serializable {
    private Boolean updateInfoPersonSubsidizedEnabled;
    private Integer updateInfoPersonMaxRecordsSimultaneous;
    private Long updateInfoPersonTakenTimeOut;
    private Boolean validateStatusAdoEnabled;
}

