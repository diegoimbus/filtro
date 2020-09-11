package co.moviired.register.exceptions;

/*
 * Copyright @2018. MOVIIRED. Todos los derechos reservados.
 *
 * @author Cuadros Cerpa, Cristian Alfonso
 * @version 1, 2018-06-20
 * @since 1.0
 */
public class ManagerException extends Exception {

    private final Integer tipo;
    private final String codigo;
    private final String mensaje;
    private final String msisdn;
    private final String msisdn2;


    public ManagerException(Integer pTipo, String pCodigo, String pMensaje) {
        super(pMensaje);
        this.tipo = pTipo;
        this.codigo = pCodigo;
        this.mensaje = pMensaje;
        this.msisdn = null;
        this.msisdn2 = null;

    }

    public ManagerException(Integer pTipo, String pCodigo, String pMensaje, String pMsisdn, String pMsisdn2) {
        super(pMensaje);
        this.tipo = pTipo;
        this.codigo = pCodigo;
        this.mensaje = pMensaje;
        this.msisdn = pMsisdn;
        this.msisdn2 = pMsisdn2;

    }

    public Integer getTipo() {
        return tipo;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getMensaje() {
        return mensaje;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public String getMsisdn2() {
        return msisdn2;
    }

    @Override
    public String toString() {
        return "ManagerException: { tipo=" + tipo + ", codigo=" + codigo + ", mensaje='" + mensaje + "' }";
    }

}


