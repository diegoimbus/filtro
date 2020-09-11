package co.movii.auth.server.exception;

public class LoginNoValidException extends AuthException {

    public LoginNoValidException() {
        super();
    }

    public LoginNoValidException(String message) {
        super(message);
    }
}

