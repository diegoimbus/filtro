package co.moviired.business.conf;

import co.moviired.base.domain.enumeration.CryptoSpec;
import co.moviired.base.helper.CryptoHelper;
import co.moviired.business.properties.BankingProperties;
import co.moviired.business.properties.IntegratorProperties;
import co.moviired.business.properties.MahindraProperties;
import co.moviired.connector.connector.ReactiveConnector;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.nio.charset.StandardCharsets;

@Configuration
public class ClientsConfig {

    // Clientes: MAHINDRA
    @Bean(name = "mhTransactionalClient")
    public ReactiveConnector mhTransactionalClient(MahindraProperties mhp) {
        return new ReactiveConnector("mhTransactionalClientPool", mhp.getUrlTransactional(), mhp.getConnectionTimeout(), mhp.getReadTimeout());
    }

    // Cliente: Microservicio Banking SWITCH

    @Bean(name = "queryBankingSwitchClient")
    public ReactiveConnector queryBankingSwitchClient(BankingProperties gp) {
        return new ReactiveConnector("queryBankingSwitchClientPool", gp.getUrlBankingQuery(), gp.getConnectionTimeoutBankingQuery(), gp.getReadTimeoutBankingQuery());
    }

    @Bean(name = "cashOutBankingSwitchClient")
    public ReactiveConnector cashOutBankingSwitchClient(BankingProperties gp) {
        return new ReactiveConnector("cashOutBankingSwitchClientPool", gp.getUrlBankingCashOut(), gp.getConnectionTimeoutBankingCashOut(), gp.getReadTimeoutBankingCashOut());
    }

    // Cliente: Integrador
    @Bean(name = "queryByReferenceIntegratorClient")
    public ReactiveConnector queryByReferenceIntegratorClient(IntegratorProperties ip) {
        return new ReactiveConnector("queryByReferenceIntegratorClientPool", ip.getUrlIntegratorValidateByReference(), ip.getConnectionTimeoutIntegratorValidateByReference(), ip.getReadTimeoutIntegratorValidateByReference());
    }

    @Bean(name = "queryByEanCodeIntegratorClient")
    public ReactiveConnector queryByEanCodeIntegratorClient(IntegratorProperties ip) {
        return new ReactiveConnector("queryByEanCodeIntegratorClientPool", ip.getUrlIntegratorValidateByEANCode(), ip.getConnectionTimeoutIntegratorValidateByEANCode(), ip.getReadTimeoutIntegratorValidateByEANCode());
    }

    // Cifrado de peticiones
    @Bean
    public CryptoHelper cryptoHelper(Environment environment) {
        return new CryptoHelper(StandardCharsets.UTF_8, CryptoSpec.AES_CBC,
                environment.getRequiredProperty("crypt.key"),
                environment.getRequiredProperty("crypt.initializationVector"));
    }

}

