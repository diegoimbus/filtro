package co.moviired.support.conf.database;

import co.moviired.base.domain.enumeration.CryptoSpec;
import co.moviired.base.helper.CryptoHelper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.validation.constraints.NotNull;
import java.nio.charset.StandardCharsets;

@Configuration
public class BeansConfig {

    @Bean("cryptoHelperAuthorization")
    public CryptoHelper cryptoHelperAuthorization(@NotNull Environment environment) {
        return new CryptoHelper(StandardCharsets.UTF_8, CryptoSpec.AES_CBC,
                environment.getRequiredProperty("crypt.key"),
                environment.getRequiredProperty("crypt.initializationVector"));
    }

}

