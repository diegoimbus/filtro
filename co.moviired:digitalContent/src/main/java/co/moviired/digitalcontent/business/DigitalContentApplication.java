package co.moviired.digitalcontent.business;

import co.moviired.digitalcontent.business.conf.StatusCodeConfig;
import co.moviired.digitalcontent.business.helper.AESCrypt;
import co.moviired.digitalcontent.business.properties.GlobalProperties;
import co.moviired.digitalcontent.business.properties.ZeusProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javax.validation.constraints.NotNull;

@Slf4j
@EnableJpaRepositories
@EnableConfigurationProperties(value = {
        StatusCodeConfig.class,
        ZeusProperties.class
})
@SpringBootApplication
public class DigitalContentApplication implements ApplicationListener<ContextRefreshedEvent> {

    private static final String LOG_LINE = "----------------------------------------------------";

    private final GlobalProperties globalProperties;

    public DigitalContentApplication(@NotNull GlobalProperties pglobalProperties) {
        super();
        this.globalProperties = pglobalProperties;
        AESCrypt.init(globalProperties);
    }

    public static void main(String[] args) {
        final SpringApplication application = new SpringApplication(DigitalContentApplication.class);
        application.run();
    }

    @Override
    public final void onApplicationEvent(ContextRefreshedEvent event) {
        try {
            if (event.getApplicationContext().getId() != null) {
                String logFormatted2 = "{} {}";
                // Evidenciar en el LOG el inicio correcto de los servicios

                log.info(LOG_LINE);
                log.info(logFormatted2, globalProperties.getApplicationName(), " application started ");
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

