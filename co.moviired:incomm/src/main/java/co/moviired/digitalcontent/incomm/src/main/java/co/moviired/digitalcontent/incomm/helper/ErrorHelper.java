package co.moviired.digitalcontent.incomm.helper;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;
import java.util.Map;

@Data
@ConfigurationProperties(value = "properties")
public class ErrorHelper implements Serializable {
    private static final long serialVersionUID = 4998671874227557386L;

    private static final String DEFAULT_ERROR_CODE = "99";

    // ERRORES
    private Map<String, String> errors;

    public final String getError(String errorCode, String message) {

        // Buscar el error
        String response = errors.get(errorCode);

        // Si no lo consigue devolver el error genérico
        if (response == null) {
            response = errorCode + "|" + message;
        }


        return response;
    }

}

