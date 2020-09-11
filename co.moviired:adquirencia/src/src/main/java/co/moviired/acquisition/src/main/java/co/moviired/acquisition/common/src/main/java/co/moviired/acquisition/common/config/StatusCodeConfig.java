package co.moviired.acquisition.common.config;

import co.moviired.base.domain.config.BaseStatusCodeConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;

import static co.moviired.acquisition.common.util.ConstantsHelper.STATUS_CODES_PREFIX;

@ConfigurationProperties(prefix = STATUS_CODES_PREFIX)
public class StatusCodeConfig extends BaseStatusCodeConfig {
}

