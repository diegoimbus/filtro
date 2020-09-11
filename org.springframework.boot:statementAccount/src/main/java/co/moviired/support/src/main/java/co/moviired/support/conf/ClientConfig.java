package co.moviired.support.conf;

import co.moviired.base.domain.enumeration.CryptoSpec;
import co.moviired.base.helper.CryptoHelper;
import co.moviired.connector.connector.ReactiveConnector;
import co.moviired.connector.connector.RestConnector;
import co.moviired.support.helper.SignatureHelper;
import co.moviired.support.properties.EmailGeneratorProperties;
import co.moviired.support.properties.GlobalProperties;
import co.moviired.support.properties.MahindraProperties;
import co.moviired.support.properties.ServiceManagerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;

import static co.moviired.support.util.ConstantsHelper.*;

@Component
public class ClientConfig {

    @Bean(name = "cryptoHelperAuthorization")
    public CryptoHelper cryptoHelperAuthorization(Environment environment) {
        return new CryptoHelper(
                StandardCharsets.UTF_8,
                CryptoSpec.AES_CBC,
                environment.getRequiredProperty("crypt.key"),
                environment.getRequiredProperty("crypt.initializationVector"));
    }

    @Bean(name = SIGNATURE_HELPER)
    public SignatureHelper signatureHelper(@NotNull GlobalProperties globalProperties, @NotNull StatusCodeConfig statusCodeConfig) throws IOException, NoSuchAlgorithmException {
        return new SignatureHelper(globalProperties.getSecret(), statusCodeConfig);
    }

    // MAHINDRA
    @Bean(name = MAHINDRA_REST)
    public RestConnector mahindraClientRest(MahindraProperties mahindraProperties) {
        return new RestConnector(
                mahindraProperties.getUrl(),
                mahindraProperties.getConnectionTimeout(),
                mahindraProperties.getReadTimeout()
        );
    }

    @Bean(name = MAHINDRA_API)
    public ReactiveConnector pmahindraClient(MahindraProperties mahindraProperties) {
        return new ReactiveConnector(
                MAHINDRA_API,
                mahindraProperties.getUrl(),
                mahindraProperties.getConnectionTimeout(),
                mahindraProperties.getReadTimeout()
        );
    }

    @Bean(name = SERVICE_MANAGER_API)
    public ReactiveConnector serviceManagerConnector(ServiceManagerProperties serviceManagerProperties) {
        return new ReactiveConnector(
                SERVICE_MANAGER_API,
                serviceManagerProperties.getUrl(),
                serviceManagerProperties.getConnectionTimeout(),
                serviceManagerProperties.getReadTimeout()
        );
    }

    @Bean(name = EMAIL_GENERATOR_API)
    public ReactiveConnector emailGeneratorConnector(EmailGeneratorProperties emailGeneratorProperties) {
        return new ReactiveConnector(
                EMAIL_GENERATOR_API,
                emailGeneratorProperties.getUrl(),
                emailGeneratorProperties.getConnectionTimeout(),
                emailGeneratorProperties.getReadTimeout()
        );
    }

}

