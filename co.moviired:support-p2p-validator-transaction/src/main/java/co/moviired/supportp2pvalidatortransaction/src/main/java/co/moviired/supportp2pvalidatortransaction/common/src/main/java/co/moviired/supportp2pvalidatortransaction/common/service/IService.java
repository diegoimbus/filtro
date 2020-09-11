package co.moviired.supportp2pvalidatortransaction.common.service;

import co.moviired.base.domain.enumeration.ErrorType;
import co.moviired.supportp2pvalidatortransaction.common.config.GlobalProperties;
import co.moviired.supportp2pvalidatortransaction.common.config.StatusCodeConfig;
import co.moviired.supportp2pvalidatortransaction.common.model.dto.IComponentDTO;
import co.moviired.supportp2pvalidatortransaction.common.model.dto.ResponseStatus;
import co.moviired.supportp2pvalidatortransaction.common.model.exceptions.ComponentThrowable;
import co.moviired.supportp2pvalidatortransaction.common.provider.mahindra.MahindraDTO;
import co.moviired.supportp2pvalidatortransaction.common.provider.schedulersupport.SchedulerSupportDTO;
import co.moviired.supportp2pvalidatortransaction.common.util.Utils;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;

import static co.moviired.supportp2pvalidatortransaction.common.util.Constants.*;
import static co.moviired.supportp2pvalidatortransaction.common.util.StatusCodes.SUCCESS_CODE;

@Slf4j
public abstract class IService {

    protected final GlobalProperties globalProperties;
    protected final StatusCodeConfig statusCodeConfig;
    private final SimpleDateFormat simpleDateFormat;

    private HashMap<String, Long> timesAssignation = new HashMap<>();

    public IService(@NotNull GlobalProperties globalProperties, @NotNull StatusCodeConfig statusCodeConfig) {

        this.globalProperties = globalProperties;
        this.statusCodeConfig = statusCodeConfig;
        simpleDateFormat = new SimpleDateFormat(TIME_COMPLETE);
    }

    public Mono<IComponentDTO> ping() {
        IComponentDTO response = new IComponentDTO();
        try {
            response.setStatus(getSuccessResponse());
        } catch (Exception e) {
            response.setStatus(getErrorResponse());
        }

        return Mono.just(response);
    }

    synchronized Mono<IComponentDTO> getNextTime(SchedulerSupportDTO request) {
        return Mono.just(request)
                .delayElement(Duration.ofMillis(1000))
                .flatMap(req -> {
                    assignCorrelative(req.getCorrelative());
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

    protected Mono<MahindraDTO> onMahindraResponse(MahindraDTO mahindraResponse, String correlative) {
        assignCorrelative(correlative);
        if (mahindraResponse.getTxnStatus().matches(SUCCESS_CODE)) {
            return Mono.just(mahindraResponse);
        } else
            return Mono.error(new ComponentThrowable(ErrorType.PROCESSING, mahindraResponse.getMessage(), mahindraResponse.getTxnStatus(), MAHINDRA));
    }

    // MAP METHODS OF UTILS ********************************************************************************************

    protected String[] getAuthorizationParts(String authorizationHeader) throws ComponentThrowable {
        return Utils.getAuthorizationParts(globalProperties, statusCodeConfig, authorizationHeader);
    }

    protected String assignCorrelative(String correlation) {
        return Utils.assignCorrelative(globalProperties, correlation);
    }

    protected ComponentThrowable getDefaultException() {
        return Utils.getDefaultException(globalProperties, statusCodeConfig);
    }

    protected ResponseStatus getSuccessResponse() {
        return Utils.getSuccessResponse(globalProperties, statusCodeConfig);
    }

    protected ResponseStatus getErrorResponse() {
        return Utils.getErrorResponse(globalProperties, statusCodeConfig);
    }

    protected ResponseStatus getErrorResponseByCode(String code) {
        return Utils.getErrorResponseByCode(globalProperties, statusCodeConfig, code);
    }

    protected Mono<AtomicReference<String[]>> validateAndGetAuthorization(String authorizationHeader, AtomicReference<String[]> authorizationParts) {
        try {
            authorizationParts.set(getAuthorizationParts(authorizationHeader));
            return Mono.just(authorizationParts);
        } catch (ComponentThrowable componentThrowable) {
            return Mono.error(componentThrowable);
        }
    }
}
