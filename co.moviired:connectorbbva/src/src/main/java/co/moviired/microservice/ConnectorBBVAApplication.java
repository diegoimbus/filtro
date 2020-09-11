package co.moviired.microservice;

import co.moviired.microservice.client.soap.cargos.impl.CargosOperacionesService;
import co.moviired.microservice.client.soap.cargos.impl.OperacionesCargos;
import co.moviired.microservice.client.soap.operacionesclean.impl.OperacionesRecaudos;
import co.moviired.microservice.client.soap.operacionesclean.impl.RecaudosOperacionesService;
import co.moviired.microservice.client.soap.seguridadbasecb.impl.SeguridadBase;
import co.moviired.microservice.client.soap.seguridadbasecb.impl.SeguridadBase_Service;
import co.moviired.microservice.conf.BankProductsProperties;
import co.moviired.microservice.conf.GlobalProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

@Component
@SpringBootApplication
@Slf4j
public class ConnectorBBVAApplication implements ApplicationListener<ContextRefreshedEvent> {

    private static final String LOG_LINE = "-------------------------------------------";
    private static final String FORMATTED_LOG_2 = "{} {}";
    private final GlobalProperties config;
    private final BankProductsProperties bankProducts;

    public ConnectorBBVAApplication(GlobalProperties config,
                                    BankProductsProperties bankProducts) {
        this.config = config;
        this.bankProducts = bankProducts;
    }

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(ConnectorBBVAApplication.class);
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

    @Bean(name = "seguridadBaseService")
    public SeguridadBase seguridadBaseService() throws IOException {
        replaceSeguridadURL();
        SeguridadBase_Service serviceTicket = new SeguridadBase_Service();
        return serviceTicket.getSeguridadBaseSOAP();
    }

    @Bean(name = "operacionCleanService")
    public OperacionesRecaudos operacionCleanService() throws IOException {
        replaceOperacionURL();
        RecaudosOperacionesService operacionesService = new RecaudosOperacionesService();
        return operacionesService.getBbvaRecaudosOperacionesSoap();
    }

    @Bean(name = "operacionesCargosService")
    public OperacionesCargos operacionesCargosService() throws IOException {
        replaceCargosURL();
        CargosOperacionesService operacionesCargoService = new CargosOperacionesService();
        return operacionesCargoService.getBbvaCargosSOAP();
    }


    private void replaceSeguridadURL() throws IOException {
        Resource resource = new ClassPathResource("/wsdl/seguridadbasecb.wsdl");
        String wsdl = resource.getFile().getCanonicalPath();

        //seguridadbasecb.wsdl
        String content = new String(Files.readAllBytes(Paths.get(wsdl)));
        content = content.replace("##SEGURIDAD_SERVICE_LOCATION##", this.bankProducts.getWsSeguridadBase());
        Files.write(Paths.get(wsdl), content.getBytes(StandardCharsets.UTF_8));

        log.debug("WS seguridadbasecb URL: {}", this.bankProducts.getWsSeguridadBase());
    }

    private void replaceOperacionURL() throws IOException {
        Resource resource = new ClassPathResource("/wsdl/bbvaRecaudos.wsdl");
        String wsdl = resource.getFile().getCanonicalPath();

        //seguridadbasecb.wsdl
        String content = new String(Files.readAllBytes(Paths.get(wsdl)));
        content = content.replace("##RECAUDOS_SERVICE_LOCATION##", this.bankProducts.getWsRecaudo());
        Files.write(Paths.get(wsdl), content.getBytes(StandardCharsets.UTF_8));

        log.debug("WS operacionesclean URL: {}", this.bankProducts.getWsRecaudo());
    }

    private void replaceCargosURL() throws IOException {
        Resource resource = new ClassPathResource("/wsdl/bbvaCargos.wsdl");
        String wsdl = resource.getFile().getCanonicalPath();

        //seguridadbasecb.wsdl
        String content = new String(Files.readAllBytes(Paths.get(wsdl)));
        content = content.replace("##CARGOS_SERVICE_LOCATION##", this.bankProducts.getWsCargos());
        Files.write(Paths.get(wsdl), content.getBytes(StandardCharsets.UTF_8));

        log.debug("WS bbvaCargos URL: {}", this.bankProducts.getWsCargos());
    }
}

