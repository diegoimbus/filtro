package co.moviired.microservice.helper;
/*
 * Copyright @2019. Movii, SAS. Todos los derechos reservados.
 *
 * @author Bejarano, Cindy
 * @version 1, 2019
 * @since 1.0
 */


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import java.util.Map;

@Data
@EnableConfigurationProperties
@ConfigurationProperties
public class ErrorHelper {

    private static final String DEFAULT_ERROR_CODE = "99";

    // ERRORES

    private Map<String, String> errors;

    public String getError(String errorCode, String message) {

        // Buscar el error
        String response = errors.get(errorCode);

        // Si no lo consigue devolver el error genérico
        if (response == null) {
            response = errorCode + "|" + message;
        }
        return response;
    }

}


