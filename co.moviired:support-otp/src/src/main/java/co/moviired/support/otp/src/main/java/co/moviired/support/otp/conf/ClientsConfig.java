package co.moviired.support.otp.conf;

import co.moviired.base.domain.enumeration.CryptoSpec;
import co.moviired.base.helper.CryptoHelper;
import co.moviired.connector.connector.ReactiveConnector;
import co.moviired.support.otp.properties.EmailProperties;
import co.moviired.support.otp.properties.GuarumoProperties;
import co.moviired.support.otp.properties.SmsProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.nio.charset.StandardCharsets;

@Component
public class ClientsConfig {

    @Bean(name = "supportSMSClient")
    public ReactiveConnector supportSMSClient(@NotNull SmsProperties sms) {
        return new ReactiveConnector(sms.getUrl(), sms.getTimeoutConnect(), sms.getTimeoutRead());
    }

    @Bean(name = "supportEMAILClient")
    public ReactiveConnector supportEMAILClient(@NotNull EmailProperties email) {
        return new ReactiveConnector(email.getUrl(), email.getTimeoutConnect(), email.getTimeoutRead());
    }

    @Bean(name = "guarumoClient")
    public ReactiveConnector guarumoClient(@NotNull GuarumoProperties guarumo) {
        return new ReactiveConnector(guarumo.getUri(), guarumo.getConnectTimeout(), guarumo.getReadTimeout());
    }


    @Bean("cryptoHelper")
    public CryptoHelper cryptoHelper(Environment environment) {
        return new CryptoHelper(StandardCharsets.UTF_8, CryptoSpec.AES_CBC,
                environment.getRequiredProperty("crypt.key"),
                environment.getRequiredProperty("crypt.initializationVector"));
    }
}

