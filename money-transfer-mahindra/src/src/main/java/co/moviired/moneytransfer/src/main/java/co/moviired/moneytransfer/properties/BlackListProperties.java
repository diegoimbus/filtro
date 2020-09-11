package co.moviired.moneytransfer.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;

@Data
@ConfigurationProperties(prefix = "client.blacklist")
public class BlackListProperties implements Serializable {

    private String url;
    private int connectionTimeout;
    private int readTimeout;

}

