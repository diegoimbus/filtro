package co.moviired.support.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;

import static co.moviired.support.util.ConstantsHelper.PREFIX_SERVICE_MANAGER;

@Data
@ConfigurationProperties(prefix = PREFIX_SERVICE_MANAGER)
public class ServiceManagerProperties implements Serializable {
    private String url;
    private int connectionTimeout;
    private int readTimeout;

    private String urlPathCrudEmail;
}

