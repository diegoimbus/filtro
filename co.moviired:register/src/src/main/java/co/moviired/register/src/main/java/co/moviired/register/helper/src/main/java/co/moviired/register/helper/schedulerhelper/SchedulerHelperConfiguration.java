package co.moviired.register.helper.schedulerhelper;

import co.moviired.connector.connector.ReactiveConnector;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.validation.constraints.NotNull;

@Configuration
public class SchedulerHelperConfiguration {

    @Bean(SchedulerConstantsHelper.SCHEDULER_HELPER_API)
    public ReactiveConnector schedulerHelperConnector(@NotNull SchedulerHelperProperties schedulerHelperProperties) {
        return new ReactiveConnector(SchedulerConstantsHelper.SCHEDULER_HELPER_API, schedulerHelperProperties.getUrl(), schedulerHelperProperties.getTimeoutConnect(), schedulerHelperProperties.getTimeoutRead());
    }
}

