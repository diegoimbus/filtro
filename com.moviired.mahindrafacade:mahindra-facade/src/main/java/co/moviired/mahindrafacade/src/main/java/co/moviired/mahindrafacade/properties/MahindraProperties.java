package co.moviired.mahindrafacade.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;

@Data
@ConfigurationProperties(prefix = "client.mahindra")
public class MahindraProperties implements Serializable {

    private String connectorName;
    private String url;
    private int connectionTimeout;
    private int readTimeout;

}

