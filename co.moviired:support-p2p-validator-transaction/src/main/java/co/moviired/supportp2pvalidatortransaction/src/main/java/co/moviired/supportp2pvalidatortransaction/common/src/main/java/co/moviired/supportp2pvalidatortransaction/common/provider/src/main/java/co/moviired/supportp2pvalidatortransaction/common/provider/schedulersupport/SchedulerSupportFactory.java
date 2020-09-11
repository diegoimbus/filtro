package co.moviired.supportp2pvalidatortransaction.common.provider.schedulersupport;

import co.moviired.supportp2pvalidatortransaction.common.provider.IProviderFactory;

public class SchedulerSupportFactory extends IProviderFactory<SchedulerSupportProperties> {

    public SchedulerSupportFactory(SchedulerSupportProperties schedulerSupportProperties) {
        super(schedulerSupportProperties);
    }

    public SchedulerSupportDTO getNextTimeRequest(String process, Long minimumTimeBetweenInstances, String correlative) {
        SchedulerSupportDTO schedulerSupportDTO = SchedulerSupportDTO.builder()
                .process(process)
                .minimumTimeBetweenInstances(minimumTimeBetweenInstances)
                .build();
        schedulerSupportDTO.setCorrelative(correlative);
        return schedulerSupportDTO;
    }
}

