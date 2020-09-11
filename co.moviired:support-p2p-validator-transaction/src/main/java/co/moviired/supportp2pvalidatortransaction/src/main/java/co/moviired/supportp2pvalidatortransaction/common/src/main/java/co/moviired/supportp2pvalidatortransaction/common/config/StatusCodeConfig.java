package co.moviired.supportp2pvalidatortransaction.common.config;

import co.moviired.base.domain.config.BaseStatusCodeConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;

import static co.moviired.supportp2pvalidatortransaction.common.util.Constants.STATUS_CODES_PREFIX;

@ConfigurationProperties(prefix = STATUS_CODES_PREFIX)
public class StatusCodeConfig extends BaseStatusCodeConfig {
}

