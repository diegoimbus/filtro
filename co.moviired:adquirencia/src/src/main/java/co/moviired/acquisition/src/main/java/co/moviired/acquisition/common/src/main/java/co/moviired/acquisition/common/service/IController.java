package co.moviired.acquisition.common.service;

import co.moviired.base.domain.StatusCode;
import co.moviired.acquisition.common.config.GlobalProperties;
import co.moviired.acquisition.common.config.StatusCodeConfig;
import co.moviired.acquisition.common.model.dto.IComponentDTO;
import co.moviired.acquisition.common.model.dto.ResponseStatus;
import co.moviired.acquisition.common.model.method.IMethod;
import co.moviired.acquisition.common.model.method.Scheduler;
import co.moviired.acquisition.common.model.method.Service;
import co.moviired.acquisition.common.provider.schedulersupport.SchedulerSupportConnector;
import co.moviired.acquisition.common.util.UtilsHelper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.*;

import static co.moviired.acquisition.common.util.ConstantsHelper.*;
import static co.moviired.acquisition.common.util.StatusCodesHelper.METHOD_IS_NOT_ENABLED_CODE;
import static co.moviired.acquisition.common.util.StatusCodesHelper.SERVER_ERROR_CODE;

@Slf4j
@Data
public abstract class IController<T extends IService> {

    private final T baseBaseService;
    private final GlobalProperties globalProperties;
    private final StatusCodeConfig statusCodeConfig;

    private final SchedulerSupportConnector schedulerSupportConnector;

    private final SimpleDateFormat simpleDateFormat;
    private final Map<String, Long> nextExecutions;

    private final int ipAddress;

    protected IController(@NotNull T baseBaseServiceI, @NotNull GlobalProperties globalPropertiesI, @NotNull StatusCodeConfig statusCodeConfigI,
                          @NotNull @Qualifier(SCHEDULER_HELPER_API) SchedulerSupportConnector schedulerSupportConnectorI) {
        this.baseBaseService = baseBaseServiceI;
        this.globalProperties = globalPropertiesI;
        this.statusCodeConfig = statusCodeConfigI;

        this.schedulerSupportConnector = schedulerSupportConnectorI;
        this.ipAddress = UtilsHelper.getIpAddress();
        this.simpleDateFormat = new SimpleDateFormat(TIME_COMPLETE);
        this.nextExecutions = new HashMap<>();
    }

    protected final Mono<String> assignCorrelative(String correlative) {
        return Mono.just(UtilsHelper.assignCorrelative(globalProperties, correlative));
    }

    protected final Mono<String> assignCorrelative() {
        return Mono.just(UtilsHelper.assignCorrelative(globalProperties, null));
    }

    protected final Mono<ResponseEntity<Mono<IComponentDTO>>> startTransaction(String correlative, Mono<IComponentDTO> methodExecution, boolean... logsControl) {
        return startTransaction(UtilsHelper.getMethodName(), correlative, new IComponentDTO(), methodExecution, logsControl);
    }

    protected final Mono<ResponseEntity<Mono<IComponentDTO>>> startTransaction(String correlative, IComponentDTO requestI, Mono<IComponentDTO> methodExecution, boolean... logsControl) {
        return startTransaction(UtilsHelper.getMethodName(), correlative, requestI, methodExecution, logsControl);
    }

    @SuppressWarnings("SameParameterValue")
    private Mono<ResponseEntity<Mono<IComponentDTO>>> startTransaction(String methodName, String correlative, IComponentDTO requestI, Mono<IComponentDTO> methodExecution, boolean... logsControl) {
        IComponentDTO request = requestI;
        if (request == null) {
            request = new IComponentDTO();
        }
        return Mono.just(new ResponseEntity<>(startTransactionMono(correlative, methodName, request, methodExecution, logsControl), HttpStatus.OK));
    }

    protected final Mono<IComponentDTO> startMonoTransaction(String correlative, IComponentDTO requestI, Mono<IComponentDTO> methodExecution, boolean... logsControl) {
        return startMonoTransaction(UtilsHelper.getMethodName(), correlative, requestI, methodExecution, logsControl);
    }

    @SuppressWarnings("SameParameterValue")
    protected final Mono<IComponentDTO> startMonoTransaction(String correlative, Mono<IComponentDTO> methodExecution, boolean... logsControl) {
        return startMonoTransaction(UtilsHelper.getMethodName(), correlative, new IComponentDTO(), methodExecution, logsControl);
    }

    private Mono<IComponentDTO> startMonoTransaction(String methodName, String correlative, IComponentDTO requestI, Mono<IComponentDTO> methodExecution, boolean... logsControl) {
        IComponentDTO request = requestI;
        if (request == null) {
            request = new IComponentDTO();
        }
        return startTransactionMono(correlative, methodName, request, methodExecution, logsControl);
    }

