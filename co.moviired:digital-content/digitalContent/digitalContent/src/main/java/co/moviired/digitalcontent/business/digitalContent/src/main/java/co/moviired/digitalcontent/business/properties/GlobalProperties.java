package co.moviired.digitalcontent.business.properties;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.Serializable;

@Data
@Configuration
public class GlobalProperties implements Serializable {

    private static final long serialVersionUID = -3395163916611319213L;

    // Versión de la aplicación
    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${spring.application.version}")
    private String applicationVersion;

    // Puertos de comunicación
    @Value("${server.port}")
    private int restPort;

    // MAIL
    @Value("${properties.mail.urlMoviired}")
    private String urlMail;

    @Value("${properties.mail.urlMovii}")
    private String urlMailMovii;


    // HOMOLOGACION

    @Value("${properties.homologacion.process_activate}")
    private String processActivate;

    @Value("${properties.homologacion.process_inactivate}")
    private String processInactivate;

    @Value("${properties.homologacion.process_pines}")
    private String processPines;


}

