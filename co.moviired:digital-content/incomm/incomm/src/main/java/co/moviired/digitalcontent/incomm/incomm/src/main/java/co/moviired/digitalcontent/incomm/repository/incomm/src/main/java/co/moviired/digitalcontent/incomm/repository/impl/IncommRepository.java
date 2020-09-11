package co.moviired.digitalcontent.incomm.repository.impl;

import co.moviired.digitalcontent.incomm.properties.GlobalProperties;
import co.moviired.digitalcontent.incomm.repository.IIncommRepository;
import lombok.extern.slf4j.Slf4j;
import org.jpos.iso.ISOMsg;
import org.jpos.q2.Q2;
import org.jpos.q2.iso.QMUX;
import org.jpos.util.NameRegistrar;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Repository;

import javax.annotation.PreDestroy;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

/*
 * Copyright @2019. MOVIIRED, SAS. Todos los derechos reservados.
 *
 * @author RIVAS, Ronel
 * @version 1, 2019-01-24
 * @since 1.0
 */

@Slf4j
@Repository
public class IncommRepository implements IIncommRepository {

    private static final long serialVersionUID = 4916954210755378039L;
    private final transient Q2 q2;

    private GlobalProperties config;
    private transient QMUX incommClient;

    // Abrir la conexión
    public IncommRepository(GlobalProperties pconfig) throws IOException {
        super();
        this.config = pconfig;

        // Directorio de configuración real
        Resource resource = new ClassPathResource("/deploy/");
        File deploy = resource.getFile();
        String deployDir = deploy.getCanonicalPath();

        // Cargar la configuración en los XML
        // Adapter
        String adapterContent = new String(Files.readAllBytes(Paths.get(deployDir + "/incommConnectionAdapter.xml")));
        adapterContent = adapterContent.replace("##ISO_CONFIG##", deployDir + "/../package/ISO87A_Incomm.xml");
        adapterContent = adapterContent.replace("##HOST##", config.getIncommHost());
        adapterContent = adapterContent.replace("##PORT##", config.getIncommPort().toString());
        adapterContent = adapterContent.replace("##DEBUG##", config.getIncommDebug().toString());
        adapterContent = adapterContent.replace("##RECONNECT##", config.getIncommReconnect().toString());
        Files.write(Paths.get(deployDir + "/incommConnectionAdapter.xml"), adapterContent.getBytes(StandardCharsets.UTF_8));

        // Multiplexor
        String multiplexorContent = new String(Files.readAllBytes(Paths.get(deployDir + "/incommMultiplexor.xml")));
        multiplexorContent = multiplexorContent.replace("##KEY##", config.getIncommKey());
        Files.write(Paths.get(deployDir + "/incommMultiplexor.xml"), multiplexorContent.getBytes(StandardCharsets.UTF_8));

        // Iniciar las colas
        log.info("\t--> Configurando COLAS y MUX - Iniciado");
        this.q2 = new Q2(deployDir);
        this.q2.start();
        log.info("\t--> Configurando COLAS y MUX - Finalizado");
    }

    @PreDestroy
    public void stop() {
        log.info("Undeploy all Q2");
        if (q2 != null && q2.running()) {
            q2.shutdown();
        }
    }

    @Override
    public ISOMsg sendRequest(ISOMsg sendMesg) throws IOException {
        ISOMsg response;
        try {
            // Obtener el cliente a InComm
            if (incommClient == null) {
                incommClient = NameRegistrar.get("mux.INCOMMMUX");
            }

            response = incommClient.request(sendMesg, this.config.getTimeOutRead());

        } catch (Exception e) {
            log.error(e.getMessage());
            throw new IOException(e);
        }

        return response;
    }


}

