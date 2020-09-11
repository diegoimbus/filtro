package co.moviired.microservice.exception;
/*
 * Copyright @2019. Movii, SAS. Todos los derechos reservados.
 *
 * @author Bejarano, Cindy
 * @version 1, 2019
 * @since 1.0
 */

public class ParseException extends Exception {

    private static final long serialVersionUID = 2323292208990801207L;

    public ParseException(String message) {
        super(message);
    }

    public ParseException(Throwable cause) {
        super(cause);
    }
}


