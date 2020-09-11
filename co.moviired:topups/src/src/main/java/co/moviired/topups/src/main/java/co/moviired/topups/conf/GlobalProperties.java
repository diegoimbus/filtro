package co.moviired.topups.conf;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;

/**
 * @author carlossaul.ramirez
 * @version 0.0.1
 */

@Data
@ConfigurationProperties("spring.application")
public class GlobalProperties implements Serializable {

    // Nombre de la aplicación
    private String name;

    // Versión de la aplicación
    private String version;

    // Puertos de comunicación
    private int restPort;

}

