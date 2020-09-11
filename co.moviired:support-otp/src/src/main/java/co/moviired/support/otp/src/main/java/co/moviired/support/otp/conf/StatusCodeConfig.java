package co.moviired.support.otp.conf;

import co.moviired.base.domain.config.BaseStatusCodeConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "status-codes")
public class StatusCodeConfig extends BaseStatusCodeConfig {
}

