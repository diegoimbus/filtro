package co.moviired.moneytransfer.properties;

import co.moviired.base.domain.config.BaseStatusCodeConfig;
import co.moviired.moneytransfer.helper.ConstanHelper;
import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties(prefix = ConstanHelper.STATUS_CODES_PREFIX)
public class StatusCodeConfig extends BaseStatusCodeConfig {
}

