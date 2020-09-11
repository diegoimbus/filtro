package co.movii.auth.server.conf;

import co.movii.auth.server.helper.GoogleRecaptcha;
import co.movii.auth.server.properties.*;
import co.moviired.base.domain.enumeration.CryptoSpec;
import co.moviired.base.helper.CryptoHelper;
import co.moviired.connector.connector.ReactiveConnector;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

import static co.movii.auth.server.helper.ConstantsHelper.REGISTER_API;

@Component
public final class ClientsConfig {


    // Clientes: MAHINDRA FACADE
    @Bean(name = "mhFacadeClient")
    public ReactiveConnector mhFacadeClient(MahindraFacadeProperties mhFacadeProperties) {
        return new ReactiveConnector("mhFacadeClient", mhFacadeProperties.getUrl(), mhFacadeProperties.getConnectionTimeout(), mhFacadeProperties.getReadTimeout());
    }

    // Clientes: MAHINDRA
    @Bean(name = "mhTransactionalClient")
    public ReactiveConnector mhTransactionalClient(MahindraProperties mhp) {
        return new ReactiveConnector("mhTransactionalClient", mhp.getUrl(), mhp.getConnectionTimeout(), mhp.getReadTimeout());
    }

    // Clientes: SUPPORT PROFILES
    @Bean(name = "profileClient")
    public ReactiveConnector profileClient(SupportProfileProperties profileProperties) {
        return new ReactiveConnector("profileClient", profileProperties.getUrl() + profileProperties.getPathGetName(), profileProperties.getConnectionTimeout(), profileProperties.getReadTimeout());
    }

    // Clientes: SUPPORT USERS
    @Bean(name = "userClientLogin")
    public ReactiveConnector userClientLogin(SupportUserProperties supportUserProperties) {
        return new ReactiveConnector("userClientLogin", supportUserProperties.getUrl() + supportUserProperties.getPathAutenticacion(), supportUserProperties.getConnectionTimeout(), supportUserProperties.getReadTimeout());
    }

    @Bean(name = "userClientGetUser")
    public  ReactiveConnector userClientGetUser(SupportUserProperties supportUserProperties) {
        return new ReactiveConnector("userClientGetUser", supportUserProperties.getUrl() + supportUserProperties.getPathGetUser(), supportUserProperties.getConnectionTimeout(), supportUserProperties.getReadTimeout());
    }

    @Bean(name = "userClientChangePassword")
    public ReactiveConnector userClientChangePassword(SupportUserProperties supportUserProperties) {
        return new ReactiveConnector("userClientChangePassword", supportUserProperties.getUrl() + supportUserProperties.getPathChangePassword(), supportUserProperties.getConnectionTimeout(), supportUserProperties.getReadTimeout());
    }

    @Bean(name = "userClientGenerateOTP")
    public ReactiveConnector userClientGenerateOTP(SupportUserProperties supportUserProperties) {
        return new ReactiveConnector("userClientGenerateOTP", supportUserProperties.getUrl() + supportUserProperties.getPathGenerateOTP(), supportUserProperties.getConnectionTimeout(), supportUserProperties.getReadTimeout());
    }

    @Bean(name = "userClientResetPassword")
    public ReactiveConnector userClientResetPassword(SupportUserProperties supportUserProperties) {
        return new ReactiveConnector("userClientResetPassword", supportUserProperties.getUrl() + supportUserProperties.getPathResetPassword(), supportUserProperties.getConnectionTimeout(), supportUserProperties.getReadTimeout());
    }

    @Bean("supportOTPConnector")
    public ReactiveConnector supportOTPConnector(SupportOTPProperties supportOTPProperties) {
        return new ReactiveConnector("supportOTPConnector", supportOTPProperties.getUrl(), supportOTPProperties.getConnectionTimeout(), supportOTPProperties.getReadTimeout());
    }

    @Bean("smsLoginConnector")
    public ReactiveConnector smsLoginConnector(SupportSmsProperties supportSmsProperties) {
        return new ReactiveConnector("smsLoginConnector", supportSmsProperties.getUrl(), supportSmsProperties.getConnectionTimeout(), supportSmsProperties.getReadTimeout());
    }

    @Bean("cryptoHelper")
    public CryptoHelper cryptoHelper(Environment environment) {
        return new CryptoHelper(StandardCharsets.UTF_8, CryptoSpec.AES_CBC,
                environment.getRequiredProperty("crypt.key"),
                environment.getRequiredProperty("crypt.initializationVector"));
    }

    @Bean("googleRecaptcha")
    public GoogleRecaptcha googleRecaptcha(ObjectMapper objectMapper, GlobalProperties googleProperties) {
        return new GoogleRecaptcha(objectMapper, googleProperties);
    }

    @Bean(value = REGISTER_API)
    public ReactiveConnector registerConnector(RegisterProperties registerProperties) {
        return new ReactiveConnector(REGISTER_API, registerProperties.getUrl(), registerProperties.getTimeoutConnect(), registerProperties.getTimeoutRead());
    }
}

