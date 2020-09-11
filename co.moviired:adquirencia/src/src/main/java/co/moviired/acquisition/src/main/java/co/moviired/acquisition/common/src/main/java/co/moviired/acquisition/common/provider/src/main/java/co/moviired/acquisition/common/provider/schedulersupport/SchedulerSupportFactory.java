package co.moviired.acquisition.common.provider.schedulersupport;

import co.moviired.acquisition.common.provider.IProviderFactory;

public class SchedulerSupportFactory extends IProviderFactory<SchedulerSupportProperties> {

    public SchedulerSupportFactory(SchedulerSupportProperties schedulerSupportProperties) {
        super(schedulerSupportProperties);
    }

    public final SchedulerSupportDTO getNextTimeRequest(String process, Long minimumTimeBetweenInstances) {
        return SchedulerSupportDTO.builder()
                .process(process)
                .minimumTimeBetweenInstances(minimumTimeBetweenInstances)
                .build();
    }
}

