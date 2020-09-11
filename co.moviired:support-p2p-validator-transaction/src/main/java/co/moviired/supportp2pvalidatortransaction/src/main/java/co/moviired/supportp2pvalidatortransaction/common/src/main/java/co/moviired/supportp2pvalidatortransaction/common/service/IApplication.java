package co.moviired.supportp2pvalidatortransaction.common.service;

import co.moviired.supportp2pvalidatortransaction.common.config.GlobalProperties;
import co.moviired.supportp2pvalidatortransaction.common.util.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;

import javax.validation.constraints.NotNull;

import static co.moviired.supportp2pvalidatortransaction.common.util.Constants.*;

@Slf4j
@Configuration
public abstract class IApplication {

    protected final GlobalProperties config;

    public IApplication(@NotNull GlobalProperties config) {
        super();
        this.config = config;
        Utils.assignCorrelative(config, null);
    }

    public void onApplicationEventBase(ContextRefreshedEvent event) {
        try {
            if (event.getApplicationContext().getParent() == null) {
                // Evidence in the LOG the correct start of the services
                log.info(LONG_LINE);
                log.info(LOG_START_PROJECT, config.getName());
                log.info(LOG_PORT_OF_PROJECT, config.getRestPort());
                log.info(LOG_PROJECT_VERSION, config.getVersion());
                log.info(LOG_RUN_OK);
                log.info(LONG_LINE);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}

