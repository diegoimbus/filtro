package co.movii.auth.server.exception;

public class ParseException extends AuthException {

    private static final long serialVersionUID = 2323292208990801207L;

    public ParseException(String message) {
        super(message);
    }

    public ParseException(String message, Throwable cause) {
        super(message, cause);
    }
}

