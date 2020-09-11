package co.moviired.support;

/*
 * Copyright @2019. Movii, SAS. Todos los derechos reservados.
 *
 * @author Bejarano, Cindy
 * @version 1, 2019
 * @since 1.0
 */

import co.moviired.support.properties.GlobalProperties;
import co.moviired.support.properties.ServiceManagerProperties;
import co.moviired.support.conf.StatusCodeConfig;
import co.moviired.support.properties.*;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

@Slf4j
@SpringBootApplication
@EnableScheduling
@Component
@EnableConfigurationProperties(value = {
        StatusCodeConfig.class,
        ConsultBalanceProperties.class,
        CmdConsultBalanceProperties.class,
        MahindraProperties.class,
        EmailGeneratorProperties.class,
        CertificatesProperties.class,
        ServiceManagerProperties.class
})
public class StatementAccountApplication implements ApplicationListener<ContextRefreshedEvent> {

    private static final String LOG_LINE = "-------------------------------------------";

    private final GlobalProperties config;

    public StatementAccountApplication(GlobalProperties pglobalProperties) {
        super();
        this.config = pglobalProperties;
    }

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(StatementAccountApplication.class);
        app.run(args);
    }

    @Override
    public final void onApplicationEvent(@NotNull ContextRefreshedEvent event) {
        try {
            if (event.getApplicationContext().getParent() == null) {

                String logFormatted2 = "{} {}";
                // Evidenciar en el LOG el inicio correcto de los servicios
                log.info("");
                log.info(LOG_LINE);
                log.info(logFormatted2, config.getApplicationName(), " application started ");
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



