package co.movii.auth.server.helper;

import co.movii.auth.server.conf.StatusCodeConfig;
import co.movii.auth.server.domain.dto.Response;
import co.movii.auth.server.domain.dto.User;
import co.movii.auth.server.providers.IResponse;
import co.movii.auth.server.providers.mahindra.response.CommandResponse;
import co.moviired.base.domain.StatusCode;
import co.moviired.base.domain.enumeration.ErrorType;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Slf4j
@Component
public final class UtilHelper implements Serializable {

    public static final String SUBSCRIBER_SOURCE = "SUBSCRIBER";
    public static final String DATE_FORMAT_DOB_LOGIN_MAHINDRA = "yyyy-MM-dd HH:mm:ss.S";
    public static final String DATE_FORMAT_DOB_QUERY_USER_INFO_MAHINDRA = "ddMMyyyy";
    public static final int CORRELATION_SIZE = 32;

    private UtilHelper() {
    }

    public static String generateCorrelationId() {
        String result = java.util.UUID.randomUUID().toString();

        result = result.replace("-", "");
        result = result.substring(0, CORRELATION_SIZE);
        return result;
    }

    public static  String asignarCorrelativo(String correlation) {
        String cId = correlation;

        if (correlation == null || correlation.isEmpty()) {
            cId = UtilHelper.generateCorrelationId();
        }

        MDC.putCloseable("correlation-id", cId);
        MDC.putCloseable("component", "authentication");
        return cId;
    }



    public final Response parseResponse(@NotNull IResponse pcommand, StatusCodeConfig statusCodeConfig) {
        // Transformar al command específico
        CommandResponse command = (CommandResponse) pcommand;
        // Armar el objeto respuesta
        Response response = new Response();
        StatusCode statusCode = statusCodeConfig.of(command.getTxnstatus());
        response.setErrorCode(command.getTxnstatus());
        if (StatusCode.Level.SUCCESS.equals(statusCode.getLevel())) {
            response.setErrorType("");
            response.setErrorMessage("OK");
            response.setErrorCode(StatusCode.Level.SUCCESS.value());
        } else {
            response.setErrorType(ErrorType.PROCESSING.name());
            response.setErrorMessage(statusCode.getMessage());
        }
        return response;
    }


    public final Response parseResponse(@NotNull String errorCode, StatusCodeConfig statusCodeConfig) {
        // Armar el objeto respuesta
        Response response = new Response();
        StatusCode statusCode = statusCodeConfig.of(errorCode);

        response.setErrorCode(errorCode);

        if (StatusCode.Level.SUCCESS.equals(statusCode.getLevel())) {
            response.setErrorType("");
            response.setErrorMessage("OK");
            response.setErrorCode(StatusCode.Level.SUCCESS.value());
        } else {
            response.setErrorType(ErrorType.PROCESSING.name());
            response.setErrorMessage(statusCode.getMessage());
        }

        return response;
    }

    public final Response addUser(Response presponse, String codeError, StatusCodeConfig statusCodeConfig, User user) {
        Response response = presponse;
        if (StatusCode.Level.SUCCESS.equals(statusCodeConfig.of(codeError).getLevel())) {
            response.setUser(user);
        }
        return  response;
    }

}

