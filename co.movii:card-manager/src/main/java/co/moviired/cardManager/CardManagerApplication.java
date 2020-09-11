package co.moviired.cardManager;

import co.moviired.cardManager.properties.GlobalProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import javax.validation.constraints.NotNull;

@Slf4j
@SpringBootApplication
public class CardManagerApplication implements ApplicationListener<ContextRefreshedEvent> {

    private static final String LOG_LINE = "-------------------------------------------";
    private final GlobalProperties config;

    public CardManagerApplication(GlobalProperties pglobalProperties) {
        this.config = pglobalProperties;
    }

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(CardManagerApplication.class);
        app.run();
    }

    @Override
    public final void onApplicationEvent(@NotNull ContextRefreshedEvent event) {

        try {
            if (event.getApplicationContext().getParent() == null) {

                String logFormatted2 = "{} {}";
                // Evidenciar en el LOG el inicio correcto de los servicios
                log.info("");
                log.info(LOG_LINE);
                //----- Se bede crar la clase config para traer los datos del yml -----
                log.info(logFormatted2, config.getApplicationName(), "application started");
                log.info(logFormatted2, "Port: ", config.getRestPort());
                log.info(logFormatted2, "Version: ", config.getApplicationVersion());
                log.info("Launched [OK]");
                log.info(LOG_LINE);
                log.info("");
            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}

