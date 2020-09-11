package co.moviired.supportp2pvalidatortransaction.common.provider.schedulersupport;

import co.moviired.supportp2pvalidatortransaction.common.config.GlobalProperties;
import co.moviired.supportp2pvalidatortransaction.common.model.method.Scheduler;
import co.moviired.supportp2pvalidatortransaction.common.model.network.HttpRequest;
import co.moviired.supportp2pvalidatortransaction.common.provider.IProviderConnector;
import co.moviired.supportp2pvalidatortransaction.common.util.Utils;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;

import static co.moviired.supportp2pvalidatortransaction.common.util.Constants.FIRST_PART_IDENTIFICATION_ASSIGN_SCHEDULER_TIME;

public class SchedulerSupportConnector extends IProviderConnector<SchedulerSupportProperties, SchedulerSupportDTO, SchedulerSupportFactory> {

    public SchedulerSupportConnector(@NotNull GlobalProperties globalProperties, @NotNull SchedulerSupportProperties properties) {
        super(globalProperties, properties, new SchedulerSupportFactory(properties), Utils.getJsonMapper(), SchedulerSupportDTO.class);
    }

    public Mono<SchedulerSupportDTO> invokeSchedulerController(String correlative, Scheduler scheduler, String processIdentification) {
        return invoke(
                HttpRequest.<SchedulerSupportDTO>builder().path(properties.getPathGetNextTime())
                        .body(factory.getNextTimeRequest(processIdentification.toUpperCase(), scheduler.getMinimumTimeBetweenInstances(), correlative))
                        .build(),
                correlative, FIRST_PART_IDENTIFICATION_ASSIGN_SCHEDULER_TIME + processIdentification.toUpperCase(), false, false);
    }
}

