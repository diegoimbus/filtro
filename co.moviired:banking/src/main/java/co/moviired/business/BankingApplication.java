package co.moviired.business;

import co.moviired.business.properties.GlobalProperties;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

@Slf4j
@SpringBootApplication
public class BankingApplication implements ApplicationListener<ContextRefreshedEvent> {

    private static final String LOG_LINE = "-------------------------------------------";
    private final GlobalProperties config;

    public BankingApplication(GlobalProperties pglobalProperties) {
        super();
        this.config = pglobalProperties;
    }

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(BankingApplication.class);
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


