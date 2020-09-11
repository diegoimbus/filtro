package co.moviired.topups;


import co.moviired.base.domain.enumeration.CryptoSpec;
import co.moviired.base.helper.CryptoHelper;
import co.moviired.connector.connector.ReactiveConnector;
import co.moviired.topups.conf.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.env.Environment;

import javax.validation.constraints.NotNull;
import java.nio.charset.StandardCharsets;

/**
 * @author carlossaul.ramirez
 * @version 0.0.1
 */

@Slf4j
@EnableCaching
@EnableConfigurationProperties(value = {
        GlobalProperties.class,
        GestorIdConfigProperties.class,
        MahindraProperties.class,
        MahindraExpDateProperties.class,
        IssuerDateParserProperties.class
})
@SpringBootApplication
public class TopupsApplication implements ApplicationListener<ContextRefreshedEvent> {
    private static final String LOG_LINE = "-------------------------------------------";

    private final GlobalProperties config;

    public TopupsApplication(@NotNull GlobalProperties config) {
        super();
        this.config = config;
    }

    public static void main(String[] args) {
        SpringApplication.run(TopupsApplication.class, args);
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        try {
            // Evidenciar en el LOG el inicio correcto de los servicios
            log.info("");
            log.info(LOG_LINE);
            log.info("{}: API REST - Port: {}", config.getName(), config.getRestPort());
            log.info("{}: API REST - Launched [OK]", config.getName());
            log.info(LOG_LINE);
            log.info("");
            log.info(LOG_LINE);
            log.info("{}: VERSION: {}", config.getName(), config.getVersion());
            log.info(LOG_LINE);
            log.info("");

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    // Clientes: MAHINDRA
    @Bean(name = "mhTransactionalClient")
    public ReactiveConnector mhTransactionalClient(MahindraProperties mahindraProperties) {
        return new ReactiveConnector(mahindraProperties.getUrlTransactional(), mahindraProperties.getConnectionTimeout(), mahindraProperties.getReadTimeout());
    }

    @Bean("cryptoHelper")
    public CryptoHelper cryptoHelper(Environment environment) {
        return new CryptoHelper(StandardCharsets.UTF_8, CryptoSpec.AES_CBC,
                environment.getRequiredProperty("crypt.key"),
                environment.getRequiredProperty("crypt.initializationVector"));
    }

}

