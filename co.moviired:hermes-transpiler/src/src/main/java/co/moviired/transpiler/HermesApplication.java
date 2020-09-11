package co.moviired.transpiler;

import co.moviired.transpiler.conf.GlobalProperties;
import co.moviired.transpiler.conf.IsoProperties;
import co.moviired.transpiler.helper.AESCrypt;
import co.moviired.transpiler.helper.ErrorHelper;
import co.moviired.transpiler.integration.iso.IsoController;
import co.moviired.transpiler.integration.iso.model.IsoClient;
import co.moviired.transpiler.integration.iso.parser.IsoParserFactory;
import co.moviired.transpiler.integration.iso.service.IsoService;
import co.moviired.transpiler.jpa.movii.repository.IProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jpos.iso.ISOException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.event.ContextRefreshedEvent;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@SpringBootApplication
@EnableConfigurationProperties(value = {ErrorHelper.class, IsoProperties.class})
@ComponentScan(basePackages = {"co.moviired.*"})
public class HermesApplication implements ApplicationListener<ContextRefreshedEvent> {

    private static final String LOG_LINE = "--------------------------------------------------";

    private final ApplicationContext applicationContext;
    private final GlobalProperties globalProperties;
    private final IsoProperties isoProperties;

    public HermesApplication(
            @NotNull ApplicationContext ctx,
            @NotNull IsoProperties isoProperties,
            @NotNull GlobalProperties pglobalProperties) {
        super();
        this.applicationContext = ctx;
        this.globalProperties = pglobalProperties;
        this.isoProperties = isoProperties;
        AESCrypt.init(globalProperties);
    }

    public static void main(String[] argv) {
        SpringApplication.run(HermesApplication.class);
    }

    @Override
    public final void onApplicationEvent(@org.jetbrains.annotations.NotNull ContextRefreshedEvent event) {
        try {
            if (event.getApplicationContext().getParent() == null) {
                // Iniciar entrada: ISO
                ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
                List<IsoClient> clients = isoProperties.getClients();
                if ((clients != null) && (!clients.isEmpty())) {
                    final IProductRepository productRepository = applicationContext.getBean(IProductRepository.class);

                    log.info("");
                    log.info(LOG_LINE);
                    log.info("{}: ISO-8583 - Launch starts...", globalProperties.getApplicationName());
                    for (IsoClient client : clients) {
                        // Crear Factory/Service/Controller for specific client
                        final IsoService isoService = new IsoService(applicationContext, client.getName(), client.getPackager());
                        final IsoParserFactory parserFactory = new IsoParserFactory(applicationContext, client.getPackager());
                        IsoController isoController = new IsoController(client.getPackager(), parserFactory, isoService, productRepository);
                        isoController.setSocketProperties(client.getPort(), isoProperties.getTimeoutConnection(), isoProperties.getTimeoutRead());

                        log.info("\tClient type: {} - Port: {}", StringUtils.rightPad(client.getName(), 10, ' '), client.getPort());
                        executor.execute(isoController);
                    }
                    log.info("{}: ISO-8583 - Launched [OK]", globalProperties.getApplicationName());
                    log.info(LOG_LINE);
                }
                executor.shutdown();

                // Evidenciar en el LOG el inicio correcto de los servicios
                log.info("");
                log.info(LOG_LINE);
                log.info("{}: API REST - Port: {}", globalProperties.getApplicationName(), globalProperties.getRestPort());
                log.info("{}: API REST - Launched [OK]", globalProperties.getApplicationName());
                log.info(LOG_LINE);
                log.info("");
                log.info(LOG_LINE);
                log.info("{}: SOAP - Port: {}", globalProperties.getApplicationName(), globalProperties.getSoapPort());
                log.info("{}: SOAP - Launched [OK]", globalProperties.getApplicationName());
                log.info(LOG_LINE);
                log.info("");
                log.info("");
                log.info(LOG_LINE);
                log.info("{} - Versión: {}", globalProperties.getApplicationName(), globalProperties.getApplicationVersion());
                log.info(LOG_LINE);
                log.info("");
                log.info("");
            }

        } catch (ISOException | IOException e) {
            log.error(e.getMessage(), e);
        }
    }

}

