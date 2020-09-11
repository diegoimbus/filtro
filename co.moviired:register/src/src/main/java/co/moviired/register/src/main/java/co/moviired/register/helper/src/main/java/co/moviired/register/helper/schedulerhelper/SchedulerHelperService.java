package co.moviired.register.helper.schedulerhelper;

import co.moviired.connector.connector.ReactiveConnector;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

@Slf4j
@Service
public final class SchedulerHelperService implements Serializable {

    private static final SecureRandom RANDOM_GENERATOR = new SecureRandom();
    private static final Long NEXT_EXECUTION = 0L;

    private final SchedulerHelperProperties schedulerHelperProperties;
    private final ReactiveConnector reactiveConnector;
    private final ObjectMapper jsonMapper;
    private final int ipAddress;

    public SchedulerHelperService(@Qualifier(SchedulerConstantsHelper.SCHEDULER_HELPER_API) ReactiveConnector pReactiveConnector,
                                  @NotNull SchedulerHelperProperties pSchedulerHelperProperties) throws UnknownHostException {
        this.reactiveConnector = pReactiveConnector;
        this.schedulerHelperProperties = pSchedulerHelperProperties;

        this.jsonMapper = new ObjectMapper();
        this.jsonMapper.configure(SerializationFeature.INDENT_OUTPUT, false);

        // Get HASH of ip address of current host
        this.ipAddress = InetAddress.getLocalHost().hashCode();
    }

    public static void assignCorrelative(String correlation, String componentName) {
        String cId = correlation;

        if (correlation == null || correlation.isEmpty()) {
            cId = SchedulerHelperService.getRandomUUID();
        }

        MDC.putCloseable(SchedulerConstantsHelper.CORRELATION_ID, cId);
        MDC.putCloseable(SchedulerConstantsHelper.COMPONENT, componentName);
    }

    public static String getRandomUUID() {
        return UUID.randomUUID().toString().replace(SchedulerConstantsHelper.STRING_LINE, SchedulerConstantsHelper.EMPTY_STRING);
    }

    public static boolean validateShift(int ipAddress) {
        // IP + RANDOM + TIMESTAMP
        long shift = ipAddress + RANDOM_GENERATOR.nextInt() + new Date().getTime();
        return (shift % 2 != 0);
    }

    public Mono<Boolean> schedulerController(String correlative, String process, boolean allowAloneDefaultControl) {
        String finalProcess = process.toLowerCase().replaceAll(SchedulerConstantsHelper.REGEX_CLEAN_PROCESS, SchedulerConstantsHelper.EMPTY_STRING);

        return getByDefaultControl()
                .flatMap(execute -> {
                    if (!Boolean.TRUE.equals(execute) || !Boolean.TRUE.equals(schedulerHelperProperties.getIsEnable())) {
                        return allowAloneDefaultControl ? Mono.just(execute) : Mono.just(false);
                    }

                    long currentTime = new Date().getTime();
                    if ((NEXT_EXECUTION + schedulerHelperProperties.getMinimumTimeBetweenInstances()) >= currentTime) {
                        return Mono.just(false);
                    }

                    return invokeSchedulerController(correlative, schedulerHelperProperties.getComponentName(), finalProcess)
                            .flatMap(response -> validateResponse(response, finalProcess, allowAloneDefaultControl))
                            .onErrorResume(e -> allowAloneDefaultControl ? getByDefaultControl() : Mono.just(false));
                });
    }

    private Mono<Boolean> validateResponse(SchedulerHelperDTO response, String finalProcess, boolean allowAloneDefaultControl) {
        if (response.isSuccess()) {
            SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String nextTime = simpleFormat.format(new Date(response.getNextTime()));
            long waitTime = response.getNextTime() - new Date().getTime();
            waitTime = waitTime >= 0 ? waitTime : 0;
            log.debug(SchedulerConstantsHelper.LOG_NEXT_EXECUTION, finalProcess, nextTime, waitTime);
            return Mono.just(true).delayElement(Duration.ofMillis(waitTime));
        } else {
            return allowAloneDefaultControl ? getByDefaultControl() : Mono.just(false);
        }
    }

    private Mono<Boolean> getByDefaultControl() {
        return Mono.just(!validateShift(ipAddress));
    }

    private Mono<SchedulerHelperDTO> invokeSchedulerController(String correlative, String componentName, String process) {
        String path = schedulerHelperProperties.getPathGetNextTime().replace(SchedulerConstantsHelper.PROCESS_PLACE_HOLDER, process);
        log.debug(SchedulerConstantsHelper.LOG_THIRD_REQUEST, SchedulerConstantsHelper.SCHEDULER_HELPER_COMPONENT,
                schedulerHelperProperties.getUrl() + path, SchedulerConstantsHelper.EMPTY_STRING);
        return reactiveConnector.exchange(HttpMethod.GET, path, new SchedulerHelperDTO(), String.class, MediaType.APPLICATION_JSON, new HashMap<>())
                .flatMap(response -> {
                    try {
                        SchedulerHelperService.assignCorrelative(correlative, componentName);
                        log.debug(SchedulerConstantsHelper.LBL_RESPONSE, SchedulerConstantsHelper.SCHEDULER_HELPER_COMPONENT.toUpperCase(), response);
                        return Mono.just(this.jsonMapper.readValue((String) response, SchedulerHelperDTO.class));
                    } catch (Exception e) {
                        log.error(SchedulerConstantsHelper.LBL_ERROR, SchedulerConstantsHelper.SCHEDULER_HELPER_COMPONENT.toUpperCase(), e.getMessage());
                        return Mono.error(e);
                    }
                })
                .onErrorResume(error -> {
                    log.error(SchedulerConstantsHelper.LBL_ERROR, SchedulerConstantsHelper.SCHEDULER_HELPER_COMPONENT.toUpperCase(), error.getMessage());
                    return Mono.error(error);
                });
    }
}

