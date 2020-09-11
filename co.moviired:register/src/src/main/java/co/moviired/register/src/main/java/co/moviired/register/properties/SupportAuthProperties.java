package co.moviired.register.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;

/*
 * Copyright @2019. MOVIIRED. Todos los derechos reservados.
 *
 * @author JAP, SBD
 * @version 1, 2019-08-30
 * @since 2.0
 */

@Data
@ConfigurationProperties(prefix = "providers.support-auth")
public final class SupportAuthProperties implements Serializable {

    private boolean enableValidatePIN;

    // CRYPT
    private String secretKey;
    private String initVector;

    // REST CLIENT
    private String url;
    private Integer timeoutTransaction;
    private Integer timeoutConnection;

}

