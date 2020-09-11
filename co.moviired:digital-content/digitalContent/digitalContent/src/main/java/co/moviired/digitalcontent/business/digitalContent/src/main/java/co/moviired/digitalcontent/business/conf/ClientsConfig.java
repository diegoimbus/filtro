package co.moviired.digitalcontent.business.conf;

import co.moviired.base.domain.enumeration.CryptoSpec;
import co.moviired.base.helper.CryptoHelper;
import co.moviired.connector.connector.ReactiveConnector;
import co.moviired.digitalcontent.business.properties.GlobalProperties;
import co.moviired.digitalcontent.business.properties.IntegratorProperties;
import co.moviired.digitalcontent.business.properties.MahindraProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.validation.constraints.NotNull;
import java.nio.charset.StandardCharsets;

@Configuration
public class ClientsConfig {


    // Clientes: MAHINDRA
    @Bean(name = "mhTransactionalClient")
    public ReactiveConnector mhTransactionalClient(@NotNull MahindraProperties mhp) {
        return new ReactiveConnector(mhp.getUrlTransactional(), mhp.getConnectionTimeout(), mhp.getReadTimeout());
    }

    // Clientes: Integrator
    @Bean(name = "integratorClient")
    public ReactiveConnector integratorClient(@NotNull IntegratorProperties icomm) {
        return new ReactiveConnector(icomm.getUrlTransactional(), icomm.getConnectionTimeout(), icomm.getReadTimeout());
    }

    // Clientes: MAHINDRA
    @Bean(name = "mhTransactionalClientRest")
    public ReactiveConnector mhTransactionalClientRest(@NotNull MahindraProperties mhp) {
        return new ReactiveConnector(mhp.getUrlTransactional(), mhp.getConnectionTimeout(), mhp.getReadTimeout());
    }

    // Clientes: email
    @Bean(name = "emailClient")
    public ReactiveConnector emailClient(@NotNull GlobalProperties global) {
        return new ReactiveConnector(global.getUrlMail(), 300000, 40000);
    }

    @Bean
    public CryptoHelper cryptoHelper(Environment environment) {
        return new CryptoHelper(StandardCharsets.UTF_8, CryptoSpec.AES_CBC,
                environment.getRequiredProperty("crypt.key"),
                environment.getRequiredProperty("crypt.initializationVector"));
    }

}

