package co.moviired.support;
/*
 * Copyright @2019. Movii, SAS. Todos los derechos reservados.
 *
 * @author Bejarano, Cindy
 * @version 1, 2019
 * @since 1.0
 */

import co.moviired.support.conf.GlobalProperties;
import co.moviired.support.conf.StatusCodeConfig;
import co.moviired.support.endpoint.bancobogota.impl.IntegrationBankEndPoint;
import co.moviired.support.endpoint.bancobogota.manager.IntegrationBankManager;
import co.moviired.support.endpoint.bancolombia.impl.IntegrationBancolombiaEndPoint;
import co.moviired.support.endpoint.bancolombia.manager.IntegrationBancolombiaManager;
import co.moviired.support.properties.CmdApproveConsignmentProperties;
import co.moviired.support.properties.MahindraProperties;
import co.moviired.support.properties.SupportUserProperties;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import javax.xml.ws.Endpoint;


@Slf4j
@SpringBootApplication
@EnableScheduling
@Component
@EnableConfigurationProperties(value = {
        SupportUserProperties.class,
        StatusCodeConfig.class,
        CmdApproveConsignmentProperties.class,
        MahindraProperties.class
})
public class ConsignmentApplication implements ApplicationListener<ContextRefreshedEvent> {

    private static final String LOG_LINE = "-------------------------------------------";
    private final GlobalProperties config;

    public ConsignmentApplication(GlobalProperties globalProperties,
                                  IntegrationBankManager integrationBankManager,
                                  IntegrationBancolombiaManager integrationBancolombiaManager) {
        super();
        this.config = globalProperties;
        Endpoint.publish(config.getUrlBogotaWs(), new IntegrationBankEndPoint(integrationBankManager));
        Endpoint.publish(config.getUrlBancolombiaWs(), new IntegrationBancolombiaEndPoint(integrationBancolombiaManager));
    }

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(ConsignmentApplication.class);
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
                log.info(logFormatted2, "URL_WS: Bogota: ", config.getUrlBogotaWs());
                log.info(logFormatted2, "URL_WS: Bancolombia", config.getUrlBancolombiaWs());
                log.info("Launched [OK]");
                log.info(LOG_LINE);
                log.info("");
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

}



