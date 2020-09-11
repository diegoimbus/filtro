package co.moviired.digitalcontent.business.helper;

import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

@Slf4j
public final class Utilidades implements Serializable {

    private static final long serialVersionUID = 9142537210745235360L;

    private Utilidades() {
        super();
    }

    public static boolean isInteger(String numero) {
        try {
            Long.parseLong(numero);
            return true;
        } catch (NumberFormatException e) {
            log.error("Ocurrio un error al intentar formatear el numero: " + e.getMessage());
            return false;
        }
    }

    public static String generateCorrelationId() {
        String result = java.util.UUID.randomUUID().toString();

        result = result.replace("-", "");
        result = result.substring(0, 20);
        return result;
    }

}

