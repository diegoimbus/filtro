package co.moviired.moneytransfer;

import co.moviired.moneytransfer.properties.*;
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
@EnableConfigurationProperties(value = {
        StatusCodeConfig.class,
        MahindraProperties.class,
        RegistraduriaProperties.class,
        SmsProperties.class,
        BlackListProperties.class,
        SupportAuthenticationProperties.class
})
@SpringBootApplication
public class MoneyTransferApplication implements ApplicationListener<ContextRefreshedEvent> {

    private static final String LOG_LINE = "----------------------------------------------";
    private final GlobalProperties globalProperties;

    public MoneyTransferApplication(GlobalProperties pGlobalProperties) {
        this.globalProperties = pGlobalProperties;
    }

    public static void main(String[] args) {
        SpringApplication.run(MoneyTransferApplication.class, args);
    }

    @Override
    public void onApplicationEvent(@NotNull ContextRefreshedEvent event) {
        try {
            if (event.getApplicationContext().getParent() == null) {
                String logFormatted2 = "{} {}";
                // Evidenciar en el LOG el inicio correcto de los servicios
                log.info("");
                log.info(LOG_LINE);
                log.info(logFormatted2, globalProperties.getApplicationName(), "application started");
                log.info(logFormatted2, "Port: ", globalProperties.getRestPort());
                log.info(logFormatted2, "Version: ", globalProperties.getApplicationVersion());
                log.info("Launched [OK]");
                log.info(LOG_LINE);
                log.info("");
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

}

