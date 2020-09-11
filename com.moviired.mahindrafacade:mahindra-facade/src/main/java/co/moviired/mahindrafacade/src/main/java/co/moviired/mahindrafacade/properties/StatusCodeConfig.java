package co.moviired.mahindrafacade.properties;

import co.moviired.base.domain.config.BaseStatusCodeConfig;
import co.moviired.mahindrafacade.helper.ConstantsHelper;
import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties(prefix = ConstantsHelper.STATUS_CODES_PREFIX)
public class StatusCodeConfig extends BaseStatusCodeConfig {
}