    private Mono<IComponentDTO> startTransactionMono(String correlative, String methodName, IComponentDTO request, Mono<IComponentDTO> methodExecution, boolean... logsControl) {
        return Mono.just(request)
                .flatMap(req -> {
                    IComponentDTO componentDTO = new IComponentDTO();

                    try {
                        Service service = getService(methodName);

                        StatusCode statusCode = validateComponentMethod(service, methodName, false);
                        if (statusCode != null) {
                            componentDTO.setStatus(ResponseStatus.builder().code(statusCode.getCode()).message(statusCode.getMessage()).component(globalProperties.getName()).build());
                            return Mono.just(componentDTO);
                        }

                        return executeMethod(correlative, getMethodName(service, methodName), request, methodExecution, logsControl);
                    } catch (Exception e) {
                        componentDTO.setStatus(
                                ResponseStatus.builder().code(SERVER_ERROR_CODE).message(e.getMessage()).component(globalProperties.getName()).build());
                        return Mono.just(componentDTO);
                    }
                });
    }

    private String getMethodName(IMethod componentMethod, String methodName) {
        return (componentMethod == null || componentMethod.getName() == null) ? methodName : componentMethod.getName();
    }

    private StatusCode validateComponentMethod(IMethod iComponentMethod, String methodName, boolean isScheduler) {
        StatusCode statusCode = null;
        if (iComponentMethod == null) {
            if (isScheduler) {
                log.error(LOG_SCHEDULER_NOT_FOUND, methodName);
            } else {
                log.error(LOG_SERVICE_METHOD_NOT_FOUND, methodName);
            }
            statusCode = statusCodeConfig.of(SERVER_ERROR_CODE);
        } else if (iComponentMethod.getIsEnable() != null && !iComponentMethod.getIsEnable()) {
            if (isScheduler) {
                log.debug(LOG_SCHEDULER_IS_NOT_ENABLED, methodName);
            } else {
                log.debug(LOG_SERVICE_METHOD_IS_NOT_ENABLED, methodName);
            }
            statusCode = statusCodeConfig.of(METHOD_IS_NOT_ENABLED_CODE);
        }
        return statusCode;
    }

    private Service getService(String methodName) {
        if (globalProperties.getServices() == null || globalProperties.getServices().getRest().isEmpty()) {
            return null;
        }
        return globalProperties.getServices().getRest().get(methodName);
    }

    private Mono<IComponentDTO> executeMethod(String correlative, String methodName, IComponentDTO request, Mono<IComponentDTO> methodExecution, boolean... logsControl) {
        return Mono.just(logsStart(correlative, methodName, request, logsControl))
                .flatMap(c -> methodExecution
                        .onErrorResume(e -> {
                            IComponentDTO componentDTO = new IComponentDTO();
                            UtilsHelper.handleThrowableError(globalProperties, statusCodeConfig, methodName, LOG_ERROR, e, componentDTO);
                            return Mono.just(componentDTO);
                        }))
                .flatMap(response -> logsEnd(methodName, response, logsControl));
    }

    // LOGS ************************************************************************************************************

    protected final String logsStart(String correlative, String requestType, IComponentDTO body, boolean... logsControl) {
        UtilsHelper.assignCorrelative(globalProperties, correlative);
        if (logsControl.length <= 0 || logsControl[0]) {
            log.info(EMPTY_STRING);
            log.info(LBL_START);
            log.info(LBL_REQUEST_TYPE, globalProperties.getName().toUpperCase(), requestType.toUpperCase(), body.protectedToString());
        } else {
            log.debug(EMPTY_STRING);
            log.debug(LBL_START);
            log.debug(LBL_REQUEST_TYPE, globalProperties.getName().toUpperCase(), requestType.toUpperCase(), body.protectedToString());
        }
        return correlative;
    }

    protected final Mono<IComponentDTO> logsEnd(String requestType, IComponentDTO response, boolean... logsControl) {
        String component = globalProperties.getName().toUpperCase();
        if (logsControl.length <= 1 || logsControl[1]) {
            log.info(LBL_RESPONSE, component, requestType.toUpperCase(), response.protectedToString());
            log.info(LBL_END);
            log.info(EMPTY_STRING);
        } else {
            log.debug(LBL_RESPONSE, component, requestType.toUpperCase(), response.protectedToString());
            log.debug(LBL_END);
            log.debug(EMPTY_STRING);
        }
        return Mono.just(response);
    }

    // SCHEDULER *******************************************************************************************************

