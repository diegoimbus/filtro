package co.moviired.microservice.conf;

/*
 * Copyright @2019. Movii, SAS. Todos los derechos reservados.
 *
 * @author Cindy Bejarano, Oscar Lopez
 * @since 1.0.0
 */

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@Component
public class GlobalProperties {

    // Versión de la aplicación
    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${spring.application.version}")
    private String applicationVersion;

    @Value("${server.port}")
    private int restPort;

}

