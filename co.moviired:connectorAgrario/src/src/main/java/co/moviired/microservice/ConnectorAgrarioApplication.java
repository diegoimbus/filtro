package co.moviired.microservice;

import co.moviired.microservice.conf.GlobalProperties;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

@Slf4j
@AllArgsConstructor
@SpringBootApplication
public class ConnectorAgrarioApplication implements ApplicationListener<ContextRefreshedEvent> {

    private static final String FORMATTED_LOG_2 = "{} {}";
    private static final String LOG_LINE = "-------------------------------------------";
    private final GlobalProperties config;

    public static void main(String[] args) {
        SpringApplication.run(ConnectorAgrarioApplication.class, args);
    }

    @Override
    public final void onApplicationEvent(@NotNull ContextRefreshedEvent event) {
        try {
            if (event.getApplicationContext().getParent() == null) {
                // Evidenciar en el LOG el inicio correcto de los servicios
                log.info("");
                log.info(LOG_LINE);
                log.info(FORMATTED_LOG_2, config.getApplicationName(), " application started ");
                log.info(FORMATTED_LOG_2, "Port: ", config.getRestPort());
                log.info(FORMATTED_LOG_2, "Version: ", config.getApplicationVersion());
                log.info("Launched [OK]");
                log.info(LOG_LINE);
                log.info("");
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

}

