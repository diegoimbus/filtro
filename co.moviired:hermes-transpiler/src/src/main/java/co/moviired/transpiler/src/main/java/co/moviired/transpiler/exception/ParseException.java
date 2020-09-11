package co.moviired.transpiler.exception;

public class ParseException extends Exception {

    private static final long serialVersionUID = 2323292208990801207L;

    public ParseException(String message) {
        super(message);
    }

    public ParseException(Throwable cause) {
        super(cause);
    }
}

