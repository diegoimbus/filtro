package co.moviired.topups.exception;

import co.moviired.topups.model.enums.ErrorType;

public class ServiceException extends Exception {
    private static final long serialVersionUID = -3605287404565519450L;
    protected final ErrorType tipo;
    protected final String codigo;
    protected final String mensaje;


    public ServiceException(ErrorType tipo, String codigo, String mensaje) {
        super(mensaje);

        this.tipo = tipo;
        this.codigo = codigo;
        this.mensaje = mensaje;

    }

    public ErrorType getTipo() {
        return tipo;
    }


    public String getCodigo() {
        return codigo;
    }


    public String getMensaje() {
        return mensaje;
    }


    @Override
    public String toString() {
        return "ServiceException: { tipo=" + tipo + ", codigo=" + codigo + ", mensaje='" + mensaje + "' }";
    }
}

