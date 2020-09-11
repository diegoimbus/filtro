package co.moviired.gateway.conf;

import co.moviired.base.domain.enumeration.CryptoSpec;
import co.moviired.base.helper.CryptoHelper;
import co.moviired.connector.connector.ReactiveConnector;
import co.moviired.gateway.properties.AuthenticationProperties;
import co.moviired.gateway.properties.SupportProfilesProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.nio.charset.StandardCharsets;

@Component
public final class ClientsConfig {

    @Bean("stringRedisTemplate")
    StringRedisTemplate stringRedisTemplate(RedisConnectionFactory connectionFactory) {
        return new StringRedisTemplate(connectionFactory);
    }

    @Bean("cryptoHelper")
    public CryptoHelper cryptoHelper(Environment environment) {
        return new CryptoHelper(
                StandardCharsets.UTF_8,
                CryptoSpec.AES_CBC,
                environment.getRequiredProperty("crypt.key"),
                environment.getRequiredProperty("crypt.initializationVector"));
    }

    @Bean(name = "authenticationClient")
    public ReactiveConnector authenticationClient(@NotNull AuthenticationProperties auth) {
        return new ReactiveConnector("authenticationClient", auth.getUrl(), auth.getTimeoutConnection(), auth.getTimeoutRead());
    }

    @Bean(name = "profilesClient")
    public ReactiveConnector profilesClient(@NotNull SupportProfilesProperties profiles) {
        return new ReactiveConnector("profilesClient", profiles.getUrl(), profiles.getTimeoutConnection(), profiles.getTimeoutRead());
    }

}

