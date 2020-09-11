package co.moviired.auth.server.properties;

import co.moviired.auth.server.helper.ConstantsHelper;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = ConstantsHelper.REGISTER_PROPERTIES_PREFIX)
public class RegisterProperties {

    private String url;
    private Integer timeoutConnect;
    private Integer timeoutRead;

}

