package co.moviired.support.endpoint.util.exceptions;

public class InvokeException extends GenericException {
    private static final long serialVersionUID = 1L;

    public InvokeException(String code, String description, String... args) {
        super(code, description, args);
    }

    public InvokeException(String code, String description, Throwable cause) {
        super(code, description, cause);
    }

    public InvokeException(CodeErrorEnum error, Throwable cause) {
        super(error, cause);
    }

    public InvokeException(CodeErrorEnum error) {
        super(error);
    }
}
