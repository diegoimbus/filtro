package co.moviired.microservice.exception;
/*
 * Copyright @2019. Movii, SAS. Todos los derechos reservados.
 *
 * @author Bejarano, Cindy
 * @version 1, 2019
 * @since 1.0
 */

import co.moviired.microservice.domain.enums.ErrorType;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class ServiceException extends Exception {

    private ErrorType tipo;
    private String codigo;
    private String mensaje;

    public ServiceException(ErrorType ptipo, String pcodigo, String pmensaje) {
        super(pmensaje);

        this.tipo = ptipo;
        this.codigo = pcodigo;
        this.mensaje = pmensaje;
    }

    @Override
    public String toString() {
        return "ServiceException: { tipo=" + tipo + ", codigo=" + codigo + ", mensaje='" + mensaje + "' }";
    }
}