    @SuppressWarnings("SameParameterValue")
    protected final Mono<IComponentDTO> startScheduler(String correlative, Mono<IComponentDTO> methodExecution, boolean... logsControl) {
        return Mono.just(correlative)
                .flatMap(c -> {
                    String methodName = UtilsHelper.getMethodName();
                    IComponentDTO componentDTO = new IComponentDTO();

                    try {
                        Scheduler scheduler = getScheduler(methodName);

                        StatusCode statusCode = validateComponentMethod(scheduler, methodName, true);
                        if (statusCode != null) {
                            componentDTO.setStatus(ResponseStatus.builder().code(statusCode.getCode()).message(statusCode.getMessage()).component(globalProperties.getName()).build());
                            return Mono.just(componentDTO);
                        }

                        return schedulerController(correlative, getMethodName(scheduler, methodName), scheduler, methodExecution, logsControl);
                    } catch (Exception e) {
                        componentDTO.setStatus(
                                ResponseStatus.builder().code(SERVER_ERROR_CODE).message(e.getMessage()).component(globalProperties.getName()).build());
                        return Mono.just(componentDTO);
                    }
                });
    }

    private Scheduler getScheduler(String methodName) {
        if (globalProperties.getSchedulers() == null || globalProperties.getSchedulers().isEmpty()) {
            return null;
        }
        return globalProperties.getSchedulers().get(methodName);
    }

    private synchronized Mono<IComponentDTO> schedulerController(String correlative, String schedulerName, Scheduler scheduler, Mono<IComponentDTO> methodExecution, boolean... logsControl) {
        String processIdentification = globalProperties.getName() + TWO_DOTS + schedulerName;
        return Mono.just(scheduler)
                .flatMap(s -> {
                    log.debug(START_EXECUTION_SCHEDULER, processIdentification);
                    return Mono.just(s);
                })
                .flatMap(s -> controlExecution(scheduler, correlative, processIdentification))
                .flatMap(execute -> {
                    UtilsHelper.assignCorrelative(globalProperties, correlative);
                    log.debug(DECISION_EXECUTE_SCHEDULER, processIdentification, execute);
                    if (Boolean.TRUE.equals(execute)) {
                        return executeMethod(correlative, schedulerName, new IComponentDTO(), methodExecution, logsControl);
                    } else {
                        return Mono.just(new IComponentDTO());
                    }
                }).doOnTerminate(() -> log.debug(END_EXECUTION_SCHEDULER, processIdentification));
    }

    private Mono<Boolean> controlExecution(Scheduler scheduler, String correlative, String processIdentification) {
        if (Boolean.TRUE.equals(!scheduler.getIsEnable())) {
            return Mono.just(false);
        } else if (scheduler.getAlwaysRun() != null && scheduler.getAlwaysRun()) {
            return Mono.just(true);
        } else {
            return getControlExecution(scheduler, correlative, processIdentification);
        }
    }

    private Mono<Boolean> getControlExecution(Scheduler scheduler, String correlative, String processIdentification) {
        return getByDefaultControl()
                .flatMap(execute -> {
                    if (notCantExecuteSchedulerController(execute, scheduler)) {
                        return Boolean.TRUE.equals(scheduler.getAllowAloneDefaultControl()) ? getByDefaultControl() : Mono.just(false);
                    }

                    long currentTime = new Date().getTime();

                    if (!nextExecutions.containsKey(processIdentification)) {
                        nextExecutions.put(processIdentification, 0L);
                    }

                    if ((nextExecutions.get(processIdentification) + scheduler.getMinimumTimeBetweenInstances()) >= currentTime) {
                        return Mono.just(false);
                    }

                    return getNextTimeAndWait(scheduler, correlative, processIdentification);
                });
    }

    private boolean notCantExecuteSchedulerController(boolean execute, Scheduler scheduler) {
        return !execute || !schedulerSupportConnector.getProperties().getIsEnable() || !scheduler.getSchedulerHelperIsEnable();
    }

    private Mono<Boolean> getNextTimeAndWait(Scheduler scheduler, String correlative, String processIdentification) {
        return schedulerSupportConnector.invokeSchedulerController(correlative, scheduler, processIdentification)
                .flatMap(response -> {
                    if (response.isSuccessResponse()) {
                        String nextTime = simpleDateFormat.format(new Date(response.getNextTime()));
                        long waitTime = response.getNextTime() - new Date().getTime();
                        waitTime = waitTime >= 0 ? waitTime : 0;
                        log.info(LOG_NEXT_EXECUTION, scheduler.getName(), nextTime, waitTime);
                        return Mono.just(true).delayElement(Duration.ofMillis(waitTime));
                    } else {
                        return Boolean.TRUE.equals(scheduler.getAllowAloneDefaultControl()) ? getByDefaultControl() : Mono.just(false);
                    }
                })
                .onErrorResume(e -> Boolean.TRUE.equals(scheduler.getAllowAloneDefaultControl()) ? getByDefaultControl() : Mono.just(false));
    }

    private Mono<Boolean> getByDefaultControl() {
        return Mono.just(!UtilsHelper.validateShift(ipAddress));
    }
}

