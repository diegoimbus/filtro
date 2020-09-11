package co.moviired.support.endpoint.util.generics;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Map;

@Data
@Component
@ConfigurationProperties(prefix = "ws-consignments")
public class SystemProperties implements Serializable {

    private static final long serialVersionUID = -3395163916611319213L;

    private Map<String, String> bogota;
    private Map<String, String> bancolombia;
    private Map<String, String> mahindra;

}
