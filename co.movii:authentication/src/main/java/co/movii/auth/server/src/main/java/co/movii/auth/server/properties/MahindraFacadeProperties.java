package co.movii.auth.server.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;

@Data
@ConfigurationProperties(prefix = "providers.mahindra-facade")
public class MahindraFacadeProperties implements Serializable {

    private static final long serialVersionUID = -7816241965226141075L;

    // SERVICE WEB
    private String url;
    private int connectionTimeout;
    private int readTimeout;

    // PATHS
    private String pathGetUser;

}

