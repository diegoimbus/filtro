package co.moviired.transaction.properties;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Data
@Component
public class GlobalParameters implements Serializable {

    private static final long serialVersionUID = -3395163916611319213L;

    // Versión de la aplicación
    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${spring.application.version}")
    private String applicationVersion;

    // Puertos de comunicación
    @Value("${server.port}")
    private int restPort;

    @Value("${properties.date_filter_value_allowed}")
    private Long dateFilterValueAllowed;

    @Value("${properties.regex-ignore-name-transfer}")
    private String regexIgnoreNameTranfer;

}
