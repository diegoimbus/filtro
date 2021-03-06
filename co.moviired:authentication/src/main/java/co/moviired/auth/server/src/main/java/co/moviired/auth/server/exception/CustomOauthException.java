package co.moviired.auth.server.exception;

import co.moviired.auth.server.security.config.CustomOauthExceptionSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;

@JsonSerialize(using = CustomOauthExceptionSerializer.class)
public class CustomOauthException extends OAuth2Exception {
    public CustomOauthException(String msg) {
        super(msg);
    }

    public CustomOauthException(String msg, Throwable t) {
        super(msg, t);
    }
}

