package co.moviired.microservice.conf;

/*
 * Copyright @2020. Movii, SAS. Todos los derechos reservados.
 *
 * @author Cindy Bejarano, Oscar Lopez
 * @since 1.1.1
 */

import co.moviired.base.domain.config.BaseStatusCodeConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "status-codes")
public class StatusCodeConfig extends BaseStatusCodeConfig {

}

