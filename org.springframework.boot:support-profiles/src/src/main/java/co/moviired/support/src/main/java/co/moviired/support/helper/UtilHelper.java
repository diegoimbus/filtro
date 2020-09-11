package co.moviired.support.helper;
/*
 * Copyright @2019. Movii, SAS. Todos los derechos reservados.
 *
 * @author Bejarano, Cindy
 * @version 1, 2019
 * @since 1.0
 */

public class UtilHelper {
    private static final Integer LENGTH = 32;

    private UtilHelper(){
        super();
    }

    public static final String generateCorrelationId() {
        String result = java.util.UUID.randomUUID().toString();

        result=result.replace("-", "").replace("-", "");
        result=result.substring(0, LENGTH);
        return result;
    }

}

