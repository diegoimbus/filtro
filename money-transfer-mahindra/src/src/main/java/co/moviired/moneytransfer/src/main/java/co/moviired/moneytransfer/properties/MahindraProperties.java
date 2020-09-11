package co.moviired.moneytransfer.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;

@Data
@ConfigurationProperties(prefix = "client.mahindra")
public class MahindraProperties implements Serializable {

    private String url;
    private int connectionTimeout;
    private int readTimeout;

}

