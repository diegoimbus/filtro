package co.moviired.transaction.conf.client;

import co.moviired.base.domain.enumeration.CryptoSpec;
import co.moviired.base.helper.CryptoHelper;
import co.moviired.connector.connector.ReactiveConnector;
import co.moviired.transaction.properties.MahindraProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.nio.charset.StandardCharsets;


@Data
@AllArgsConstructor
@Configuration
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

    @Bean("cryptoHelper")
    public CryptoHelper cryptoHelper(Environment environment) {
        return new CryptoHelper(StandardCharsets.UTF_8, CryptoSpec.AES_CBC,
                environment.getRequiredProperty("crypt.key"),
                environment.getRequiredProperty("crypt.initializationVector"));
    }
}

