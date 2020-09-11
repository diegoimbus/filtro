package com.moviired.model;

import co.moviired.base.util.Security;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;

/*
 * Copyright @2018. MOVIIRED. Todos los derechos reservados.
 *
 * @author Cuadros Cerpa, Cristian Alfonso
 * @version 1, 2018-06-20
 * @since 1.0
 */

@Slf4j
@Data
@Component
@Configuration
@EnableScheduling
public class Configurations implements Serializable {

    // Versión de la aplicación
    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${spring.application.version}")
    private String applicationVersion;

    @Value("${spring.application.services.rest.uri}")
    private String uri;

    // Puertos de comunicación
    @Value("${server.port}")
    private int restPort;

    private int ipAddress;

    // Servicios Habilitar/Deshabilitar
    @Value("${spring.application.methods.cashInEnabled}")
    private boolean cashInEnabled;
    @Value("${spring.application.methods.cashReversoEnabled}")
    private boolean cashReversoEnabled;
    @Value("${spring.application.methods.cashOutInitializerEnabled}")
    private boolean cashOutInitializerEnabled;
    @Value("${spring.application.methods.cashOutPendingEnabled}")
    private boolean cashOutPendingEnabled;
    @Value("${spring.application.methods.cashOutCompletedEnabled}")
    private boolean cashOutCompletedEnabled;
    @Value("${spring.application.methods.validateSubscriberEnabled}")
    private boolean validateSubscriberEnabled;
    @Value("${spring.application.methods.validateTransactionsEnabled}")
    private boolean validateTransactionsEnabled;
    @Value("${spring.application.methods.aval.cashOutInitializerEnabled}")
    private boolean avalCashOutInitializerEnabled;
    @Value("${spring.application.methods.aval.cashOutEnabled}")
    private boolean avalCashOutEnabled;
    @Value("${properties.simultaneous-cashout}")
    private Integer simultaneousCashout;

    // Propriedades
    @Value("${properties.secret}")
    private String secret;
    @Value("${properties.SNDPROVIDER}")
    private String sndprovider;
    @Value("${properties.RCVPROVIDER}")
    private String rcvprovider;
    @Value("${properties.SNDINSTRUMENT}")
    private String sndinstrument;
    @Value("${properties.RCVINSTRUMENT}")
    private String rcvinstrument;
    @Value("${properties.LANGUAGE1}")
    private String language1;
    @Value("${properties.LANGUAGE2}")
    private String language2;
    @Value("${properties.LANGUAGE1COMPLET}")
    private String language1Complet;
    @Value("${properties.LANGUAGE2COMPLET}")
    private String language2Complet;
    @Value("${properties.NAME_CASHIN}")
    private String nameCashin;
    @Value("${properties.NAME_REVERSO}")
    private String nameReverso;
    @Value("${properties.IS_TCP_CHECK_REQ}")
    private String isTcpCheckReq;
    @Value("${properties.NAME_SOLCASHOUT}")
    private String nameSolcashout;
    @Value("${properties.PROVIDER}")
    private String provider;
    @Value("${properties.PROVIDER2}")
    private String provider2;
    @Value("${properties.PROVIDERCOMPLET}")
    private String providerComplet;
    @Value("${properties.PROVIDER2COMPLET}")
    private String provider2Complet;
    @Value("${properties.PAYID}")
    private String payId;
    @Value("${properties.PAYID2}")
    private String payId2;
    @Value("${properties.PAYIDCOMPLET}")
    private String payIdComplet;
    @Value("${properties.PAYID2COMPLET}")
    private String payId2Complet;
    @Value("${properties.BLOCKSMS}")
    private String blockSms;
    @Value("${properties.BLOCKSMSCOMPLET}")
    private String blockSmsComplet;
    @Value("${properties.SUBTYPE}")
    private String subtype;
    @Value("${properties.CONFIRM_REQ}")
    private String confirmReq;
    @Value("${properties.SECRET_KEY}")
    private String secretKey;
    @Value("${properties.NAME_CASHOUTPENDING}")
    private String nameCashoutPending;
    @Value("${properties.PROVIDERCPENDING}")
    private String providerCPending;
    @Value("${properties.PAYIDCPENDING}")
    private String payIdCPending;
    @Value("${properties.TXNID}")
    private String txnId;
    @Value("${properties.BLOCKSMSCPENDING}")
    private String blockSmsPending;
    @Value("${properties.SERVICE}")
    private String service;
    @Value("${properties.TXNMODE}")
    private String txnMode;
    @Value("${properties.TXNMODECOMPLET}")
    private String txnModeComplet;
    @Value("${properties.NOOFTXNREQ}")
    private String nooFtxnReq;
    @Value("${properties.NAME_CASHOUTCOMPLETED}")
    private String nameCashoutCompleted;
    @Value("${properties.ACTION}")
    private String action;
    @Value("${properties.USERTYPE}")
    private String userType;
    @Value("${properties.TYPE}")
    private String type;
    @Value("${properties.PROVIDERSUBSCRIBER}")
    private String providerSubscriber;
    @Value("${properties.NAME_QUERY_TRANSACTION}")
    private String nameQueryTransaction;
    @Value("${properties.USERTYPEQT}")
    private String userTypeQt;
    @Value("${properties.SYSTEM}")
    private String system;
    @Value("${properties.NAME_QUERY_TRANSACTION_MH}")
    private String nameQueryTransactionMh;
    @Value("${properties.NAME_AUTHPINREQ}")
    private String nameAuthpinreq;

