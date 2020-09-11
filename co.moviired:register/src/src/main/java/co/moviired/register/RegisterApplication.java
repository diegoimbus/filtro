package co.moviired.register;

import co.moviired.register.config.StatusCodeConfig;
import co.moviired.register.helper.schedulerhelper.SchedulerHelperProperties;
import co.moviired.register.properties.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.validation.constraints.NotNull;

import static co.moviired.register.helper.ConstantsHelper.*;

@Slf4j
@EnableScheduling
@EnableJpaRepositories
@SpringBootApplication
@EnableConfigurationProperties(value = {
        GlobalProperties.class,
        StatusCodeConfig.class,
        AdoProperties.class,
        ClevertapProperties.class,
        OtpProperties.class,
        SmsProperties.class,
        MahindraProperties.class,
        BlackListProperties.class,
        RegistraduriaProperties.class,
        SupportAuthProperties.class,
        TermsAndConditionsProperties.class,
        CleanAddressProperties.class,
        CmlProperties.class,
        SchedulersConfigurationProperties.class,
        ServiceActivationProperties.class,
        SchedulerHelperProperties.class,
        SubsidyProperties.class
})
public class RegisterApplication implements ApplicationListener<ContextRefreshedEvent> {

    private final GlobalProperties config;

    public RegisterApplication(@NotNull GlobalProperties pConfig) {
        super();
        this.config = pConfig;
    }

    public static void main(String[] args) {
        SpringApplication.run(RegisterApplication.class);
    }

    @Override
    public void onApplicationEvent(@org.jetbrains.annotations.NotNull ContextRefreshedEvent event) {
        try {
            if (event.getApplicationContext().getParent() == null) {
                // Evidence in the LOG the correct start of the services
                log.info(LINE);
                log.info(LOG_START_PROJECT, config.getName());
                log.info(LOG_PORT_OF_PROJECT, config.getRestPort());
                log.info(LOG_PROJECT_VERSION, config.getVersion());
                log.info(LOG_RUN_OK);
                log.info(LINE);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

}

