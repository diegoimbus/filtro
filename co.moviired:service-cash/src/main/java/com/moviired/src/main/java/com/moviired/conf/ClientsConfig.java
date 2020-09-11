package com.moviired.conf;

import co.moviired.connector.connector.ReactiveConnector;
import com.moviired.properties.*;
import lombok.Data;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.validation.constraints.NotNull;

/*
 * Copyright @2018. MOVIIRED. Todos los derechos reservados.
 *
 * @author RIVAS, Ronel
 * @version 1, 2018-12-19
 * @since 1.0
 */

@Data
@Configuration
public class ClientsConfig {

    private final MahindraProperties mahindraProperties;
    private final SupportUserProperties supportUserProperties;
    private final SupportOTPProperties supportOtpProperties;
    private final SupportSmsProperties supportSmsProperties;
    private final ConsultBalanceProperties consultBalanceProperties;

    public ClientsConfig(@NotNull MahindraProperties pMahindraProperties,
                         @NotNull SupportUserProperties pSupportUserProperties,
                         @NotNull SupportOTPProperties pSupportOTPProperties,
                         @NotNull SupportSmsProperties pSupportSmsProperties,
                         @NotNull ConsultBalanceProperties pConsultBalanceProperties) {
        super();
        this.mahindraProperties = pMahindraProperties;
        this.supportUserProperties = pSupportUserProperties;
        this.supportOtpProperties = pSupportOTPProperties;
        this.supportSmsProperties = pSupportSmsProperties;
        this.consultBalanceProperties = pConsultBalanceProperties;
    }


    /**
     * metodo Bean Mahindra Client
     *
     * @param
     * @return ReactiveConnector
     */
    @Bean(name = "mahindraClient")
    public ReactiveConnector mahindraClient() {
        return new ReactiveConnector("mahindraClient",
                this.mahindraProperties.getUrl(),
                this.mahindraProperties.getConnectionTimeout(),
                this.mahindraProperties.getReadTimeout()
        );
    }

    /**
     * metodo Bean supportUserClient
     *
     * @param
     * @return ReactiveConnector
     */
    @Bean(name = "supportUserClient")
    public ReactiveConnector supportUserClient() {
        return new ReactiveConnector("supportUserClient",
                this.supportUserProperties.getUrl(),
                this.supportUserProperties.getConnectionTimeout(),
                this.supportUserProperties.getReadTimeout()
        );
    }

    /**
     * metodo Bean supportOtpClient
     *
     * @param
     * @return ReactiveConnector
     */
    @Bean(name = "supportOtpClient")
    public ReactiveConnector supportOtpClient() {
        return new ReactiveConnector("supportOtpClient",
                this.supportOtpProperties.getUrl(),
                this.supportOtpProperties.getConnectionTimeout(),
                this.supportOtpProperties.getReadTimeout()
        );
    }

    /**
     * metodo Bean supportSmsClient
     *
     * @param
     * @return ReactiveConnector
     */
    @Bean(name = "supportSmsClient")
    public ReactiveConnector supportSmsClient() {
        return new ReactiveConnector("supportSmsClient",
                this.supportSmsProperties.getUrl(),
                this.supportSmsProperties.getConnectionTimeout(),
                this.supportSmsProperties.getReadTimeout()
        );
    }

    /**
     * metodo Bean consultBalance
     *
     * @param
     * @return ReactiveConnector
     */
    @Bean(name = "consultBalance")
    public ReactiveConnector consultBalance() {
        return new ReactiveConnector("consultBalance",
                this.consultBalanceProperties.getUrl(),
                this.consultBalanceProperties.getConnectionTimeout(),
                this.consultBalanceProperties.getReadTimeout()
        );
    }

}

