package co.moviired.digitalcontent.incomm.properties;

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

    // Incomm
    @Value("${client.host}")
    private String incommHost;

    @Value("${client.port}")
    private Integer incommPort;

    @Value("${client.key}")
    private String incommKey;

    @Value("${client.debug}")
    private Boolean incommDebug;

    @Value("${client.reconnectDelay}")
    private Integer incommReconnect;

    @Value("${client.timeout.read}")
    private Integer timeOutRead;

    // REVERSO

    @Value("${client.reverse.excecute}")
    private boolean reverse;

    @Value("${client.reverse.retries}")
    private Integer retries;

    @Value("${client.reverse.delay}")
    private Integer delay;

    @Value("${properties.digitalContent.pinAllowedCharacters}")
    private String pinAllowedCharacters;
}

