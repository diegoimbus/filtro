package co.moviired.support.exceptions;

/*
 * Copyright @2018. MOVIIRED. Todos los derechos reservados.
 *
 * @author Cuadros Cerpa, Cristian Alfonso
 * @version 1, 2018-06-20
 * @since 1.0
 */
public final class UnbarDisabledTemplateException extends Exception {

    private final Integer tipo;
    private final String codigo;
    private final String mensaje;


    public UnbarDisabledTemplateException() {
        super("Desbloqueo deshabilitado");
        this.tipo = 0;
        this.codigo = "UNBAR_DISABLED";
        this.mensaje = "Desbloqueo deshabilitado";

    }


    @Override
    public String toString() {
        return "ManagerException: { tipo=" + tipo + ", codigo=" + codigo + ", mensaje='" + mensaje + "' }";
    }

}


