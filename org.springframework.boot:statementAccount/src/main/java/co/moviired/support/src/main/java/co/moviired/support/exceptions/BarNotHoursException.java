package co.moviired.support.exceptions;

import co.moviired.support.domain.entity.account.BarTemplate;

/*
 * Copyright @2018. MOVIIRED. Todos los derechos reservados.
 *
 * @author Cuadros Cerpa, Cristian Alfonso
 * @version 1, 2018-06-20
 * @since 1.0
 */
public final class BarNotHoursException extends Exception {

    private final Integer tipo;
    private final String codigo;
    private final String mensaje;


    public BarNotHoursException(BarTemplate template) {
        super("Es dia de bloqueo para el template " + template.getName() + " Pero no la hora");
        this.tipo = 0;
        this.codigo = "HOURS_NOT_BAR";
        this.mensaje = "Es dia de bloqueo para el template " + template.getName() + " Pero no la hora";

    }


    @Override
    public String toString() {
        return "ManagerException: { tipo=" + tipo + ", codigo=" + codigo + ", mensaje='" + mensaje + "' }";
    }

}


