package co.moviired.register.helper;

import co.moviired.connector.connector.ReactiveConnector;
import co.moviired.register.properties.SupportAuthProperties;
import co.moviired.register.providers.supportauth.ResponseSupportAuth;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.Serializable;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/*
 * Copyright @2019. MOVIIRED. Todos los derechos reservados.
 *
 * @author JAP, SBD
 * @version 1, 2019-08-30
 * @since 2.0
 */

@Slf4j
@Component
public final class SupportAuthHelper implements Serializable {

    private final SupportAuthProperties supportAuthConfig;
    private final ReactiveConnector supportAuthClient;
    private final CryptHelper supportAuthCryptHelper;

    public SupportAuthHelper(CryptHelper pSupportAuthCryptHelper, SupportAuthProperties pSupportAuthConfig, ReactiveConnector pSupportAuthClient) {
        this.supportAuthConfig = pSupportAuthConfig;
        this.supportAuthClient = pSupportAuthClient;
        this.supportAuthCryptHelper = pSupportAuthCryptHelper;
    }

    public Mono<ResponseSupportAuth> validatePinFormat(String phoneNumber, String documentNum, String mPin, String dob) throws IOException {
        // Verificar si está habilitada la seguridad de clave
        if (!supportAuthConfig.isEnableValidatePIN()) {
            ResponseSupportAuth responseSupportAuth = new ResponseSupportAuth();
            responseSupportAuth.setErrorCode("00");
            return Mono.just(responseSupportAuth);
        }

        // Codificar los datos bases
        String cellNumber = supportAuthCryptHelper.encoder(phoneNumber);
        String documentNumber = supportAuthCryptHelper.encoder(documentNum);
        String password = supportAuthCryptHelper.encoder(mPin);

        // Armar la petición al servicio de validación el fortmato de clave
        String request = supportAuthConfig.getUrl()
                .concat("?cellphone=").concat(URLEncoder.encode(cellNumber, StandardCharsets.UTF_8.toString()))
                .concat("&documentNumber=").concat(URLEncoder.encode(documentNumber, StandardCharsets.UTF_8.toString()))
                .concat("&password=").concat(URLEncoder.encode(password, StandardCharsets.UTF_8.toString()))
                .concat("&dob=").concat(dob);

        // Invocar al servicio de validación el fortmato de clave
        log.info("enviada peticion de validacion {}", request);
        return this.supportAuthClient.get(request, ResponseSupportAuth.class, MediaType.APPLICATION_JSON, null)
                .flatMap(resp -> Mono.just((ResponseSupportAuth) resp));
    }

}

