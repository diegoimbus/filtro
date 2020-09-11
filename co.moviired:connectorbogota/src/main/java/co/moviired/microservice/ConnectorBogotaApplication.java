package co.moviired.microservice;

/*
 * Copyright @2019. Movii, SAS. Todos los derechos reservados.
 *
 * @author Cindy Bejarano, Oscar Lopez
 * @since 1.0.0
 */

import co.moviired.microservice.conf.GlobalProperties;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@SpringBootApplication
public class ConnectorBogotaApplication implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private final GlobalProperties config;

    private static final String FORMATTED_LOG_2 = "{} {}";
    private static final String LOG_LINE = "-------------------------------------------";

    public ConnectorBogotaApplication(GlobalProperties config) {
        this.config = config;
    }

    public static void main(String[] args) {
        SpringApplication.run(ConnectorBogotaApplication.class, args);
    }

    @Override
    public final void onApplicationEvent(@NotNull ContextRefreshedEvent event) {
        try {
            if (event.getApplicationContext().getParent() == null) {
                // Evidenciar en el LOG el inicio correcto de los servicios
                log.info("");
                log.info(LOG_LINE);
                String appName = config.getApplicationName();
                log.info(FORMATTED_LOG_2, appName, " application started ");
                log.info(FORMATTED_LOG_2, "Port: ", config.getRestPort());
                log.info(FORMATTED_LOG_2, "Version: ", config.getApplicationVersion());
                log.info("Launched [OK]");
                log.info(LOG_LINE);
                log.info("");
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

}

