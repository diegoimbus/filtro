package co.moviired.support.properties;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;


@Data
@ConfigurationProperties(prefix = "endpoints.consult-balance")
public class ConsultBalanceProperties implements Serializable {
    private static final long serialVersionUID = 1905122041950251207L;
    private String url;
    private int connectionTimeout;
    private int readTimeout;


}

