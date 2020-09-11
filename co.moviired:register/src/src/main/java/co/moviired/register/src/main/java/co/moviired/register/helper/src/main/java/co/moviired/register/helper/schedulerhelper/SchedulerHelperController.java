package co.moviired.register.helper.schedulerhelper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping(SchedulerConstantsHelper.PROJECT_PATH)
public class SchedulerHelperController {

    private final SchedulerHelperProperties schedulerHelperProperties;
    private final Map<String, Long> timesAssignation = new HashMap<>();

    public SchedulerHelperController(@NotNull SchedulerHelperProperties pSchedulerHelperProperties) {
        this.schedulerHelperProperties = pSchedulerHelperProperties;
    }

    @GetMapping(value = SchedulerConstantsHelper.SCHEDULER_HELPER_PATH)
    public final Mono<ResponseEntity<Mono<SchedulerHelperDTO>>> assignTime(@PathVariable String componentName,
                                                                           @PathVariable String process, @PathVariable Long minimumTimeBetweenInstances) {
        String identifier = componentName + SchedulerConstantsHelper.TWO_DOTS + process;
        return Mono.just(new ResponseEntity<>(Mono.just(getNextTime(identifier, minimumTimeBetweenInstances)), HttpStatus.OK));
    }

    private synchronized SchedulerHelperDTO getNextTime(String identifier, Long minimumTimeBetweenInstances) {
        try {
            SchedulerHelperService.assignCorrelative(null, schedulerHelperProperties.getComponentName());
            log.debug(SchedulerConstantsHelper.ASSIGN_NEXT_EXECUTION, identifier, minimumTimeBetweenInstances);
            long newAssignation = new Date().getTime();
            if (timesAssignation.containsKey(identifier)) {
                newAssignation = (Math.max(new Date().getTime(), timesAssignation.get(identifier))) + minimumTimeBetweenInstances;
            }
            timesAssignation.put(identifier, newAssignation);
            SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            log.debug(SchedulerConstantsHelper.SUCCESS_NEXT_EXECUTION, identifier, simpleFormat.format(newAssignation));
            return SchedulerHelperDTO.builder().isSuccess(true).nextTime(newAssignation).build();
        } catch (Exception e) {
            log.error(SchedulerConstantsHelper.ERROR_ASSIGN_TIME, identifier);
            return SchedulerHelperDTO.builder().isSuccess(false).build();
        }
    }
}

