package co.movii.auth.server.exception;

public class PasswordFormatInvalidException extends AuthException {
    public PasswordFormatInvalidException(String message) {
        super(message);
    }
}

