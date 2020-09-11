package co.moviired.acquisition.common.service;

import co.moviired.acquisition.common.config.GlobalProperties;
import co.moviired.acquisition.common.config.StatusCodeConfig;
import co.moviired.acquisition.common.model.dto.IComponentDTO;
import co.moviired.acquisition.common.provider.schedulersupport.SchedulerSupportConnector;
import co.moviired.acquisition.common.provider.schedulersupport.SchedulerSupportDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;

import static co.moviired.acquisition.common.util.ConstantsHelper.*;

@Slf4j
public abstract class IPrimaryController<T extends IService> extends IController<T> {

    public IPrimaryController(@NotNull T baseBaseService,
                              @NotNull GlobalProperties globalProperties,
                              @NotNull StatusCodeConfig statusCodeConfig,
                              @NotNull @Qualifier(SCHEDULER_HELPER_API) SchedulerSupportConnector schedulerSupportConnector) {
        super(baseBaseService, globalProperties, statusCodeConfig, schedulerSupportConnector);
    }

    @GetMapping(value = PING_YML_ROUTE)
    public final Mono<ResponseEntity<Mono<IComponentDTO>>> ping() {
        return assignCorrelative().flatMap(correlationId -> startTransaction(correlationId, getBaseBaseService().ping()));
    }

    @PostMapping(value = SCHEDULER_HELPER_PATH)
    public final Mono<ResponseEntity<Mono<IComponentDTO>>> schedulerHelper(@RequestHeader(value = CORRELATIVE_HEADER, required = false) String correlative,
                                                                           @RequestBody SchedulerSupportDTO request) {
        return assignCorrelative(correlative).flatMap(correlationId -> startTransaction(correlationId, request, getBaseBaseService().getNextTime(correlationId, request), false, false));
    }
}

