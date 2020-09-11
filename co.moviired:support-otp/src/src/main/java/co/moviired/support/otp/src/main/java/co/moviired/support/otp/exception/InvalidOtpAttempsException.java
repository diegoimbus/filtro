package co.moviired.support.otp.exception;

public class InvalidOtpAttempsException extends Exception {
    public InvalidOtpAttempsException() {
        super();
    }

    public InvalidOtpAttempsException(String message) {
        super(message);
    }

    public InvalidOtpAttempsException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidOtpAttempsException(Throwable cause) {
        super(cause);
    }

    public InvalidOtpAttempsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

