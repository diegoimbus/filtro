package co.moviired.digitalcontent.business.properties;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.Serializable;

@Data
@Configuration
public class IntegratorProperties implements Serializable {

    private static final long serialVersionUID = -4351094487498311258L;

    // SERVICE WEB

    @Value("${clients.integrator.url}")
    private String urlTransactional;

    @Value("${clients.integrator.timeout.connection}")
    private int connectionTimeout;

    @Value("${clients.integrator.timeout.read}")
    private int readTimeout;

}
