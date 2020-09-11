package co.moviired.acquisition.config;

import co.moviired.acquisition.common.config.GlobalProperties;
import co.moviired.acquisition.common.config.StatusCodeConfig;
import co.moviired.acquisition.common.util.SignatureHelper;
import co.moviired.base.domain.enumeration.CryptoSpec;
import co.moviired.base.helper.CryptoHelper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;

import static co.moviired.acquisition.common.util.ConstantsHelper.*;

/**
 * This class define beans for the component
 */
@Configuration
public class ComponentBeans {

    /**
     * This bean is created for cipher the header of authentication
     *
     * @param environment environment defined in yml
     * @return crypto helper
     */
    @Bean(CRYPTO_HELPER)
    public CryptoHelper cryptoHelper(Environment environment) {
        return new CryptoHelper(StandardCharsets.UTF_8, CryptoSpec.AES_CBC,
                environment.getRequiredProperty(CRYPTO_KEY),
                environment.getRequiredProperty(CRYPTO_INIT_VECTOR));
    }

    /**
     * This bean is created for cipher the signatures
     *
     * @return signature helper
     */
    @Bean(name = SIGNATURE_HELPER)
    public SignatureHelper signatureHelper(@NotNull GlobalProperties globalProperties, @NotNull StatusCodeConfig statusCodeConfig) throws IOException, NoSuchAlgorithmException {
        return new SignatureHelper(globalProperties.getSecret(), statusCodeConfig);
    }
}

