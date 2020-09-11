package co.moviired.support.otp.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;

/*
 * Copyright @2018. MOVIIRED. Todos los derechos reservados.
 *
 * @author Cuadros, Cristian
 * @version 1, 2018-12-19
 * @since 1.0
 */

@Data
@ConfigurationProperties(prefix = "guarumo")
public class GuarumoProperties implements Serializable {

    // Connection params
    private String uri;
    private Integer connectTimeout;
    private Integer readTimeout;

    // Other parameters
    private String voice;
    private String message;
    private String timesToRetry;
    private String retryAfter;
    private String userParams;
    private String user;
    private String password;

}

