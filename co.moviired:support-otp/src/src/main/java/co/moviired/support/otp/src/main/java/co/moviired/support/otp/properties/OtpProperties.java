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
@ConfigurationProperties(prefix = "otp")
public class OtpProperties implements Serializable {

    private String secret;
    private Boolean defaultAlpha;
    private Integer defaultLength;
    private Integer defaultExpirationLapse;
    private Integer expirationJobRate;

    private String moviiOrigin;
    private Integer moviiValidateAttemps;
    private Integer moviiGenerateByDay;
    private String moviiredOrigin;
    private Integer moviiredValidateAttemps;
    private Integer moviiredGenerateByDay;

}

