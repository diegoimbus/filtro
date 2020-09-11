package co.moviired.auth.server.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;

@Data
@ConfigurationProperties(prefix = "providers.support-profile")
public class SupportProfileProperties implements Serializable {

    private static final long serialVersionUID = -4351094487498311258L;

    // SERVICE WEB
    private String url;
    private int connectionTimeout;
    private int readTimeout;

    // PATHS
    private String pathGetName;

}

