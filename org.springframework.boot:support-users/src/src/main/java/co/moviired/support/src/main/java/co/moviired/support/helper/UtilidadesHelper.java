package co.moviired.support.helper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class UtilidadesHelper {

    private static final Integer NUMBER_32 = 32;


    private UtilidadesHelper() {
        super();
    }

    public static boolean isInteger(String numero) {
        try {
            Long.parseLong(numero);
            return true;
        } catch (NumberFormatException e) {
            log.error("Ocurrio un error al intentar formatear el numero: " + e.getMessage(), e);
            return false;
        }
    }

    public static String generateCorrelationId() {
        String result = java.util.UUID.randomUUID().toString();

        result = result.replace("-", "").replace("-", "");
        result = result.substring(0, UtilidadesHelper.NUMBER_32);
        return result;
    }
}

