package co.moviired.support.exceptions;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.IOException;

/*
 * Copyright @2019. MOVIIRED, SAS. Todos los derechos reservados.
 *
 * @author Cuadros Cerpa, Cristian Alfonso
 * @version 1, 2018-06-20
 * @since 1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ManagerException extends IOException {

    private final Integer tipo;
    private final String codigo;
    private final String transactionId;

    public ManagerException(Integer ptipo, String pcodigo, String mensaje) {
        super(mensaje);
        this.tipo = ptipo;
        this.codigo = pcodigo;
        this.transactionId = null;
    }

    public ManagerException(String ptransactionId, Integer ptipo, String pcodigo, String mensaje) {
        super(mensaje);
        this.transactionId = ptransactionId;
        this.tipo = ptipo;
        this.codigo = pcodigo;
    }

}

