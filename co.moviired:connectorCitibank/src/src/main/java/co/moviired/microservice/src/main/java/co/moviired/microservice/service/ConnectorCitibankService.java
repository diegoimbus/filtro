package co.moviired.microservice.service;

import co.moviired.base.domain.StatusCode;
import co.moviired.base.domain.enumeration.ErrorType;
import co.moviired.base.domain.exception.DataException;
import co.moviired.base.domain.exception.ProcessingException;
import co.moviired.base.domain.exception.ServiceException;
import co.moviired.microservice.conf.BankProperties;
import co.moviired.microservice.conf.GlobalProperties;
import co.moviired.microservice.conf.StatusCodeConfig;
import co.moviired.microservice.connection.ConnectionManagement;
import co.moviired.microservice.constants.ConstantNumbers;
import co.moviired.microservice.domain.enums.OperationType;
import co.moviired.microservice.domain.request.Input;
import co.moviired.microservice.domain.request.Request;
import co.moviired.microservice.domain.response.ErrorDetail;
import co.moviired.microservice.domain.response.Outcome;
import co.moviired.microservice.domain.response.Response;
import co.moviired.microservice.provider.IParser;
import co.moviired.microservice.provider.ParserFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@AllArgsConstructor
public class ConnectorCitibankService {

    private final ParserFactory parserFactory;
    private final BankProperties bankProperties;
    private final GlobalProperties globalProperties;
    private final StatusCodeConfig statusCodeConfig;
    private final ConnectionManagement connectionManagement;

    private static final String SEPARATOR = "\\|";
    private static final String SUBSCRIBER = "SUBSCRIBER";

    public Mono<Response> service(Mono<Request> paramRequest, OperationType opType) {
        return paramRequest.flatMap(request -> {
            StatusCode statusCode;
            Response respuesta = null;
            Input parameters = request.getData();
            try {
                Input.validateParameters(parameters, opType, statusCodeConfig);
                if (parameters.getImei().split(SEPARATOR)[ConstantNumbers.LENGTH_3] != null) {
                    setIdentLog(parameters.getImei().split(SEPARATOR)[ConstantNumbers.LENGTH_3]);
                }

                String requestType = (opType.equals(OperationType.PAYMENT)) ? "PAGO" : "CONSULTA";
                log.info("{}", "************ INICIANDO - PROCESO DE " + requestType + " CONNECTOR CITIBANK ************");

                log.info("PETICION INICIAL \n" + new ObjectMapper().writeValueAsString(parameters));

                validateAvailableTransaction(parameters);

                // Transformar la trama a enviar
                IParser parser = parserFactory.getParser(opType);
                Object requestBank = parser.parseRequest(parameters);
                Object responseBank = connectionManagement.sendMessageToProvider(requestBank);
                respuesta = parser.parseResponse(responseBank, parameters);

                // Verificar si se obtuvo respuesta
                if (respuesta == null) {
                    statusCode = statusCodeConfig.of("C01");
                    throw new DataException(statusCode.getCode(), statusCode.getMessage());
                }

            } catch (ServiceException se) {
                log.error("RESPUESTA - ERROR SERVICIO: " + se.toString());
                // Armar el objeto de respuesta con el error específico
                respuesta = generateErrorResponse(se, HttpStatus.PROCESSING);

            } catch (Exception e) {
                log.error("RESPUESTA - ERROR: " + e.getMessage());
                // Armar el objeto de respuesta con el error específico
                ServiceException se = new ProcessingException("-1", e.toString());
                respuesta = generateErrorResponse(se, HttpStatus.INTERNAL_SERVER_ERROR);

            } finally {
                try {
                    log.info("RESPUESTA CONNECTOR CITIBANK \n" + new ObjectMapper().writeValueAsString(respuesta));
                } catch (JsonProcessingException e) {
                    log.error(e.getMessage(), e);
                }
            }
            log.info("{}", "************ FINALIZADO - PROCESO CONNECTOR CITIBANK  ************");
            return Mono.just(respuesta);
        });
    }

    // Valida si la transaccion por el source esta habilitada
    private void validateAvailableTransaction(Input parameters) throws ServiceException {
        StatusCode statusCode;
        String[] shortReference = parameters.getShortReferenceNumber().split(SEPARATOR);
        if (SUBSCRIBER.equalsIgnoreCase(shortReference[ConstantNumbers.LENGTH_3])) {
            if (!bankProperties.isTxAvailableSubscriber()) {
                statusCode = statusCodeConfig.of("C06");
                throw new ServiceException(ErrorType.CONFIGURATION, statusCode.getCode(), statusCode.getMessage());
            }
            parameters.setNetworkExtension(bankProperties.getNetworkExtensionCodeSubscriber());
        } else {
            if (!bankProperties.isTxAvailableChannel()) {
                statusCode = statusCodeConfig.of("C07");
                throw new ServiceException(ErrorType.CONFIGURATION, statusCode.getCode(), statusCode.getMessage());
            }
            parameters.setNetworkExtension(bankProperties.getNetworkExtensionCodeChannel());
        }
    }

    // Generar respuesta errada, por error del servicio
    private Response generateErrorResponse(ServiceException e, HttpStatus httpStatus) {
        ErrorDetail error = new ErrorDetail(e.getErrorType().ordinal(), e.getCode(), e.getMessage());
        Outcome outcome = new Outcome(httpStatus, error);
        Response response = new Response();
        response.setOutcome(outcome);
        return response;
    }

    // Enviar datos de log
    private void setIdentLog(String correlationId) {
        MDC.putCloseable("component", globalProperties.getApplicationName());
        MDC.putCloseable("correlation-id", correlationId);
    }

}

