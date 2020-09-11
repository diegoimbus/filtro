package co.moviired.auth.server.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;

@Data
@ConfigurationProperties(prefix = "providers.support-user")
public class SupportUserProperties implements Serializable {

    private static final long serialVersionUID = -4351094487498311258L;

    // SERVICE WEB
    private String url;
    private int connectionTimeout;
    private int readTimeout;

    // PATHS
    private String pathChangePassword;
    private String pathAutenticacion;
    private String pathResetPassword;
    private String pathGenerateOTP;
    private String pathGetUser;

}

