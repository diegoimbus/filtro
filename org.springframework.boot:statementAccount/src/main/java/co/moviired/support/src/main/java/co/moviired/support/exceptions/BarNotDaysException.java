package co.moviired.support.exceptions;

import co.moviired.support.domain.entity.account.BarTemplate;

/*
 * Copyright @2018. MOVIIRED. Todos los derechos reservados.
 *
 * @author Cuadros Cerpa, Cristian Alfonso
 * @version 1, 2018-06-20
 * @since 1.0
 */
public final class BarNotDaysException extends Exception {

    private final Integer tipo;
    private final String codigo;
    private final String mensaje;


    public BarNotDaysException(BarTemplate template) {
        super("No es dia de bloqueo para el template " + template.getName());
        this.tipo = 0;
        this.codigo = "DAY_NOT_BAR";
        this.mensaje = "No es dia de bloqueo para el template " + template.getName();

    }


    @Override
    public String toString() {
        return "ManagerException: { tipo=" + tipo + ", codigo=" + codigo + ", mensaje='" + mensaje + "' }";
    }

}


