package co.movii.auth.server;

import co.movii.auth.server.conf.StatusCodeConfig;
import co.movii.auth.server.properties.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import javax.validation.constraints.NotNull;

@Slf4j
@SpringBootApplication
@EnableConfigurationProperties(value = {
        StatusCodeConfig.class,
        ExtraValidationsProperties.class,
        MahindraProperties.class,
        SupportOTPProperties.class,
        SupportProfileProperties.class,
        SupportUserProperties.class,
        SupportSmsProperties.class,
        MahindraFacadeProperties.class,
        RegisterProperties.class
})
public class AuthenticationApplication implements ApplicationListener<ContextRefreshedEvent> {

    private static final String LOG_LINE = "-------------------------------------------";

    private final GlobalProperties globalProperties;

    public AuthenticationApplication(@NotNull GlobalProperties pglobalProperties) {
        super();
        this.globalProperties = pglobalProperties;
    }

    public static void main(String[] args) {
        final SpringApplication application = new SpringApplication(AuthenticationApplication.class);
        application.run();
    }

    @Override
    public final void onApplicationEvent(@org.jetbrains.annotations.NotNull ContextRefreshedEvent event) {
        try {
            if (event.getApplicationContext().getParent() == null) {
                // Evidenciar en el LOG el inicio correcto de los servicios
                log.info("");
                log.info(LOG_LINE);
                log.info("{} - Port: {}", globalProperties.getApplicationName(), globalProperties.getRestPort());
                log.info("{} - Launched [OK]", globalProperties.getApplicationName());
                log.info(LOG_LINE);
                log.info("");
                log.info(LOG_LINE);
                log.info("{} - Version: {}", globalProperties.getApplicationName(), globalProperties.getApplicationVersion());
                log.info(LOG_LINE);
                log.info("");
            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

}

