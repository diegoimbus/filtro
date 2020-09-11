package co.moviired.cardManager.conf;

import co.moviired.base.domain.enumeration.CryptoSpec;
import co.moviired.base.helper.CryptoHelper;
import co.moviired.cardManager.properties.MahindraProperties;
import co.moviired.connector.connector.ReactiveConnector;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.nio.charset.StandardCharsets;

@Configuration
public class ClientsConfig {

    // Cliente: MAHINDRA
    @Bean(name = "mhTransactionalClient")
    public ReactiveConnector mhTransactionalClientPool(MahindraProperties mhp){
        return new ReactiveConnector("mhTransactionalClientPool", mhp.getUrlTransactional(), mhp.getConnectionTimeout(), mhp.getReadTimeout());
    }

    // Cifrado de peticiones
    @Bean
    public CryptoHelper cryptoHelper(Environment environment) {
        return new CryptoHelper(StandardCharsets.UTF_8, CryptoSpec.AES_CBC,
                environment.getRequiredProperty("crypt.key"),
                environment.getRequiredProperty("crypt.initializationVector"));
    }
}

