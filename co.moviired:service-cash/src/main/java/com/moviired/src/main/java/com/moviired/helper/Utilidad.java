package com.moviired.helper;

import co.moviired.base.domain.StatusCode;
import co.moviired.base.domain.exception.ServiceException;
import co.moviired.base.util.Generator;
import co.moviired.base.util.Security;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moviired.client.mahindra.command.Response;
import com.moviired.excepciones.ManagerException;
import com.moviired.model.request.CashOutRequest;
import com.moviired.model.response.Data;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;


@Slf4j
public final class Utilidad {

    private static final Random RANDOM_GENERATOR = new Random();
    private static final Integer MULTIPLE_NUMBER = 100;
    private static final String LBL_RESPONSE = "Response = ";
    private static final String TRANSACTION_ERROR = "400";

    private Utilidad() {
        super();
    }

    public static boolean isInteger(String numero) {
        try {
            Long.parseLong(numero);
            return true;
        } catch (NumberFormatException e) {
            log.error("Ocurrio un error al intentar formatear el numero: " + e.getMessage(), e);
            return false;
        }
    }

    // Verificar turno de ejecución del JOB actual
    public static boolean validateShift(int ipAddress) {
        // IP + RANDOM + TIMESTAMP
        long shift = ipAddress + RANDOM_GENERATOR.nextInt() + new Date().getTime();
        return (shift % 2 != 0);
    }

    public static String assignCorrelative(String correlation) {
        String correlative = correlation;
        if (correlation == null || correlation.isEmpty()) {
            correlative = String.valueOf(Generator.correlationId());

        }
        MDC.putCloseable("correlation-id", correlative);
        MDC.putCloseable("component", "srv-cash");
        return correlative;
    }

    public static boolean multiplo(int valor) {
        if (valor % MULTIPLE_NUMBER == 0) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    public static String dateFormat(Date fecha) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(fecha);
    }

    public static Date dateFormat(String fecha) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.parse(fecha);
    }

    public static Data generateResponseError(Throwable throwable) {
        Data data = new Data();
        String messageCause = (null == throwable.getCause()) ? throwable.getMessage() : throwable.getCause().getMessage();
        log.error("Error generado : {}", messageCause);

        if (throwable instanceof ServiceException) {
            data.setCode(Constant.TRANSACTION_OK);
            data.setErrorCode(((ServiceException) throwable).getCode());
            data.setErrorMessage(throwable.getMessage());
        } else {
            data.setCode(Constant.TRANSACTION_OK);
            data.setErrorCode(StatusCode.Level.FAIL.value());
            data.setErrorMessage(messageCause != null ? messageCause : "Ocurrio un error procesando la transacción");
        }
        return data;
    }


    public static Data generateErrorResponseCashIn(Throwable throwable) {
        Data data;
        if (throwable instanceof ManagerException) {
            data = generateErrorManagerException((ManagerException) throwable);
        } else {
            data = generateErrorServiceException((Exception) throwable);
        }
        return data;
    }

    private static Data generateErrorManagerException(ManagerException throwable) {
        Data data = new Data();
        log.error(LBL_RESPONSE + throwable.getMessage());
        data.setErrorType(throwable.getTipo() + "");
        data.setErrorCode(throwable.getCodigo());
        data.setCode(throwable.getCodigo());
        data.setErrorMessage(throwable.getMessage());
        return data;
    }

    private static Data generateErrorServiceException(Exception e) {
        Data data = new Data();
        log.error(LBL_RESPONSE + e.getMessage());
        data.setErrorType("1");
        data.setErrorCode("500");
        data.setCode("500");
        data.setErrorMessage(e.getMessage());
        return data;
    }

    public static Data responseError(Response responseMahindra) {
        Data response = new Data();
        response.setErrorType("1");
        response.setErrorCode(responseMahindra.getTxnstatus());
        response.setTransactionId(responseMahindra.getTxnid());
        response.setErrorMessage(responseMahindra.getMessage());
        response.setCode(TRANSACTION_ERROR);
        return response;
    }

    public static void infoRequestAndDataService(CashOutRequest request, Data data) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            if (request != null) {
                log.info("REQUEST SERVICE REST : {}", Security.printIgnore(objectMapper.writeValueAsString(request), "otp", "pin", "agentCode", "userLogin"));
            }

            if (data != null) {
                log.info("RESPONSE SERVICE REST : {}", Security.printIgnore(objectMapper.writeValueAsString(data), "otp", "pin", "agentCode", "userLogin"));
            }

        } catch (JsonProcessingException e) {
            log.error("Error parsing Response or Data in JSON");
        }

    }

}

