package co.moviired.transpiler.helper;

/*
 * Copyright @2019. MOVIIRED, SAS. Todos los derechos reservados.
 *
 * @author RIVAS, Ronel
 * @version 1, 2019-06-27
 * @since 1.0
 */

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;
import java.util.Map;

@Data
@ConfigurationProperties
public class ErrorHelper implements Serializable {

    private static final long serialVersionUID = 6092780672661878995L;

    private static final String DEFAULT_ERROR_CODE = "99";
    private static final String REGEX_EXP = "\\|";

    // ERRORES
    private Map<String, String> errors;

    public final String[] getError(String errorCode) {
        // Validar el parámetro de entrada
        if ((errorCode == null) || (errorCode.trim().isEmpty())) {
            return errors.get(DEFAULT_ERROR_CODE).split(REGEX_EXP);
        }

        // Buscar el error
        String response = errors.get(errorCode);

        // Si no lo consigue devolver el error genérico
        if (response == null) {
            response = errors.get(DEFAULT_ERROR_CODE);
        }

        return response.split(REGEX_EXP);
    }

    public final String[] getError(String errorCode, String mensaje) {

        // Validar el parámetro de entrada
        if ((errorCode == null) || (errorCode.trim().isEmpty())) {
            return errors.get(DEFAULT_ERROR_CODE).split(REGEX_EXP);
        }

        // Buscar el error
        String response = errors.get(errorCode);

        // Si no lo consigue devolver el error genérico
        if (response == null) {
            response = errorCode + "|" + mensaje;
        }


        return response.split(REGEX_EXP);
    }


}

