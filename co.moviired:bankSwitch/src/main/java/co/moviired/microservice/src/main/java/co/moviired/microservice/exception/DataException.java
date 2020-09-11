package co.moviired.microservice.exception;
/*
 * Copyright @2019. Movii, SAS. Todos los derechos reservados.
 *
 * @author Bejarano, Cindy
 * @version 1, 2019
 * @since 1.0
 */

import co.moviired.microservice.domain.enums.ErrorType;

public class DataException extends ServiceException {

    public DataException() {
        super(ErrorType.DATA, "500", "Los datos provistos en la petición no son correctos o no se proporcionaron");
    }

    public DataException(String codigo, String mensaje) {
        super(ErrorType.DATA, codigo, mensaje);
    }

    public DataException(Exception e) {
        super(ErrorType.DATA, "500", e.getMessage());
    }

}

