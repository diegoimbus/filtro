package co.moviired.support.conf;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.Serializable;

@Data
@Configuration
public class MahindraProperties implements Serializable {

    private static final long serialVersionUID = -4351094487498311258L;

    // SERVICE WEB

    @Value("${properties.mahindra.url}")
    private String url;

    @Value("${properties.mahindra.timeout.connection}")
    private int connectionTimeout;

    @Value("${properties.mahindra.timeout.read}")
    private int readTimeout;

    @Value("${properties.mahindra.provider}")
    private String provider;

    @Value("${properties.mahindra.type}")
    private String type;

    @Value("${properties.mahindra.usertype}")
    private String usertype;

}

