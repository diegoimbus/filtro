package co.moviired.register.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;

import static co.moviired.register.helper.ConstantsHelper.CML_CONFIG_PREFIX;

@Data
@ConfigurationProperties(prefix = CML_CONFIG_PREFIX)
public final class CmlProperties implements Serializable {

    private String url;
    private Integer timeoutConnect;
    private Integer timeoutRead;

    private String pathValidateReferral;
}

