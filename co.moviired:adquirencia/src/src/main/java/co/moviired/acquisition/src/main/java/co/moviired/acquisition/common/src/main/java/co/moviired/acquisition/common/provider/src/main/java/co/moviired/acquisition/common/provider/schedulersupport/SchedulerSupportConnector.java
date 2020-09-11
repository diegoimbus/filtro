package co.moviired.acquisition.common.provider.schedulersupport;

import co.moviired.acquisition.common.config.GlobalProperties;
import co.moviired.acquisition.common.model.method.Scheduler;
import co.moviired.acquisition.common.model.network.HttpRequest;
import co.moviired.acquisition.common.provider.IProviderConnector;
import co.moviired.acquisition.common.util.UtilsHelper;
import com.google.common.collect.ImmutableMap;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;

import static co.moviired.acquisition.common.util.ConstantsHelper.CORRELATIVE_HEADER;
import static co.moviired.acquisition.common.util.ConstantsHelper.FIRST_PART_IDENTIFICATION_ASSIGN_SCHEDULER_TIME;

public class SchedulerSupportConnector extends IProviderConnector<SchedulerSupportProperties, SchedulerSupportDTO, SchedulerSupportFactory> {

    public SchedulerSupportConnector(@NotNull GlobalProperties globalProperties, @NotNull SchedulerSupportProperties properties) {
        super(globalProperties, properties, new SchedulerSupportFactory(properties), UtilsHelper.getJsonMapper(), SchedulerSupportDTO.class);
    }

    public final Mono<SchedulerSupportDTO> invokeSchedulerController(String correlative, Scheduler scheduler, String processIdentification) {
        return invoke(
                HttpRequest.<SchedulerSupportDTO>builder().path(getProperties().getPathGetNextTime())
                        .body(getFactory().getNextTimeRequest(processIdentification.toUpperCase(), scheduler.getMinimumTimeBetweenInstances()))
                        .headers(new ImmutableMap.Builder<String, String>().put(CORRELATIVE_HEADER, correlative).build())
                        .build(),
                correlative, FIRST_PART_IDENTIFICATION_ASSIGN_SCHEDULER_TIME + processIdentification.toUpperCase(), false, false);
    }
}

