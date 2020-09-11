package co.moviired.transpiler.conf;

import co.moviired.connector.connector.ReactiveConnector;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClientConfig {
    // Clientes: MAHINDRA
    @Bean(name = "mhTransactionalClient")
    public ReactiveConnector mhTransactionalClient(MahindraProperties mhp) {
        return new ReactiveConnector("mhTransactionalClient", mhp.getUrlTransactional(), mhp.getConnectionTimeout(), mhp.getReadTimeout());
    }

    @Bean(name = "validateBillPaymentByReferenceClient")
    public ReactiveConnector validateBillPaymentByReferenceClient(GeTraxProperties gtp) {
        return new ReactiveConnector("validateBillPaymentByReferenceClient", gtp.getUrlTransactional() + gtp.getUrlValidateBillPaymentByReference(), gtp.getConnectionTimeout(), gtp.getReadTimeout());
    }

    @Bean(name = "validateBillPaymentByEANClient")
    public ReactiveConnector validateBillPaymentByEANClient(GeTraxProperties gtp) {
        return new ReactiveConnector("validateBillPaymentByEANClient", gtp.getUrlTransactional() + gtp.getValidateBillPaymentByEANCode(), gtp.getConnectionTimeout(), gtp.getReadTimeout());
    }

    @Bean(name = "digitalContentClientActivate")
    public ReactiveConnector digitalContentClientActivate(DigitalContentProperties gtp) {
        return new ReactiveConnector("digitalContentClientActivate", gtp.getUrlActivate(), gtp.getConnectionTimeout(), gtp.getReadTimeout());
    }

    @Bean(name = "digitalContentClientInactivate")
    public ReactiveConnector digitalContentClientInactivate(DigitalContentProperties gtp) {
        return new ReactiveConnector("digitalContentClientInactivate", gtp.getUrlInactivate(), gtp.getConnectionTimeout(), gtp.getReadTimeout());
    }

    @Bean(name = "digitalContentClientPinesSales")
    public ReactiveConnector digitalContentClientPinesSales(DigitalContentProperties gtp) {
        return new ReactiveConnector("digitalContentClientPinesSales", gtp.getUrlPinesSale(), gtp.getConnectionTimeout(), gtp.getReadTimeout());
    }

    @Bean(name = "digitalContentClientPinesInactivate")
    public ReactiveConnector digitalContentClientPinesInactivate(DigitalContentProperties gtp) {
        return new ReactiveConnector("digitalContentClientPinesInactivate", gtp.getUrlPinesInactivate(), gtp.getConnectionTimeout(), gtp.getReadTimeout());
    }
}

