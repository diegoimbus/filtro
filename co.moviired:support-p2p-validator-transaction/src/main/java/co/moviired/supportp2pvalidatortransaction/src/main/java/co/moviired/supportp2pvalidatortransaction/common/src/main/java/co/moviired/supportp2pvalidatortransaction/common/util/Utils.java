package co.moviired.supportp2pvalidatortransaction.common.util;

import co.moviired.base.domain.StatusCode;
import co.moviired.base.domain.enumeration.ErrorType;
import co.moviired.base.domain.exception.ParsingException;
import co.moviired.base.helper.CryptoHelper;
import co.moviired.supportp2pvalidatortransaction.common.config.GlobalProperties;
import co.moviired.supportp2pvalidatortransaction.common.config.StatusCodeConfig;
import co.moviired.supportp2pvalidatortransaction.common.model.dto.IComponentDTO;
import co.moviired.supportp2pvalidatortransaction.common.model.dto.ResponseStatus;
import co.moviired.supportp2pvalidatortransaction.common.model.exceptions.ComponentThrowable;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

import javax.validation.constraints.NotNull;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static co.moviired.supportp2pvalidatortransaction.common.util.Constants.*;
import static co.moviired.supportp2pvalidatortransaction.common.util.StatusCodes.*;

@Slf4j
public class Utils {

    private static final Random randomGenerator = new Random();

    private Utils() {
        // Not is necessary this implementation
    }

    public static int getIpAddress() {
        int ipAddress = new Random().nextInt();
        try {
            ipAddress = InetAddress.getLocalHost().hashCode();
        } catch (UnknownHostException e) {
            log.error(e.getMessage(), e);
        }
        return ipAddress;
    }

    public static boolean validateShift(int ipAddress) {
        // IP + RANDOM + TIMESTAMP
        long shift = ipAddress + randomGenerator.nextInt() + new Date().getTime();
        return (shift % 2 != 0);
    }

    public static String assignCorrelative(GlobalProperties globalProperties, String correlation) {
        String cId = correlation;

        if (correlation == null || correlation.isEmpty())
            cId = getCorrelative();

        MDC.putCloseable(CORRELATIVE_ID, cId);
        MDC.putCloseable(COMPONENT_CORRELATIVE, globalProperties.getName());
        return cId;
    }

    public static String getCorrelative() {
        return UUID.randomUUID().toString().replace(STRING_LINE, EMPTY_STRING);
    }

    public static String[] getAuthorizationParts(GlobalProperties globalProperties, StatusCodeConfig statusCodeConfig, String authorizationHeader) throws ComponentThrowable {
        ComponentThrowable exception = new ComponentThrowable(ErrorType.DATA, AUTHORIZATION_HEADER_INVALID_CODE,
                statusCodeConfig.of(AUTHORIZATION_HEADER_INVALID_CODE).getMessage(), globalProperties.getName());
        if (authorizationHeader == null) throw exception;
        if (!authorizationHeader.contains(TWO_DOTS)) throw exception;
        if (authorizationHeader.length() != AUTHORIZATION_LENGTH) throw exception;
        String[] authorizationParts = authorizationHeader.split(TWO_DOTS);
        if (authorizationParts.length != 2) throw exception;
        if (authorizationParts[0].length() != PHONE_NUMBER_LENGTH) throw exception;
        if (authorizationParts[1].length() != PIN_LENGTH) throw exception;
        if (!isLongNumber(authorizationParts[0]) || !isLongNumber(authorizationParts[1])) throw exception;
        log.info(LOG_NUMBER_OF_REQUEST, authorizationParts[0]);
        return authorizationParts;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean isLongNumber(String chain) {
        try {
            Long.parseLong(chain);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static ComponentThrowable getDefaultException(GlobalProperties globalProperties, StatusCodeConfig statusCodeConfig) {
        return new ComponentThrowable(ErrorType.PROCESSING, statusCodeConfig.of(SERVER_ERROR_CODE).getCode(),
                statusCodeConfig.of(SERVER_ERROR_CODE).getMessage(),
                globalProperties.getName());
    }

    public static ObjectMapper getJsonMapper() {
        ObjectMapper jsonMapper = new ObjectMapper();
        jsonMapper.configure(SerializationFeature.INDENT_OUTPUT, false);
        return jsonMapper;
    }

    public static ObjectMapper getXmlMapper() {
        XmlMapper xmlMapper = new XmlMapper();
        xmlMapper.configure(SerializationFeature.INDENT_OUTPUT, false);
        return xmlMapper;
    }

    public static ResponseStatus getSuccessResponse(GlobalProperties globalProperties, StatusCodeConfig statusCodeConfig) {
        StatusCode statusCode = statusCodeConfig.of(SUCCESS_CODE);
        return ResponseStatus.builder().code(SUCCESS_CODE).message(statusCode.getMessage()).component(globalProperties.getName()).build();
    }

    public static ResponseStatus getErrorResponse(GlobalProperties globalProperties, StatusCodeConfig statusCodeConfig) {
        StatusCode statusCode = statusCodeConfig.of(SERVER_ERROR_CODE);
        return ResponseStatus.builder().code(statusCode.getCode()).message(statusCode.getMessage()).component(globalProperties.getName()).build();
    }

    public static ResponseStatus getErrorResponseByCode(GlobalProperties globalProperties, StatusCodeConfig statusCodeConfig, String code) {
        StatusCode statusCode = statusCodeConfig.of(code);
        return ResponseStatus.builder().code(statusCode.getCode()).message(statusCode.getMessage()).component(globalProperties.getName()).build();
    }

    @SuppressWarnings("ReplaceAllDot")
    public static String protectFields(String jsonText, String... protectFields) {
        for (String protectField : protectFields) {
            String regex = QUOTES + protectField + REGEX_REPLACE_JSON_VALUE;
            Matcher m = Pattern.compile(regex).matcher(jsonText);
            while (m.find()) {
                String match = m.group();
                String replace = m.group().replace(protectField, EMPTY_STRING).replace(QUOTES, EMPTY_STRING).replace(TWO_DOTS, EMPTY_STRING);
                String placeHolder = replace.replaceAll(REGEX_ALL, ASTERISK);
                String ret = match.replace(replace, placeHolder);
                jsonText = jsonText.replace(match, ret);
            }
        }
        return jsonText;
    }

    // HANDLE ERROR ****************************************************************************************************

    public static void handleThrowableError(GlobalProperties globalProperties, StatusCodeConfig statusCodeConfig, String operation, @NotNull String message, Throwable e, IComponentDTO response) {
        log.error(message, operation, e.getMessage());
        if (e instanceof ComponentThrowable) {
            response.setStatus(
                    ResponseStatus.builder()
                            .code(((ComponentThrowable) e).getCode())
                            .message(e.getMessage())
                            .component(((ComponentThrowable) e).getComponentOfError())
                            .build());
        } else {
            response.setStatus(getErrorResponse(globalProperties, statusCodeConfig));
        }
    }

    public static Map<String, String> addAuthorizationHeader(CryptoHelper cryptoHelper, String phoneNumber, String pin, Map<String, String> headers) throws ParsingException {
        if (headers == null) headers = new HashMap<>();
        headers.put(AUTHORIZATION_HEADER, cryptoHelper.encoder(phoneNumber) + TWO_DOTS + cryptoHelper.encoder(pin));
        return headers;
    }
}

