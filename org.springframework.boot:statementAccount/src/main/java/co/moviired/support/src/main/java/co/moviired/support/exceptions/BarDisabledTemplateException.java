package co.moviired.support.exceptions;

/*
 * Copyright @2018. MOVIIRED. Todos los derechos reservados.
 *
 * @author Cuadros Cerpa, Cristian Alfonso
 * @version 1, 2018-06-20
 * @since 1.0
 */
public final class BarDisabledTemplateException extends Exception {

    private final Integer tipo;
    private final String codigo;
    private final String mensaje;


    public BarDisabledTemplateException() {
        super("Template deshabilitado");
        this.tipo = 0;
        this.codigo = "TEMPLATE_DISABLED";
        this.mensaje = "Template deshabilitado";

    }


    @Override
    public String toString() {
        return "ManagerException: { tipo=" + tipo + ", codigo=" + codigo + ", mensaje='" + mensaje + "' }";
    }

}


