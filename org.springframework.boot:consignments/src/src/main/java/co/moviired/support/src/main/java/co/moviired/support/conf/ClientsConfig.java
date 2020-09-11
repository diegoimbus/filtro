package co.moviired.support.conf;

import co.moviired.connector.connector.ReactiveConnector;
import co.moviired.connector.connector.RestConnector;
import co.moviired.support.properties.MahindraProperties;
import co.moviired.support.properties.SupportUserProperties;
import lombok.Data;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import javax.validation.constraints.NotNull;

import static co.moviired.support.util.Constants.SUPPORT_USER_API;

@Data
@Configuration
public class ClientsConfig {

    // MAHINDRA
    @Bean(name = "mahindraClient")
    public RestConnector mahindraClient(@NotNull MahindraProperties mahindraProperties) {
        return new RestConnector(
                mahindraProperties.getUrl(),
                mahindraProperties.getConnectionTimeout(),
                mahindraProperties.getReadTimeout()
        );
    }

    @Bean(name = SUPPORT_USER_API)
    public ReactiveConnector supportUserClient(SupportUserProperties supportUserProperties) {
        return new ReactiveConnector(supportUserProperties.getUrl(), supportUserProperties.getConnectionTimeout(), supportUserProperties.getReadTimeout());
    }

}
