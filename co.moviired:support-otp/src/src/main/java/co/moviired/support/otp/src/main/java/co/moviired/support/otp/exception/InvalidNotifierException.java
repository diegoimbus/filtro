package co.moviired.support.otp.exception;

public class InvalidNotifierException extends Exception {
    public InvalidNotifierException() {
        super();
    }

    public InvalidNotifierException(String message) {
        super(message);
    }

    public InvalidNotifierException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidNotifierException(Throwable cause) {
        super(cause);
    }

    public InvalidNotifierException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

