package co.moviired.support.conf;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.io.Serializable;

@Data
@Configuration
public class SupportAutenticationProperties implements Serializable {

    private static final long serialVersionUID = -4351094487498311258L;

    // SERVICE WEB

    @Value("${properties.supportAuthentication.url}")
    private String url;

    @Value("${properties.supportAuthentication.timeout.connection}")
    private int connectionTimeout;

    @Value("${properties.supportAuthentication.timeout.read}")
    private int readTimeout;

    @Bean
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }

}

