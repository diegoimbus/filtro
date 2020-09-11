package co.moviired.moneytransfer.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;

@Data
@ConfigurationProperties(prefix = "client.registraduria")
public class RegistraduriaProperties implements Serializable {

    private String url;
    private int connectionTimeout;
    private int readTimeout;
    private Integer forced;
}

