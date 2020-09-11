package co.moviired.cardManager.conf;

import co.moviired.base.domain.config.BaseStatusCodeConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "status-codes")
public class StatusCodeConfig extends BaseStatusCodeConfig {
}

