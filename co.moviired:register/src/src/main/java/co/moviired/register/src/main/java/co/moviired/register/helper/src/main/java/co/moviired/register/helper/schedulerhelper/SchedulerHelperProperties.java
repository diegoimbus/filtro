package co.moviired.register.helper.schedulerhelper;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;

@Data
@ConfigurationProperties(prefix = SchedulerConstantsHelper.SCHEDULER_PROPERTIES_PREFIX)
public final class SchedulerHelperProperties implements Serializable {
    private String url;
    private Integer timeoutConnect;
    private Integer timeoutRead;
    private Boolean isEnable;
    private Integer minimumTimeBetweenInstances;
    private String pathGetNextTime;
    private String componentName;
}
