package co.movii.auth.server.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import static co.movii.auth.server.helper.ConstantsHelper.REGISTER_PROPERTIES_PREFIX;

@Data
@ConfigurationProperties(prefix = REGISTER_PROPERTIES_PREFIX)
public class RegisterProperties {

    private String url;
    private Integer timeoutConnect;
    private Integer timeoutRead;

    private String pathInactivatePendingChangePassword;
}

