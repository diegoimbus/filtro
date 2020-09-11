package co.moviired.digitalcontent.incomm;

import co.moviired.digitalcontent.incomm.helper.ErrorHelper;
import co.moviired.digitalcontent.incomm.properties.GlobalProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.validation.constraints.NotNull;

@Slf4j
@EnableScheduling
@SpringBootApplication
@EnableConfigurationProperties(value = {
        ErrorHelper.class
})
public class IncommApplication implements ApplicationListener<ContextRefreshedEvent> {

    private static final String LOG_LINE = "---------------------------------------------";

    private final GlobalProperties globalProperties;

    public IncommApplication(@NotNull GlobalProperties pglobalProperties) {
        super();
        this.globalProperties = pglobalProperties;
    }

    public static void main(String[] args) {
        final SpringApplication application = new SpringApplication(IncommApplication.class);
        application.run(args);
    }

    @Override
    public final void onApplicationEvent(ContextRefreshedEvent event) {
        try {
            if (event.getApplicationContext().getId() != null) {
                String logFormatted2 = "{} {}";
                // Evidenciar en el LOG el inicio correcto de los servicios

                log.info(LOG_LINE);
                log.info(logFormatted2, globalProperties.getApplicationName(), "application started ");
                log.info(logFormatted2, "Port: ", globalProperties.getRestPort());
                log.info(logFormatted2, "Version: ", globalProperties.getApplicationVersion());
                log.info("Launched [OK]");
                log.info(LOG_LINE);


            }

        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

}

