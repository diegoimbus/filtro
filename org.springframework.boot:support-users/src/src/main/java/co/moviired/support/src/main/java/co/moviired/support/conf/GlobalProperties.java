package co.moviired.support.conf;
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

@Data
@Component
public class GlobalProperties {

    // Versión de la aplicación
    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${spring.application.version}")
    private String applicationVersion;

    // Puertos de comunicación
    @Value("${server.port}")
    private int restPort;

    // context
    @Value("${server.servlet.context-path}")
    private String context;
    private String urlPath;
    @Value("${client.timeLifeInMillis}")
    private long clientTimeLifeInMillis;

    // EMAIL
    @Value("${properties.email.url}")
    private String urlServiceSendEmail;
    @Value("${properties.email.pathRegistryUser}")
    private String pathRegistryUser;
    @Value("${properties.email.pathUpdateUser}")
    private String pathUpdateUser;
    @Value("${properties.email.pathRejectedUser}")
    private String pathRejectedUser;
    @Value("${properties.email.pathApprovedUser}")
    private String pathApprovedUser;
    @Value("${properties.email.pathAssignPin}")
    private String pathAssignPin;
    @Value("${properties.email.pathUserManipulated}")
    private String pathUserManipulated;
    @Value("${properties.email.urlPortal}")
    private String urlPortal;

    // Primer usuario admon

    @Value("${providers.firstUser.firstName}")
    private String firstName;
    @Value("${providers.firstUser.msisdn}")
    private String msisdn;
    @Value("${providers.firstUser.mpin}")
    private String mpin;
    @Value("${providers.firstUser.cellPhone}")
    private String cellPhone;
    @Value("${providers.firstUser.userType}")
    private String userType;
    @Value("${providers.firstUser.dob}")
    private String dob;
    @Value("${providers.firstUser.idType}")
    private String idType;
    @Value("${providers.firstUser.agentCode}")
    private String agentCode;
    @Value("${providers.firstUser.idno}")
    private String idno;
    @Value("${providers.firstUser.email}")
    private String email;
    @Value("${providers.firstUser.gender}")
    private String gender;
    @Value("${providers.firstUser.status}")
    private String status;

// Primer usuario risk

    @Value("${providers.riskUser.firstName}")
    private String riskFirstName;
    @Value("${providers.riskUser.msisdn}")
    private String riskMsisdn;
    @Value("${providers.riskUser.mpin}")
    private String riskMpin;
    @Value("${providers.riskUser.cellPhone}")
    private String riskCellPhone;
    @Value("${providers.riskUser.userType}")
    private String riskUserType;
    @Value("${providers.riskUser.dob}")
    private String riskDob;
    @Value("${providers.riskUser.idType}")
    private String riskIdType;
    @Value("${providers.riskUser.agentCode}")
    private String riskAgentCode;
    @Value("${providers.riskUser.idno}")
    private String riskIdno;
    @Value("${providers.riskUser.email}")
    private String riskEmail;
    @Value("${providers.riskUser.gender}")
    private String riskGender;
    @Value("${providers.riskUser.status}")
    private String riskStatus;

}


