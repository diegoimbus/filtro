package co.moviired.support.endpoint.util.exceptions;

public class DaoBaseException extends GenericException {
    private static final long serialVersionUID = 1L;

    public DaoBaseException(String code, String description, String... args) {
        super(code, description, args);
    }

    public DaoBaseException(String code, String description, Throwable cause) {
        super(code, description, cause);
    }

    public DaoBaseException(String description) {
        super(CodeErrorEnum.DAOERROR.getCode(), description);
    }

    public DaoBaseException(String code, Throwable cause) {
        super(code, cause);
    }

    public DaoBaseException(Throwable cause) {
        super(CodeErrorEnum.DAOERROR.getCode(), cause);
    }

    public DaoBaseException() {
        super(CodeErrorEnum.DAOERROR);
    }

    public DaoBaseException(CodeErrorEnum codeError, String description) {
        super(codeError.getCode(), codeError.getDescription() + description);
    }

    public DaoBaseException(CodeErrorEnum code, String description, Throwable cause) {
        super(code.getCode(), code.getDescription() + description, cause);
    }

    public DaoBaseException(CodeErrorEnum code, Throwable cause) {
        super(code, cause);
    }
}

