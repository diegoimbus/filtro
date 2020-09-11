package co.moviired.register.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;

import static co.moviired.register.helper.ConstantsHelper.SPRING_CONFIG_PREFIX;

@Data
@ConfigurationProperties(prefix = SPRING_CONFIG_PREFIX)
public final class GlobalProperties implements Serializable {

    private String name;
    private String version;
    private Integer restPort;
    private String secret;
}

