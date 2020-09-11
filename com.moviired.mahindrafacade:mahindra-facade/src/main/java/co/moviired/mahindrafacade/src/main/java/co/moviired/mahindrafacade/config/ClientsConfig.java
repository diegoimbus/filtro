package co.moviired.mahindrafacade.config;

import co.moviired.connector.connector.ReactiveConnector;
import co.moviired.mahindrafacade.properties.MahindraProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@AllArgsConstructor
public class ClientsConfig {

    private final MahindraProperties mahindraProperties;

    // MAHINDRA
    @Bean(name = "mahindraClient")
    public ReactiveConnector mahindraClient() {
        return new ReactiveConnector(
                this.mahindraProperties.getConnectorName(),
                this.mahindraProperties.getUrl(),
                this.mahindraProperties.getConnectionTimeout(),
                this.mahindraProperties.getReadTimeout()
        );
    }

}

