package co.moviired.mahindrafacade.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@ConfigurationProperties(prefix = "properties")
@Configuration
@Component
@Data
public class CommandProperties {

    private List<String> commands;
    private Map<String, String> response;
}

