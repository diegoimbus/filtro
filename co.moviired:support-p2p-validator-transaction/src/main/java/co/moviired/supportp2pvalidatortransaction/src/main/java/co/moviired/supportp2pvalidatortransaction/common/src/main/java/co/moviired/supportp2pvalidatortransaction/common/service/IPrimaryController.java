package co.moviired.supportp2pvalidatortransaction.common.service;

import co.moviired.supportp2pvalidatortransaction.common.config.GlobalProperties;
import co.moviired.supportp2pvalidatortransaction.common.config.StatusCodeConfig;
import co.moviired.supportp2pvalidatortransaction.common.model.dto.IComponentDTO;
import co.moviired.supportp2pvalidatortransaction.common.provider.schedulersupport.SchedulerSupportConnector;
import co.moviired.supportp2pvalidatortransaction.common.provider.schedulersupport.SchedulerSupportDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;

import static co.moviired.supportp2pvalidatortransaction.common.util.Constants.*;

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
        return assignCorrelative(null).flatMap(request -> startTransaction(PING_SERVICE, request, baseBaseService.ping()));
    }

    @PostMapping(value = SCHEDULER_HELPER_PATH)
    public final Mono<ResponseEntity<Mono<IComponentDTO>>> assignSchedulerTime(@RequestBody SchedulerSupportDTO req) {
        return assignCorrelative(req).flatMap(request -> startTransaction(SCHEDULER_HELPER, request, baseBaseService.getNextTime((SchedulerSupportDTO) request), false, false));
    }
}

