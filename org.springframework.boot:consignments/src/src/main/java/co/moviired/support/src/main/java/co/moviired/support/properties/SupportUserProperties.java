package co.moviired.support.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;

@Data
@ConfigurationProperties(prefix = "endpoints.support-user")
public class SupportUserProperties implements Serializable {

    private static final long serialVersionUID = -4351094487498311258L;

    private String url;
    private int connectionTimeout;
    private int readTimeout;

}

