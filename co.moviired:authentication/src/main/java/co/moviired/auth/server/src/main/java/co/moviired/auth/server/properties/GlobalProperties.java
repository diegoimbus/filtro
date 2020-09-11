package co.moviired.auth.server.properties;
/*
 * Copyright @2020. Moviired, SAS. Todos los derechos reservados.
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

    private static final long serialVersionUID = -4351094487498311258L;

    // Versión de la aplicación
    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${spring.application.version}")
    private String applicationVersion;

    @Value("${server.port}")
    private int restPort;

    // Contexto
    @Value("${server.servlet.context-path}")
    private String context;

    // Portal
    @Value("${auth2.portal.client}")
    private String clientPortal;
    @Value("${auth2.portal.password}")
    private String passClientPortal;

    // Service
    @Value("${auth2.service.client}")
    private String clientService;
    @Value("${auth2.service.password}")
    private String passwordService;

    // Mobile
    @Value("${auth2.mobile.client}")
    private String clientMobile;
    @Value("${auth2.mobile.password}")
    private String passwordMobile;

    @Value("${providers.google.captcha.url}")
    private String urlGoogle;
    @Value("${providers.google.captcha.secret}")
    private String secretGoogle;

    @Value("${properties.encript.token}")
    private String token;

    @Value("${properties.encript.expiration}")
    private String expiration;
}

