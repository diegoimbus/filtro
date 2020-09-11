package co.moviired.support.endpoint.util.generics;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Map;

@Data
@Component
@ConfigurationProperties(prefix = "errors-message")
public class MessageProperties implements Serializable {
    private static final long serialVersionUID = 6092780672661878995L;

    private Map<String, String> errors;

}

