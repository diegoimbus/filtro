package co.movii.auth.server.conf;

import co.moviired.base.domain.config.BaseStatusCodeConfig;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@Data
@EqualsAndHashCode(callSuper = false)
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "status-codes")
public class StatusCodeConfig extends BaseStatusCodeConfig {
}

