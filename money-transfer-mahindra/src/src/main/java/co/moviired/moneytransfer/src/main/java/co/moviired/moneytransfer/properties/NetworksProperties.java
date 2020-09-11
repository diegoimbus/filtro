package co.moviired.moneytransfer.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.List;

@ConfigurationProperties(prefix = "properties.parameters")
@Configuration
@Component
@Data
public class NetworksProperties {

    //Networks
    private List<Network> networks;

    @Data
    public static class Network {
        private String user;
        private String pass;
    }

}

