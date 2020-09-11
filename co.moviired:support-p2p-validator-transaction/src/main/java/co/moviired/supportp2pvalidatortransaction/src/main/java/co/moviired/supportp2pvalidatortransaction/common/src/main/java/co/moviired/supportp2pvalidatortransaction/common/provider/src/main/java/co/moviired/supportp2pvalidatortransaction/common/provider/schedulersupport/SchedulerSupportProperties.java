package co.moviired.supportp2pvalidatortransaction.common.provider.schedulersupport;

import co.moviired.supportp2pvalidatortransaction.common.provider.IProviderProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;

import static co.moviired.supportp2pvalidatortransaction.common.util.Constants.SCHEDULER_PROPERTIES_PREFIX;

@Data
@EqualsAndHashCode(callSuper = true)
@ConfigurationProperties(prefix = SCHEDULER_PROPERTIES_PREFIX)
public class SchedulerSupportProperties extends IProviderProperties {

    private Boolean isEnable;
    private String pathGetNextTime;
}
