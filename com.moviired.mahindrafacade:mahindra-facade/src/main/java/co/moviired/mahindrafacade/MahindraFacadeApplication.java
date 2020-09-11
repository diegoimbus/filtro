package co.moviired.mahindrafacade;

import co.moviired.mahindrafacade.properties.GlobalProperties;
import co.moviired.mahindrafacade.properties.MahindraProperties;
import co.moviired.mahindrafacade.properties.StatusCodeConfig;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;


@Slf4j
@EnableConfigurationProperties(value = {
        StatusCodeConfig.class,
        MahindraProperties.class
})
@SpringBootApplication
public class MahindraFacadeApplication implements ApplicationListener<ContextRefreshedEvent> {

    private static final String LOG_LINE = "----------------------------------------------";
    private final GlobalProperties globalProperties;

    public MahindraFacadeApplication(GlobalProperties pglobalProperties) {
        this.globalProperties = pglobalProperties;
    }

    public static void main(String[] args) {
        // Iniciar la aplicación
        SpringApplication.run(MahindraFacadeApplication.class, args);
    }

    @Override
    public void onApplicationEvent(@NotNull ContextRefreshedEvent event) {
        try {
            if (event.getApplicationContext().getParent() == null) {
                String logFormatted2 = "{} {}";

                // Configuracion del servidor netty
                System.setProperty("reactor.netty.ioWorkerCount", String.valueOf(Math.max(Runtime.getRuntime().availableProcessors(), globalProperties.getIoWorkerCount())));
                System.setProperty("reactor.netty.ioSelectCount", globalProperties.getIoSelectCount());
                System.setProperty("reactor.netty.pool.maxConnections", globalProperties.getMaxConnections());
                System.setProperty("reactor.netty.pool.maxIdleTime", globalProperties.getMaxIdleTime());
                System.setProperty("reactor.netty.pool.leasingStrategy", globalProperties.getLeasingStrategy());

                // Evidenciar en el LOG el inicio correcto de los servicios
                log.info("");
                log.info(LOG_LINE);
                log.info(logFormatted2, globalProperties.getApplicationName(), " application started ");
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

