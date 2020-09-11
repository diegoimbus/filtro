package co.moviired.supportp2pvalidatortransaction.config;

import co.moviired.base.domain.enumeration.CryptoSpec;
import co.moviired.base.helper.CryptoHelper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.nio.charset.StandardCharsets;

import static co.moviired.supportp2pvalidatortransaction.common.util.Constants.*;

@Configuration
public class ComponentBeans {

    @Bean(CRYPTO_HELPER)
    public CryptoHelper cryptoHelper(Environment environment) {
        return new CryptoHelper(StandardCharsets.UTF_8, CryptoSpec.AES_CBC,
                environment.getRequiredProperty(CRYPTO_KEY),
                environment.getRequiredProperty(CRYPTO_INIT_VECTOR));
    }
}

