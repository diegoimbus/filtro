package co.moviired.gateway.properties;
/*
 * Copyright @2019. Movii, SAS. Todos los derechos reservados.
 *
 * @author Bejarano, Cindy
 * @version 1, 2019
 * @since 1.0
 */

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;

@Data
@ConfigurationProperties(value = "services.authentication")
public class AuthenticationProperties implements Serializable {

    private static final long serialVersionUID = -4351094487498311258L;

    private String url;
    private String client;
    private String password;
    private Integer timeoutConnection;
    private Integer timeoutRead;


}

