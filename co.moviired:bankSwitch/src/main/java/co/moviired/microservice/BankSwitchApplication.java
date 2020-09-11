package co.moviired.microservice;
/*
 * Copyright @2019. Movii, SAS. Todos los derechos reservados.
 *
 * @author Bejarano, Cindy
 * @version 1, 2019
 * @since 1.0
 */

import co.moviired.connector.connector.IsoConnector;
import co.moviired.microservice.conf.GlobalProperties;
import co.moviired.microservice.conf.SwitchProperties;
import lombok.extern.slf4j.Slf4j;
import org.jpos.iso.ISOException;
import org.jpos.iso.packager.GenericPackager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;


@Component
@SpringBootApplication
@Slf4j
public class BankSwitchApplication implements ApplicationListener<ContextRefreshedEvent> {

    private static final String LOG_LINE = "-------------------------------------------";
    private static final String FORMATTED_LOG_2 = "{} {}";
    private final GlobalProperties config;

    public BankSwitchApplication(GlobalProperties config) {
        this.config = config;
    }

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(BankSwitchApplication.class);
        app.run(args);
    }

    @Override
    public final void onApplicationEvent(ContextRefreshedEvent event) {
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

    // Cliente: ISO -
    @Bean
    public IsoConnector switchClient(SwitchProperties config) throws ISOException, IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return new IsoConnector(
                new GenericPackager(new ClassPathResource("iso8583/iso-message.xml").getInputStream()),
                config.getSocketIP(),
                config.getSocketPuerto(),
                config.getConexionTimeout()
        );
    }

}

