package co.moviired.support.conf;
/*
 * Copyright @2019. Movii, SAS. Todos los derechos reservados.
 *
 * @author Bejarano, Cindy
 * @version 1, 2019
 * @since 1.0
 */

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Data
@Component
public class GlobalProperties implements Serializable {

    // Versión de la aplicación
    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${spring.application.secret}")
    private String secret;

    @Value("${spring.application.version}")
    private String applicationVersion;

    // Puertos de comunicación
    @Value("${server.port}")
    private int restPort;

    @Value("${spring.application.baseUrl}")
    private String baseUrl;

    // EMAIL
    @Value("${properties.email.url}")
    private String urlServiceSendEmail;

    @Value("${properties.email.pathConsignmentProcess}")
    private String pathConsignmentProcess;

    @Value("${ws-consignments.bogota.urlWsIntCoreIntWs}")
    private String urlBogotaWs;

    @Value("${ws-consignments.bancolombia.urlWsIntCoreIntWs}")
    private String urlBancolombiaWs;

}

