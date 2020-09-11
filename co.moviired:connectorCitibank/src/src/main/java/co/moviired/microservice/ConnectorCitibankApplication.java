package co.moviired.microservice;

import co.moviired.microservice.conf.GlobalProperties;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import reactor.util.annotation.NonNull;

@Slf4j
@AllArgsConstructor
@SpringBootApplication
public class ConnectorCitibankApplication implements ApplicationListener<ContextRefreshedEvent> {

    private final GlobalProperties globalProperties;

    private static final String FORMATTED_LOG_2 = "{} {}";
    private static final String LOG_LINE = "-------------------------------------------";

    public static void main(String[] args) {
        SpringApplication.run(ConnectorCitibankApplication.class, args);
    }

    @Override
    public void onApplicationEvent(@NonNull ContextRefreshedEvent event) {
        try {
            if (event.getApplicationContext().getParent() == null) {
                // Evidenciar en el LOG el inicio correcto de los servicios
                log.info("");
                log.info(LOG_LINE);
                log.info(FORMATTED_LOG_2, globalProperties.getApplicationName(), "application started");
                log.info(FORMATTED_LOG_2, "Port: ", globalProperties.getRestPort());
                log.info(FORMATTED_LOG_2, "Version: ", globalProperties.getApplicationVersion());
                log.info("Launched [OK]");
                log.info(LOG_LINE);
                log.info("");
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}

