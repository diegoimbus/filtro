package co.moviired.moneytransfer.config.client;

import co.moviired.base.domain.enumeration.CryptoSpec;
import co.moviired.base.helper.CryptoHelper;
import co.moviired.connector.connector.ReactiveConnector;
import co.moviired.moneytransfer.properties.*;
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
    private final RegistraduriaProperties registraduriaProperties;
    private final SmsProperties smsProperties;
    private final BlackListProperties blackListProperties;
    private final SupportAuthenticationProperties supportAuthenticationProperties;

    // MAHINDRA
    @Bean(name = "mahindraClient")
    public ReactiveConnector mahindraClient() {
        return new ReactiveConnector(
                this.mahindraProperties.getUrl(),
                this.mahindraProperties.getConnectionTimeout(),
                this.mahindraProperties.getReadTimeout()
        );
    }

    //Registraduria
    @Bean(name = "registraduriaClient")
    public ReactiveConnector registraduriaClient() {
        return new ReactiveConnector(
                this.registraduriaProperties.getUrl(),
                this.registraduriaProperties.getConnectionTimeout(),
                this.registraduriaProperties.getReadTimeout()
        );
    }

    // SUPPORT-SMS
    @Bean(name = "supportSmsClient")
    public ReactiveConnector supportSmsClient() {
        return new ReactiveConnector(
                this.smsProperties.getUrl(),
                this.smsProperties.getConnectionTimeout(),
                this.smsProperties.getReadTimeout()
        );
    }

    // Black List
    @Bean(name = "blackListClient")
    public ReactiveConnector blackListClient() {
        return new ReactiveConnector(
                this.blackListProperties.getUrl(),
                this.blackListProperties.getConnectionTimeout(),
                this.blackListProperties.getReadTimeout()
        );
    }

    // Support Authentication
    @Bean(name = "supportAuthenticationClient")
    public ReactiveConnector supportAuthenticationClient() {
        return new ReactiveConnector(
                this.supportAuthenticationProperties.getUrl(),
                this.supportAuthenticationProperties.getConnectionTimeout(),
                this.supportAuthenticationProperties.getReadTimeout()
        );
    }

    @Bean("cryptoHelper")
    public CryptoHelper cryptoHelper(Environment environment) {
        return new CryptoHelper(StandardCharsets.UTF_8, CryptoSpec.AES_CBC,
                environment.getRequiredProperty("crypt.key"),
                environment.getRequiredProperty("crypt.initializationVector"));
    }

}

