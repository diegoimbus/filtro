package co.moviired.register.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;

import static co.moviired.register.helper.ConstantsHelper.REGISTRADURIA_PREFIX;

@Data
@ConfigurationProperties(prefix = REGISTRADURIA_PREFIX)
public final class RegistraduriaProperties implements Serializable {

    private String url;
    private String forced;
    private Integer timeoutConnect;
    private Integer timeoutRead;
}

