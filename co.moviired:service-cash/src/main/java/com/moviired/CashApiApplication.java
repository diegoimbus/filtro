package com.moviired;

import co.moviired.base.domain.enumeration.CryptoSpec;
import co.moviired.base.helper.CryptoHelper;
import com.moviired.conf.StatusCodeConfig;
import com.moviired.controller.CashISOController;
import com.moviired.helper.AtallaHelper;
import com.moviired.helper.Constant;
import com.moviired.helper.SignatureHelper;
import com.moviired.model.Configurations;
import com.moviired.properties.*;
import com.moviired.security.AuthorizationFilter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jpos.iso.ISOException;
import org.jpos.iso.packager.GenericPackager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/*
 * Copyright @2018. MOVIIRED. Todos los derechos reservados.
 *
 * @author RIVAS, Ronel
 * @version 1, 2018-12-19
 * @since 1.0
 */

@Slf4j
@SpringBootApplication
@EnableConfigurationProperties(value = {
        StatusCodeConfig.class,
        SupportOTPProperties.class,
        SupportUserProperties.class,
        MahindraProperties.class,
        AvalProperties.class,
        SupportSmsProperties.class,
        CmdConsultBalanceProperties.class,
        AtallaProperties.class
})

public class CashApiApplication implements ApplicationListener<ContextRefreshedEvent> {

    private static final String LOG_LINE = "-------------------------------------------";

    private final ApplicationContext applicationContext;
    private final Configurations wsConfig;
    private final SupportOTPProperties supportOTPProperties;
    private boolean isActive = true;
    private boolean processIsRunning = true;


    public CashApiApplication(ApplicationContext pApplicationContext, Configurations pwsConfig, SupportOTPProperties pSupportOTPProperties) {
        this.wsConfig = pwsConfig;
        this.applicationContext = pApplicationContext;
        this.supportOTPProperties = pSupportOTPProperties;
    }

    public static void main(String[] args) {
        // Iniciar entrada: REST
        final SpringApplication app = new SpringApplication(CashApiApplication.class);
        app.run(args);
    }

    private void setProcessIsRunning(boolean pProcessIsRunning) {
        this.processIsRunning = pProcessIsRunning;
    }

    @Override
    public final void onApplicationEvent(@NotNull ContextRefreshedEvent event) {
        try {

            //Iniciar entrada: ISO
            if(wsConfig.isEnabledConnection()) {
                CashISOController isoClient = applicationContext.getBean(CashISOController.class);
                ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
                executor.submit(() -> {
                    while (isActive) {
                        try {
                            setProcessIsRunning(true);
                            while (processIsRunning) {
                                CompletableFuture<Void> futures = CompletableFuture.allOf(CompletableFuture.runAsync(isoClient, executor));
                                futures.join();
                                futures.get(Constant.SERVIBANCA_CONNECTION_TRIAL_TIME, TimeUnit.SECONDS);
                                log.debug("Reiniciando conexion ... ip : {} port {} ", wsConfig.getSocketIp(), wsConfig.getSocketPort());
                                setProcessIsRunning(false);
                            }
                        } catch (Exception e) {
                            log.error("Error executing process: {}", e.getMessage());
                        }
                    }
                    executor.shutdownNow();
                });
            }
            // Evidenciar en el LOG el inicio correcto de los servicios
            log.info(LOG_LINE);
            log.info("");
            log.info(LOG_LINE);
            log.info("SRV-CASH: API REST - Port: " + wsConfig.getRestPort());
            log.info("SRV-CASH: API REST - Launched [OK]");
            log.info(LOG_LINE);
            log.info("");
            log.info(LOG_LINE);
            log.info("SRV-CASH: SOAP - Port: " + wsConfig.getRestPort());
            log.info("SRV-CASH: SOAP - Launched [OK]");
            log.info(LOG_LINE);
            log.info("");
            log.info(LOG_LINE);
            log.info("SRV-CASH VERSION: " + wsConfig.getApplicationVersion());
            log.info(LOG_LINE);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * @param environment
     */
    @Bean("cryptoHelperAuthorization")
    public CryptoHelper cryptoHelperAuthorization(Environment environment) {
        return new CryptoHelper(StandardCharsets.UTF_8, CryptoSpec.AES_CBC,
                environment.getRequiredProperty("crypt.key"),
                environment.getRequiredProperty("crypt.initializationVector"));
    }

    /**
     *
     */
    @Bean("cryptoHelperOtp")
    public CryptoHelper cryptoHelperOtp() {
        return new CryptoHelper(StandardCharsets.UTF_8, CryptoSpec.AES_CBC, supportOTPProperties.getKey(), supportOTPProperties.getInitVector());
    }

    /**
     *
     */
    @Bean
    public GenericPackager servibancaPackager() throws IOException, ISOException {
        return new GenericPackager(new ClassPathResource("iso/servibanca-packager.xml").getInputStream());
    }

    /**
     * @param pWsConfig
     */
    @Bean(name = "signatureHelper")
    public SignatureHelper signatureHelper(Configurations pWsConfig) throws IOException, NoSuchAlgorithmException {
        return new SignatureHelper(pWsConfig.getSecret());
    }

    /**
     * @param pWsConfig
     */
    @Bean(name = "ipAddress")
    public int ipAddress(Configurations pWsConfig) {
        return pWsConfig.getIpAddress();
    }


    /**
     * @param pWsConfig
     * @param cryptoHelperAuthorization
     */
    @Bean
    public FilterRegistrationBean<AuthorizationFilter> loggingFilter(@javax.validation.constraints.NotNull Configurations pWsConfig, @javax.validation.constraints.NotNull CryptoHelper cryptoHelperAuthorization) {
        FilterRegistrationBean<AuthorizationFilter> registrationBean
                = new FilterRegistrationBean<>();

        registrationBean.setFilter(new AuthorizationFilter(cryptoHelperAuthorization));
        registrationBean.addUrlPatterns(pWsConfig.getUri() + "/*");

        return registrationBean;
    }

    /**
     * @param atallaProperties
     */
    @Bean
    public AtallaHelper atallaHelper(@NotNull AtallaProperties atallaProperties) {
        return new AtallaHelper(atallaProperties.getHost(), atallaProperties.getPort(), atallaProperties.getTimeOut(), atallaProperties);
    }
}


