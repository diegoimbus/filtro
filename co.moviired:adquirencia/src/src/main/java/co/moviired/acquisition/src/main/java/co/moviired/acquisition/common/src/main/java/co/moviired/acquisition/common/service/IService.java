package co.moviired.acquisition.common.service;

import co.moviired.acquisition.common.provider.mahindra.MahindraDTO;
import co.moviired.base.domain.enumeration.ErrorType;
import co.moviired.acquisition.common.config.GlobalProperties;
import co.moviired.acquisition.common.config.StatusCodeConfig;
import co.moviired.acquisition.common.model.dto.IComponentDTO;
import co.moviired.acquisition.common.model.dto.ResponseStatus;
import co.moviired.acquisition.common.model.exceptions.ComponentThrowable;
import co.moviired.acquisition.common.provider.schedulersupport.SchedulerSupportDTO;
import co.moviired.acquisition.common.util.UtilsHelper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static co.moviired.acquisition.common.util.ConstantsHelper.*;
import static co.moviired.acquisition.common.util.StatusCodesHelper.SUCCESS_CODE;

@Slf4j
@Data
public abstract class IService {

    private final GlobalProperties globalProperties;
    private final StatusCodeConfig statusCodeConfig;
    private final SimpleDateFormat simpleDateFormat;

    private Map<String, Long> timesAssignation = new HashMap<>();

    public IService(@NotNull GlobalProperties globalPropertiesI, @NotNull StatusCodeConfig statusCodeConfigI) {

        this.globalProperties = globalPropertiesI;
        this.statusCodeConfig = statusCodeConfigI;
        simpleDateFormat = new SimpleDateFormat(TIME_COMPLETE);
    }

    public final Mono<IComponentDTO> ping() {
        IComponentDTO response = new IComponentDTO();
        try {
            response.setStatus(getSuccessResponse());
        } catch (Exception e) {
            response.setStatus(getErrorResponse());
        }
        return Mono.just(response);
    }

    final synchronized Mono<IComponentDTO> getNextTime(String correlative, SchedulerSupportDTO request) {
        return Mono.just(request)
                .delayElement(Duration.ofMillis(MILLIS_IN_ONE_SECOND))
                .flatMap(req -> {
                    assignCorrelative(correlative);
                    SchedulerSupportDTO response = SchedulerSupportDTO.builder().build();
                    try {
                        log.debug(ASSIGN_NEXT_EXECUTION, request.getProcess(), request.getMinimumTimeBetweenInstances());
                        long newAssignation = new Date().getTime();
                        if (timesAssignation.containsKey(request.getProcess())) {
                            newAssignation = (Math.max(newAssignation, timesAssignation.get(request.getProcess()))) + request.getMinimumTimeBetweenInstances();
                        }
                        timesAssignation.put(request.getProcess(), newAssignation);
                        log.debug(SUCCESS_NEXT_EXECUTION, request.getProcess(), simpleDateFormat.format(newAssignation));
                        response.setNextTime(newAssignation);
                        response.setStatus(getSuccessResponse());
                    } catch (Exception e) {
                        log.error(ERROR_ASSIGN_TIME, e.getMessage());
                        response.setStatus(getErrorResponse());
                    }
                    return Mono.just(response);
                });
    }

    // ON RESPONSES ****************************************************************************************************

    protected final Mono<MahindraDTO> onMahindraResponse(MahindraDTO mahindraResponse, String correlative) {
        assignCorrelative(correlative);
        if (mahindraResponse.getTxnStatus().matches(SUCCESS_CODE)) {
            return Mono.just(mahindraResponse);
        } else {
            return Mono.error(new ComponentThrowable(ErrorType.PROCESSING, mahindraResponse.getMessage(), mahindraResponse.getTxnStatus(), MAHINDRA));
        }
    }

    // MAP METHODS OF UTILS ********************************************************************************************

    protected final String[] getAuthorizationParts(String authorizationHeader) throws ComponentThrowable {
        return UtilsHelper.getAuthorizationParts(globalProperties, statusCodeConfig, authorizationHeader);
    }

    protected final void assignCorrelative(String correlation) {
        UtilsHelper.assignCorrelative(globalProperties, correlation);
    }

    protected final ComponentThrowable getDefaultException() {
        return UtilsHelper.getDefaultException(globalProperties, statusCodeConfig);
    }

    protected final ResponseStatus getSuccessResponse() {
        return UtilsHelper.getSuccessResponse(globalProperties, statusCodeConfig);
    }

    protected final ResponseStatus getErrorResponse() {
        return UtilsHelper.getErrorResponse(globalProperties, statusCodeConfig);
    }

    protected final ResponseStatus getErrorResponseByCode(String code) {
        return UtilsHelper.getErrorResponseByCode(globalProperties, statusCodeConfig, code);
    }

    protected final ComponentThrowable getComponentThrowableProcessing(String statusCode) {
        return new ComponentThrowable(ErrorType.PROCESSING, getStatusCodeConfig().of(statusCode).getMessage(),
                statusCode, getGlobalProperties().getName());
    }

    protected final Mono<AtomicReference<String[]>> validateAndGetAuthorization(String authorizationHeader, AtomicReference<String[]> authorizationParts) {
        try {
            authorizationParts.set(getAuthorizationParts(authorizationHeader));
            return Mono.just(authorizationParts);
        } catch (ComponentThrowable componentThrowable) {
            return Mono.error(componentThrowable);
        }
    }
}
