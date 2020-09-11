package co.moviired.transaction;


import co.moviired.transaction.properties.GlobalParameters;
import co.moviired.transaction.properties.MahindraProperties;
import co.moviired.transaction.properties.MoviiService;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@EnableConfigurationProperties(value = {MoviiService.class, MahindraProperties.class})
@SpringBootApplication
public class TransactionApplication implements ApplicationListener<ContextRefreshedEvent> {

    private static final String LOG_LINE = "-----------------------------------";

    private final GlobalParameters globalParameters;

    public TransactionApplication(GlobalParameters pglobalParameters) {
        this.globalParameters = pglobalParameters;
    }

    public static void main(String[] args) {
        // Iniciar entrada: REST
        SpringApplication.run(TransactionApplication.class);
    }

    @Override
    public final void onApplicationEvent(@NotNull ContextRefreshedEvent event) {
        try {
            if (event.getApplicationContext().getParent() == null) {

                // Evidenciar en el LOG el inicio correcto de los servicios
                log.info("");
                log.info(LOG_LINE);
                String appName = globalParameters.getApplicationName();
                log.info(appName + " application started ");
                log.info("Port: " + globalParameters.getRestPort());
                log.info("Version: " + globalParameters.getApplicationVersion());
                log.info("Launched [OK]");
                log.info(LOG_LINE);
                log.info("");
            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}

