package co.moviired.transpiler.conf;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.Serializable;

@Data
@Configuration
public class GlobalProperties implements Serializable {

    private static final long serialVersionUID = -3395163916611319213L;

    // Versión de la aplicación
    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${spring.application.version}")
    private String applicationVersion;

    // Puertos de comunicación
    @Value("${server.port}")
    private int restPort;

    @Value("${server.port}")
    private int soapPort;

    @Value("${spring.application.services.waitTime}")
    private int serviceWaitTime;

    @Value("${properties.pinAllowedCharacters}")
    private String pinAllowedCharacters;

}

