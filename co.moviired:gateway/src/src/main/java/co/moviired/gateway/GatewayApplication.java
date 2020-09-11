package co.moviired.gateway;

import co.moviired.gateway.properties.AuthenticationProperties;
import co.moviired.gateway.properties.GlobalProperties;
import co.moviired.gateway.properties.PathsProperties;
import co.moviired.gateway.properties.SupportProfilesProperties;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

@Slf4j
@SpringBootApplication
@EnableConfigurationProperties(value = {
        PathsProperties.class,
        AuthenticationProperties.class,
        SupportProfilesProperties.class
})
public class GatewayApplication implements ApplicationListener<ContextRefreshedEvent> {
    private static final String LOG_LINE = "-------------------------------------------";

    private final GlobalProperties config;

    public GatewayApplication(GlobalProperties globalProperties) {
        super();
        this.config = globalProperties;
    }

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }

    @Override
    public final void onApplicationEvent(@NotNull ContextRefreshedEvent event) {
        try {
            // Evidenciar en el LOG el inicio correcto de los servicios
            log.info("");
            log.info(LOG_LINE);
            log.info("{} application started", config.getApplicationName().toUpperCase());
            log.info("Version: {}", config.getApplicationVersion());
            log.info("Launched [OK]");
            log.info(LOG_LINE);
            log.info("");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}