    @Value("${properties.NAME_USRQRYINFO}")
    private String nameUsrQryInfo;

    @Value("${properties.OTPREQ}")
    private String otpReq;
    @Value("${properties.ISPINCHECKREQ}")
    private String isPinCheckReq;
    @Value("${properties.SOURCE}")
    private String source;
    @Value("${properties.PROVIDERAUTH}")
    private String providerAuth;
    @Value("${properties.USERTYPEMERCHANT}")
    private String userTypeMerchant;
    @Value("${properties.SCREVERSAL}")
    private String scReversal;
    @Value("${properties.ACTIONREVERSO}")
    private String actionReverso;
    @Value("${properties.CMREVERSAL}")
    private String cmReversal;
    @Value("${properties.returnAgentCode}")
    private String returnAgentCode;
    @Value("${properties.NAME_HOLDMONEY}")
    private String nameHoldMoney;

    // HOLD_MONEY
    @Value("${properties.NAME_UNHOLDMONEY}")
    private String nameUnholdMoney;
    @Value("${properties.RELEASE_AFTER_DAYS}")
    private Integer releaseAfterDays;
    @Value("${properties.PRIORITY_REQUEST_TYPE}")
    private String priorityRequestType;
    @Value("${properties.SUFIJO_CELULAR}")
    private String sufijoCelular;


    // SMS DB
    @Value("${properties.TEXTO_SMS1}")
    private String textoSms1;
    @Value("${properties.TEXTO_SMS2}")
    private String textoSms2;
    @Value("${properties.TEXTO_SMS3}")
    private String textoSms3;
    @Value("${properties.TEXTO_SMS4}")
    private String textoSms4;
    @Value("${properties.TEXTO_SMS5}")
    private String textoSms5;
    // EMAIL
    @Value("${properties.email.url}")
    private String urlServiceSendEmail;
    @Value("${properties.email.pathConsignmentProcess}")
    private String pathConsignmentProcess;
    // HTTP Pool
    @Value("${properties.HttpPoolTotalMax}")
    private Integer httpPoolTotalMax;
    @Value("${properties.HttpPoolRouteMax}")
    private Integer httpPoolRouteMax;
    @Value("${properties.HttpPoolCheckInactivityInMillis}")
    private Integer httpPoolCheckInactivityInMillis;
    @Value("${properties.TimeoutTransaction}")
    private Integer timeoutTransaction;
    @Value("${properties.TimeoutConnection}")
    private Integer timeoutConnection;
    @Value("${servibanca.services.iso.ip}")
    private String socketIp;

    // SERVIBANCA
    @Value("${servibanca.services.iso.enabledConnection}")
    private boolean enabledConnection;

    @Value("${servibanca.services.iso.port}")
    private int socketPort;
    @Value("${servibanca.services.iso.timeoutInMillis.connection}")
    private int connectionTimeout;
    @Value("${servibanca.services.iso.timeoutInMillis.read}")
    private int transactionTimeout;
    @Value("${servibanca.properties.cost}")
    private Integer srvBancaCost;
    @Value("${servibanca.properties.binPAN}")
    private String binPAN;
    @Value("${servibanca.properties.field54-accountType}")
    private String field54ACCT;
    @Value("${servibanca.properties.field54-balanceType}")
    private String field54TYPE;
    @Value("${servibanca.properties.field54-currType}")
    private String field54CURRCODE;
    @Value("${servibanca.properties.field54-sign}")
    private String field54SIGN;
    @Value("${oracle.dataecho}")
    private String dataEcho;
    @Value("${oracle.dataquery}")
    private String dataQuery;
    @Value("${oracle.dataconsignment}")
    private String dataConsignment;

    @Value("${properties.userServibanca.agentCodeServibanca}")
    private String agentCodeServibanca;

    @Value("${properties.userServibanca.agentCodeServibancaAliados}")
    private String agentCodeServibancaAliados;

    @Value("${properties.job.properties.timeFindCronGiroComplete}")
    private int timeFindCronGiroComplete;

    @Value("${properties.costTransaction.exceptions.exceptionA}")
    private boolean exceptionA;

    @Value("${properties.costTransaction.exceptions.exceptionB}")
    private boolean exceptionB;





    /**
     * metodo get ipAddress
     *
     * @param
     * @return int
     */
    public int getIpAddress() {
        try {
            ipAddress = InetAddress.getLocalHost().hashCode();
        } catch (UnknownHostException e) {
            log.error(e.getMessage(), e);
        }
        return ipAddress;
    }


    /**
     * metodo get getField54
     *
     * @param amount
     * @return int
     */
    public String getField54(String amount) {
        return field54ACCT.concat(field54TYPE).concat(field54CURRCODE).concat(field54SIGN).concat(amount);
    }

    /**
     * metodo get getKeySpec
     *
     * @param
     * @return SecretKeySpec
     */
    @Bean
    public SecretKeySpec getKeySpec() throws UnsupportedEncodingException, NoSuchAlgorithmException {
        return Security.generateKeyFrom(getSecret());
    }
}

