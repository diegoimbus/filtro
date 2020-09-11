package co.moviired.transaction.properties;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.Serializable;


/*
 * Copyright @2018. MOVIIRED. Todos los derechos reservados.
 *
 * @author Cuadros Cerpa, Cristian Alfonso
 * @version 1, 2018-06-20
 * @since 1.0
 */

@Data
@Configuration
@NoArgsConstructor
public class GlobalProperties implements Serializable {

    private static final long serialVersionUID = -3395163916611319213L;

    // Versión de la aplicación
    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${spring.application.version}")
    private String applicationVersion;

    @Value("${server.port}")
    private int restPort;


    //Activaciones de los metodos
    @Value("${spring.application.methods.getTransactionsEnable}")
    private boolean getTransactionsEnable;

}

