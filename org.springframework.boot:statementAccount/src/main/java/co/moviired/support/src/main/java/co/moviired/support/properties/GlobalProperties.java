package co.moviired.support.properties;
/*
 * Copyright @2019. Movii, SAS. Todos los derechos reservados.
 *
 * @author Bejarano, Cindy
 * @version 1, 2019
 * @since 1.0
 */

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Data
@Component
public class GlobalProperties implements Serializable {

    // Versión de la aplicación
    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${spring.application.secret}")
    private String secret;

    @Value("${spring.application.version}")
    private String applicationVersion;

    // Puertos de comunicación
    @Value("${server.port}")
    private int restPort;

    @Value("${spring.application.baseUrl}")
    private String baseUrl;

    // EMAIL
    @Value("${properties.email.url}")
    private String urlServiceSendEmail;
    @Value("${properties.email.pathConsignmentProcess}")
    private String pathConsignmentProcess;
    @Value("${properties.email.pathUserWarning}")
    private String pathUserWarning;
    @Value("${properties.email.emailRiskData}")
    private String emailRiskData;

    // JOBS BAR/UNBAR

    @Value("${spring.application.jobs.barAccount.rangeBar}")
    private Integer rangeBar;

    @Value("${properties.bar.type}")
    private String typeBarUnbar;

    @Value("${properties.bar.bar-type}")
    private String barType;

    @Value("${properties.bar.user-type}")
    private String barUserType;

    @Value("${properties.bar.provider}")
    private String barProvider;

    @Value("${properties.bar.reason}")
    private String barReason;


    // StatementAccounts
    @Value("${properties.statementAccounts.state.preJuridicoMin}")
    private Integer preJuridicoMin;
    @Value("${properties.statementAccounts.state.preJuridicoMax}")
    private Integer preJuridicoMax;

    @Value("${properties.statementAccounts.state.carteraMin}")
    private Integer carteraMin;
    @Value("${properties.statementAccounts.state.carteraMax}")
    private Integer carteraMax;

    @Value("${spring.application.services.rest.getDocument}")
    private String pathGetPdf;
}

