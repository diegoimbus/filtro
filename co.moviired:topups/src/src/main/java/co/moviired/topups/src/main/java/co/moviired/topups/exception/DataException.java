package co.moviired.topups.exception;

import co.moviired.topups.model.enums.ErrorType;

public class DataException extends ServiceException {

    private static final long serialVersionUID = -8491299051598640782L;

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

