package co.moviired.auth.server.security;

import co.moviired.audit.service.PushAuditService;
import co.moviired.auth.server.controller.AuthenticationController;
import co.moviired.auth.server.domain.dto.Request;
import co.moviired.auth.server.domain.dto.Response;
import co.moviired.auth.server.domain.dto.User;
import co.moviired.auth.server.domain.enums.GeneralStatus;
import co.moviired.auth.server.domain.enums.OperationType;
import co.moviired.auth.server.domain.enums.ProviderType;
import co.moviired.auth.server.exception.CustomOauthException;
import co.moviired.auth.server.exception.ParseException;
import co.moviired.auth.server.helper.UtilHelper;
import co.moviired.auth.server.properties.GlobalProperties;
import co.moviired.auth.server.service.AuthenticationService;
import co.moviired.base.domain.exception.ParsingException;
import co.moviired.base.helper.CryptoHelper;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Proveedor de autentacion para el proyecto
 **/

@Slf4j
@Service
@SuppressWarnings("unchecked")
public final class CustomAuthentication implements AuthenticationProvider {

    private static final String STARTED = "STARTED";
    private static final String FINISHED = "FINISHED";
    private static final String RESPONSE = "RESPONSE ";
    private static final String REQUEST = "REQUEST ";
    private static final String LOG_FORMATED = " {} {} {}";
    private static final String LOG_COMPONENT = "PROCESS SUPPORT AUTHENTICATION";
    private static final String SUCCESS = "00";

    private static final String SOURCE = "source";
    private static final String CHANNEL = "channel";

    private final AuthenticationController authenticationController;
    private final AuthenticationService authenticationService;
    private final PushAuditService pushAuditService;
    private final CryptoHelper cryptoHelper;
    private final GlobalProperties globalProperties;

    public CustomAuthentication(@NotNull AuthenticationController pauthenticationController,
                                AuthenticationService pauthenticationService,
                                @NotNull PushAuditService ppublishService,
                                @NotNull GlobalProperties pglobalProperties,
                                @NotNull CryptoHelper pcryptoHelper) {
        super();
        this.authenticationController = pauthenticationController;
        this.authenticationService = pauthenticationService;
        this.pushAuditService = ppublishService;
        this.globalProperties = pglobalProperties;
        this.cryptoHelper = pcryptoHelper;
    }


    @Override
    public Authentication authenticate(Authentication authentication) {
        UsernamePasswordAuthenticationToken authenticationToken;

        log.info(LOG_FORMATED, LOG_COMPONENT, STARTED, "authenticate");
        String decryptedUser;
        try {
            decryptedUser = cryptoHelper.decoder(authentication.getPrincipal().toString());
        } catch (ParsingException e) {
            log.error(LOG_FORMATED, LOG_COMPONENT, "Encriptado de credenciales del usuario invalidas ", "");
            return null;
        }

        Request request = new Request(decryptedUser, "*****");
        log.info(LOG_FORMATED, LOG_COMPONENT, REQUEST + "a supportuser ", request);

        try {
            request = new Request(authentication.getPrincipal().toString(), authentication.getCredentials().toString());
            this.asignarCorrelativo(request);

            Map<String, String> details = (Map<String, String>) authentication.getDetails();
            setImeiSourceChannel(details, request);
            // SE REALIZA LOGIN
            Response responseLogin = this.authenticationController.login(request).block();
            log.info(LOG_FORMATED, LOG_COMPONENT, RESPONSE + "de supportuser ", responseLogin.getErrorMessage());

            HashMap<String, Object> mapCred = new HashMap<>();
            if (!SUCCESS.equals(responseLogin.getErrorCode())) {
                throw new CustomOauthException(responseLogin.getErrorCode() + ":" + responseLogin.getErrorMessage());
            }

            if ((responseLogin.getUser().getChangePasswordRequired() != null) && (responseLogin.getUser().getChangePasswordRequired().equalsIgnoreCase("REQUIRED"))) {
                throw new CustomOauthException("AUTH_92: Bienvenido! Se solicita cambio de clave por seguridad.");
            }

            // SE VERIFICA PERFIL
            log.info(LOG_FORMATED, LOG_COMPONENT, REQUEST + "a supportProfile ", request.getName());
            request.setName(responseLogin.getUser().getUserType());
            Response responseProfile = this.authenticationService.process(Mono.just(request), OperationType.PROFILE_NAME, ProviderType.SUPPORT_PROFILE).block();
            if(responseProfile.getProfile() != null) {
                responseProfile.getProfile().setOperations(null);
            }
            log.info(LOG_FORMATED, LOG_COMPONENT, RESPONSE + "de supportProfile ", responseProfile);

            if (responseProfile.getErrorCode().equals("99")) {
                throw new CustomOauthException("AUTH_103: Perfil inactivo o no existe.");
            }
            if ( responseProfile.getProfile() != null && responseProfile.getProfile().getStatus().equals(GeneralStatus.DISABLED)) {
                throw new CustomOauthException("AUTH_103: Perfil inactivo.");
            }

            authenticationToken = new UsernamePasswordAuthenticationToken(authentication.getName(), authentication.getCredentials().toString(), new ArrayList<>());
            User user = responseLogin.getUser();

            mapCred.put("credentials", authentication.getCredentials().toString());
            mapCred.put("user", user);
            mapCred.put("errorCode", "00");
            mapCred.put("errorMessage", "Login exitoso.");
            mapCred.put("role", user.getUserType());
            authenticationToken.setDetails(mapCred);


            // AUDITORIA
            Map<String, String> detailOperation = new LinkedHashMap<>();
            detailOperation.put(SOURCE, request.getSource());
            detailOperation.put(CHANNEL, request.getChannel());
            detailOperation.put("userType", user.getUserType());
            this.pushAuditService.pushAudit(
                    this.pushAuditService.generarAudit(
                            user.getMsisdn(),
                            "AUTHORIZATION",
                            request.getCorrelationId(),
                            "Se ha autenticado.",
                            detailOperation)
            );

        } catch (ParseException  | IOException e) {
            throw new CustomOauthException("01:Cuenta no existe o clave inválida.", e);
        }

        log.info(LOG_FORMATED, LOG_COMPONENT, FINISHED, "authenticate");
        return authenticationToken;
    }


    private Request setImeiSourceChannel(Map<String, String> details, Request request ) {
        if (details.get("imei") != null) {
            request.setImei(details.get("imei"));
        }
        if (details.get(SOURCE) != null) {
            request.setSource(details.get(SOURCE));
        }
        if (details.get(CHANNEL) != null) {
            request.setChannel(details.get(CHANNEL));
        }
        return request;
    }

    @Override
    public boolean supports(Class<? extends Object> authentication) {
        return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
    }

    private void asignarCorrelativo(Request request) {
        String cId = UtilHelper.generateCorrelationId();
        request.setCorrelationId(cId);
        MDC.putCloseable("correlation-id", cId);
        MDC.putCloseable("component", this.globalProperties.getApplicationName());
    }

}

