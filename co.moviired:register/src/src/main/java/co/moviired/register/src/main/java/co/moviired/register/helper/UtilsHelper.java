package co.moviired.register.helper;

import co.moviired.base.domain.exception.ParsingException;
import co.moviired.base.helper.CryptoHelper;
import co.moviired.register.config.StatusCodeConfig;
import co.moviired.register.domain.enums.register.ServiceStatusCode;
import co.moviired.register.domain.model.register.CleanAddressReplace;
import co.moviired.register.exceptions.ServiceException;
import co.moviired.register.properties.CleanAddressProperties;
import co.moviired.register.properties.GlobalProperties;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static co.moviired.register.helper.ConstantsHelper.*;

@Slf4j
public final class UtilsHelper {

    private static final SecureRandom RANDOM_GENERATOR = new SecureRandom();

    private UtilsHelper() {
        super();
    }

    public static String getRandomUUID() {
        return UUID.randomUUID().toString().replace(STRING_LINE, "");
    }

    public static Object consume(String url, Object request, Class cls, String token, HttpComponentsClientHttpRequestFactory requestFactory) {
        List<MediaType> acceptableMediaTypes = new ArrayList<>();
        acceptableMediaTypes.add(MediaType.APPLICATION_JSON);
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(acceptableMediaTypes);
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (token != null) {
            headers.set("Authorization", token);
        }

        log.info("Peticion: url= " + url);
        HttpEntity<String> entity = new HttpEntity<>(new Gson().toJson(request), headers);
        RestTemplate restTemplate = requestFactory != null ? new RestTemplate(requestFactory) : new RestTemplate();
        restTemplate.getMessageConverters()
                .add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));
        return restTemplate.postForEntity(url, entity, cls);

    }

    public static void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (Exception ie) {
            log.error(ie.getMessage());
        }
    }

    // Verificar turno de ejecución del JOB actual
    public static boolean validateShift(int ipAddress) {
        // IP + RANDOM + TIMESTAMP
        long shift = ipAddress + RANDOM_GENERATOR.nextInt() + new Date().getTime();
        return (shift % 2 != 0);
    }

    public static String asignarCorrelativo(String correlation) {
        String cId = correlation;

        if (correlation == null || correlation.isEmpty()) {
            cId = UtilsHelper.getRandomUUID();
        }

        MDC.putCloseable("correlation-id", cId);
        MDC.putCloseable("component", "register");
        return cId;
    }

    public static String cleanAddress(CleanAddressProperties cleanAddressProperties, String pinitAddress) {
        String initAddress = pinitAddress;
        for (CleanAddressReplace cleanAddressReplace : cleanAddressProperties.getCleanAddressReplaces()) {
            initAddress = initAddress.toLowerCase().replace(cleanAddressReplace.getInitValue(), cleanAddressReplace.getFinalValue());
        }
        initAddress = initAddress.replaceAll(cleanAddressProperties.getCleanAddressRegex(), " ");
        initAddress = initAddress.replaceAll(cleanAddressProperties.getCleanMultipleSpacesRegex(), " ");
        return initAddress;
    }

    public static boolean isNotNumber(String chain) {
        try {
            Long.parseLong(chain);
            return false;
        } catch (NumberFormatException e) {
            return true;
        }
    }

    public static String[] getAuthorizationParts(String authorizationHeader, StatusCodeConfig statusCodeConfig, GlobalProperties globalProperties) throws ServiceException {
        ServiceException exception = new ServiceException(
                ServiceStatusCode.AUTHORIZATION_HEADER_INVALID.getStatusCode(),
                statusCodeConfig.of(ServiceStatusCode.AUTHORIZATION_HEADER_INVALID.getStatusCode()).getMessage(), globalProperties.getName());
        if (authorizationHeader == null) {
            throw exception;
        }
        if (!authorizationHeader.contains(TWO_DOTS)) {
            throw exception;
        }
        if (authorizationHeader.length() != AUTHORIZATION_LENGTH) {
            throw exception;
        }
        String[] authorizationParts = authorizationHeader.split(TWO_DOTS);
        if (authorizationParts.length != 2) {
            throw exception;
        }
        if (authorizationParts[0].length() != PHONE_NUMBER_LENGTH) {
            throw exception;
        }
        if (authorizationParts[1].length() != PIN_LENGTH) {
            throw exception;
        }
        if (isNotNumber(authorizationParts[0]) || isNotNumber(authorizationParts[1])) {
            throw exception;
        }
        log.info(LOG_NUMBER_OF_REQUEST, authorizationParts[0]);
        return authorizationParts;
    }

    public static String getAuthorizationHeader(CryptoHelper cryptoHelper, String msisdn, String pin) {
        try {
            return cryptoHelper.encoder(msisdn) + TWO_DOTS + cryptoHelper.encoder(pin);
        } catch (ParsingException e) {
            log.error(LOG_ERROR_GENERATING_AUTHORIZATION, e.getMessage());
            return EMPTY_STRING;
        }
    }

    public static String cleanNameForBlackList(String name) {
        return name.toUpperCase()
                .replaceAll("[Ñ]", "N")
                .replaceAll("[ÁÄ]", "A")
                .replaceAll("[ÉË]", "E")
                .replaceAll("[ÍÏ]", "I")
                .replaceAll("[ÓÖ]", "O")
                .replaceAll("[ÚÜ]", "U")
                .replaceAll(REGEX_CLEAN_NAMES, "")
                .trim();
    }
}

