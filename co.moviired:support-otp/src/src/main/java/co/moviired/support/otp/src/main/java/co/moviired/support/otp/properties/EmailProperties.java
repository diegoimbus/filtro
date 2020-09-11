package co.moviired.support.otp.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;

/*
 * Copyright @2019. MOVIIRED. Todos los derechos reservados.
 *
 * @author RIVAS, Ronel
 * @version 1, 2019-06-27
 * @since 1.0
 */
@Data
@ConfigurationProperties(prefix = "email")
public class EmailProperties implements Serializable {

    private String url;
    private String pathMovii;
    private String pathMoviired;
    private Integer timeoutConnect;
    private Integer timeoutRead;
}

