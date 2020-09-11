package co.movii.auth.server.exception;
/*
 * Copyright @2019. Movii, SAS. Todos los derechos reservados.
 *
 * @author Bejarano, Cindy
 * @version 1, 2019
 * @since 1.0
 */

public class MaxDevicesException extends AuthException {
    public MaxDevicesException(String message) {
        super(message);
    }
}

