package co.moviired.support.otp;

import co.moviired.support.otp.conf.StatusCodeConfig;
import co.moviired.support.otp.properties.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.validation.constraints.NotNull;

@Slf4j
@EnableScheduling
@EnableJpaRepositories
@SpringBootApplication
@EnableConfigurationProperties(value = {GlobalProperties.class, OtpProperties.class, StatusCodeConfig.class, SmsProperties.class, EmailProperties.class, GuarumoProperties.class})
public class SupportOTPApplication implements ApplicationListener<ContextRefreshedEvent> {

    private static final String LINE = "------------------------------------------------";
    private final GlobalProperties config;

    public SupportOTPApplication(@NotNull GlobalProperties config) {
        super();
        this.config = config;
    }

    public static void main(String[] args) {
        SpringApplication.run(SupportOTPApplication.class, args);
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        try {
            if (event.getApplicationContext().getParent() == null) {
                // Evidenciar en el LOG el inicio correcto de los servicios
                log.info(LINE);
                log.info("{} Application started", config.getName());
                log.info("Port: {}", config.getRestPort());
                log.info("Version: {}", config.getVersion());
                log.info("Launched [OK]");
                log.info(LINE);
            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}

