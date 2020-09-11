package co.moviired.acquisition.common.model.method;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Scheduler extends IMethod {

    private String cron;
    private Long rate;
    private Boolean alwaysRun;
    private Boolean schedulerHelperIsEnable;
    private Long minimumTimeBetweenInstances;
    private Boolean allowAloneDefaultControl;
}

