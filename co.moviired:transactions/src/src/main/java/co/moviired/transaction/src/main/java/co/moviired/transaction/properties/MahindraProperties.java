package co.moviired.transaction.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;

@Data
@ConfigurationProperties(prefix = "client.mahindra")
public class MahindraProperties implements Serializable {

    private static final long serialVersionUID = 6175037159783288477L;
    private String connectorName;
    private String url;
    private int connectionTimeout;
    private int readTimeout;

}

