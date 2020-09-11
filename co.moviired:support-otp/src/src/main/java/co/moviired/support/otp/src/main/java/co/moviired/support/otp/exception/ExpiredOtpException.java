package co.moviired.support.otp.exception;

public class ExpiredOtpException extends Exception {
    public ExpiredOtpException() {
        super();
    }

    public ExpiredOtpException(String message) {
        super(message);
    }

    public ExpiredOtpException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExpiredOtpException(Throwable cause) {
        super(cause);
    }

    public ExpiredOtpException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

