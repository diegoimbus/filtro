package co.moviired.digitalcontent.incomm.helper;

import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

@Slf4j
public final class Utilidades implements Serializable {

    private static final long serialVersionUID = 9142537210745235360L;
    private static final int ID_LENGTH = 20;

    private Utilidades() {
        super();
    }

    public static String generateCorrelationId() {
        String result = java.util.UUID.randomUUID().toString();

        result = result.replace("-", "");
        result = result.substring(0, ID_LENGTH);
        return result;
    }

}

