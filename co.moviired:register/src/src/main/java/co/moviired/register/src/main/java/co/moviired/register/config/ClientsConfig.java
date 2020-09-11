package co.moviired.register.config;

import co.moviired.base.domain.enumeration.CryptoSpec;
import co.moviired.base.helper.CryptoHelper;
import co.moviired.connector.connector.ReactiveConnector;
import co.moviired.register.helper.CryptHelper;
import co.moviired.register.helper.SignatureHelper;
import co.moviired.register.properties.*;
import lombok.Data;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;

import static co.moviired.register.helper.ConstantsHelper.*;

/*
 * Copyright @2018. MOVIIRED. Todos los derechos reservados.
 *
 * @author RIVAS, Ronel
 * @version 1, 2019-10-07
 * @since 1.0
 */

@Data
@Configuration
public class ClientsConfig {

    // MAHINDRA
    @Bean(name = "mahindraClient")
    @Primary
    public ReactiveConnector mahindraClient(@NotNull MahindraProperties mahindraProperties) {
        return new ReactiveConnector("mahindraClient", mahindraProperties.getUrl(), mahindraProperties.getConnectionTimeout(), mahindraProperties.getReadTimeout());
    }

    // SUPPORT-OTP
    @Bean(name = "supportOtpClient")
    public ReactiveConnector supportOtpClient(@NotNull OtpProperties otpProperties) {
        return new ReactiveConnector("supportOtpClient", otpProperties.getUrl(), otpProperties.getConnectionTimeout(), otpProperties.getReadTimeout());
    }

    // SUPPORT-AUTHENTICATION
    @Bean(name = "supportAuthClient")
    public ReactiveConnector supportAuthClient(@NotNull SupportAuthProperties supportAuthProperties) {
        return new ReactiveConnector("supportAuthClient", supportAuthProperties.getUrl(), supportAuthProperties.getTimeoutConnection(), supportAuthProperties.getTimeoutTransaction());
    }

    // ADO
    @Bean(name = "adoClient")
    public ReactiveConnector adoClient(@NotNull AdoProperties adoProperties) {
        return new ReactiveConnector("adoClient", adoProperties.getUrl(), adoProperties.getTimeoutConnect(), adoProperties.getTimeoutRead());
    }

    // CLEVERTAP
    @Bean(name = "cleverTapClient")
    public ReactiveConnector cleverTapClient(@NotNull ClevertapProperties clevertapProperties) {
        return new ReactiveConnector("cleverTapClient", clevertapProperties.getUrl(), clevertapProperties.getTimeoutConnect(), clevertapProperties.getTimeoutRead());
    }

    // SUPPORT-SMS
    @Bean("supportSmsClient")
    public ReactiveConnector supportSmsClient(SmsProperties smsProperties) {
        return new ReactiveConnector("supportSmsClient", smsProperties.getUrl(), smsProperties.getConnectionTimeout(), smsProperties.getReadTimeout());
    }

    @Bean(name = CML_API)
    public ReactiveConnector cmlClient(CmlProperties cmlProperties) {
        return new ReactiveConnector(CML_API, cmlProperties.getUrl(), cmlProperties.getTimeoutConnect(), cmlProperties.getTimeoutRead());
    }

    @Bean(CRYPTO_HELPER)
    public CryptoHelper cryptoHelper(@NotNull Environment environment) {
        return new CryptoHelper(StandardCharsets.UTF_8, CryptoSpec.AES_CBC, environment.getRequiredProperty("crypto.key"), environment.getRequiredProperty("crypto.init-vector"));
    }

    @Bean("supportAuthCryptHelper")
    public CryptHelper supportAuthCryptHelper(SupportAuthProperties supportAuthProperties) {
        return new CryptHelper(supportAuthProperties.getSecretKey(), supportAuthProperties.getInitVector());
    }

    @Bean(name = SIGNATURE_HELPER)
    public SignatureHelper signatureHelper(@NotNull GlobalProperties globalProperties, @NotNull StatusCodeConfig statusCodeConfig) throws IOException, NoSuchAlgorithmException {
        return new SignatureHelper(globalProperties.getSecret(), statusCodeConfig);
    }
}

