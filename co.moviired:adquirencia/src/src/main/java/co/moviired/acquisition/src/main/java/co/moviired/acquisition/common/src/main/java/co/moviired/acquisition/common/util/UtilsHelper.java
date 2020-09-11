package co.moviired.acquisition.common.util;

import co.moviired.acquisition.common.service.IController;
import co.moviired.base.domain.StatusCode;
import co.moviired.base.domain.enumeration.ErrorType;
import co.moviired.base.domain.exception.ParsingException;
import co.moviired.base.helper.CryptoHelper;
import co.moviired.acquisition.common.config.GlobalProperties;
import co.moviired.acquisition.common.config.StatusCodeConfig;
import co.moviired.acquisition.common.model.dto.IComponentDTO;
import co.moviired.acquisition.common.model.dto.ResponseStatus;
import co.moviired.acquisition.common.model.exceptions.ComponentThrowable;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.MDC;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.validation.constraints.NotNull;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static co.moviired.acquisition.common.util.ConstantsHelper.*;
import static co.moviired.acquisition.common.util.StatusCodesHelper.*;

@Slf4j
public final class UtilsHelper {

    private static final SecureRandom RANDOM_GENERATOR = new SecureRandom();

    private UtilsHelper() {
        // Not is necessary this implementation
    }

    public static int getIpAddress() {
        int ipAddress = RANDOM_GENERATOR.nextInt();
        try {
            ipAddress = InetAddress.getLocalHost().hashCode();
        } catch (UnknownHostException e) {
            log.error(e.getMessage(), e);
        }
        return ipAddress;
    }

    public static boolean validateShift(int ipAddress) {
        // IP + RANDOM + TIMESTAMP
        long shift = ipAddress + RANDOM_GENERATOR.nextInt() + new Date().getTime();
        return (shift % TWO_INT != ZERO_INT);
    }

    public static String assignCorrelative(GlobalProperties globalProperties, String correlation) {
        String cId = correlation;

        if (correlation == null || correlation.isEmpty()) {
            cId = getCorrelative();
        }

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
        if (authorizationParts.length != TWO_INT) {
            throw exception;
        }
        if (authorizationParts[ZERO_INT].length() != PHONE_NUMBER_LENGTH) {
            throw exception;
        }
        if (authorizationParts[ONE_INT].length() != PIN_LENGTH) {
            throw exception;
        }
        if (!isLongNumber(authorizationParts[ZERO_INT]) || !isLongNumber(authorizationParts[1])) {
            throw exception;
        }
        log.info(LOG_NUMBER_OF_REQUEST, authorizationParts[ZERO_INT]);
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
    public static String protectFields(String jsonTextI, String... protectFields) {
        String jsonText = jsonTextI;
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

    public static Map<String, String> addAuthorizationHeader(CryptoHelper cryptoHelper, String phoneNumber, String pin, Map<String, String> headersI) throws ParsingException {
        Map<String, String> headers = headersI;
        if (headers == null) {
            headers = new HashMap<>();
        }
        headers.put(AUTHORIZATION_HEADER, cryptoHelper.encoder(phoneNumber) + TWO_DOTS + cryptoHelper.encoder(pin));
        return headers;
    }

    /**
     * Get the first method of project called
     *
     * @return name of first method of component called
     */
    public static String getMethodName() {
        String packageName = IController.class.getPackageName().replace(COMMON_PACKAGE, EMPTY_STRING);
        String methodName = EMPTY_STRING;
        List<StackTraceElement> projectStackTrace = Arrays.stream(new Throwable().getStackTrace()).filter(stackTraceElement -> stackTraceElement.getClassName().contains(packageName))
                .collect(Collectors.toList());

        if (!projectStackTrace.isEmpty()) {
            methodName = projectStackTrace.get(projectStackTrace.size() - ONE_INT).getMethodName();
        }

        return methodName.replaceAll(REGEX_REMOVE_LAMBDA_OF_METHOD_NAME, EMPTY_STRING)
                .replaceAll(REGEX_REMOVE_FINAL_OF_LAMBDA_METHOD_NAME, EMPTY_STRING);
    }

    public static String randomAlphaNumeric(int count) {
        int charsCount = count;
        StringBuilder builder = new StringBuilder();
        while (charsCount-- != ZERO_INT) {
            int character = Integer.parseInt(String.valueOf(Math.round(RANDOM_GENERATOR.nextFloat() * (ALPHA_NUMERIC_STRING.length() - ONE_INT))));
            builder.append(ALPHA_NUMERIC_STRING.charAt(character));
        }
        return builder.toString();
    }

    public static MessageDigest getMessageDigest(String algorithm) throws NoSuchAlgorithmException {
        return MessageDigest.getInstance(algorithm);
    }

    public static Cipher getCipher(String transformation) throws NoSuchPaddingException, NoSuchAlgorithmException {
        return Cipher.getInstance(transformation);
    }

    public static String cipherSha256(final String data) {
        return DigestUtils.sha256Hex(data);
    }

    public static String toStringCSV(Boolean useQuotes, String separator, String... values) {
        StringBuilder csvLine = new StringBuilder();
        int lastValue = values.length - ONE_INT;
        for (int i = ZERO_INT; i < values.length; i++) {
            if (Boolean.TRUE.equals(useQuotes)) {
                csvLine.append(QUOTES);
            }
            csvLine.append(values[i]);
            if (Boolean.TRUE.equals(useQuotes)) {
                csvLine.append(QUOTES);
            }
            if (i < lastValue) {
                csvLine.append(separator);
            }
        }
        return csvLine.append(JUMP_LINE).toString();
    }
}

