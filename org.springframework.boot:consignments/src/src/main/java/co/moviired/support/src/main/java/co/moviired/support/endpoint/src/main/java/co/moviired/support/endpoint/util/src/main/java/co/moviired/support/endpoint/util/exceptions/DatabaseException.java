package co.moviired.support.endpoint.util.exceptions;

public class DatabaseException extends GenericException {
    private static final long serialVersionUID = 1L;

    public DatabaseException(String code, String description, String... args) {
        super(code, description, args);
    }

    public DatabaseException(CodeErrorEnum codeError) {
        super(codeError);
    }

    public DatabaseException(CodeErrorEnum codeError, Throwable cause) {
        super(codeError, cause);
    }

    public DatabaseException(String code, String description, Throwable cause) {
        super(code, description, cause);
    }

    public DatabaseException(String description) {
        super(CodeErrorEnum.ERRORCHANNEL.getCode(), description);
    }

    public DatabaseException(String code, Throwable cause) {
        super(code, cause);
    }

    public DatabaseException(Throwable cause) {
        super(CodeErrorEnum.ERRORCHANNEL.getCode(), cause);
    }

    public DatabaseException(Throwable exception, CodeErrorEnum code, String... args) {
        super(exception, code, args);
    }

    public DatabaseException(CodeErrorEnum code, String... args) {
        super(code, args);
    }
}

