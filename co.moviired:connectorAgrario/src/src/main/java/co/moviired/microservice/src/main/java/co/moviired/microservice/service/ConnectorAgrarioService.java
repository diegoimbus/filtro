package co.moviired.microservice.service;

import co.moviired.base.domain.StatusCode;
import co.moviired.base.domain.enumeration.ErrorType;
import co.moviired.base.domain.exception.ServiceException;
import co.moviired.microservice.conf.BASE24TCPCERPChannel;
import co.moviired.microservice.conf.BankProperties;
import co.moviired.microservice.conf.GlobalProperties;
import co.moviired.microservice.conf.StatusCodeConfig;
import co.moviired.microservice.constants.ConstantNumbers;
import co.moviired.microservice.domain.enums.OperationType;
import co.moviired.microservice.domain.request.Input;
import co.moviired.microservice.domain.request.Request;
import co.moviired.microservice.domain.response.ErrorDetail;
import co.moviired.microservice.domain.response.Outcome;
import co.moviired.microservice.domain.response.Response;
import co.moviired.microservice.provider.IParser;
import co.moviired.microservice.provider.ParserFactory;
import co.moviired.microservice.repository.BankingAgrarioRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.packager.GenericPackager;
import org.slf4j.MDC;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.net.SocketException;

@Slf4j
@Service
public class ConnectorAgrarioService {

    private static final String IMEI_SEPARATOR = "\\|";
    private static final String SUBSCRIBER = "SUBSCRIBER";
    private final ParserFactory parserFactory;
    private final BankProperties bankProperties;
    private final StatusCodeConfig statusCodeConfig;
    private final GlobalProperties globalProperties;
    private final BankingAgrarioRepository bankingAgrarioRepository;
    private final GenericPackager packagerAgrario;

    public ConnectorAgrarioService(ParserFactory parserFactory, BankProperties bankProperties, GlobalProperties globalProperties,
                                   StatusCodeConfig statusCodeConfig, BankingAgrarioRepository bankingAgrarioRepository) throws ISOException, IOException {
        super();
        this.parserFactory = parserFactory;
        this.bankProperties = bankProperties;
        this.statusCodeConfig = statusCodeConfig;
        this.globalProperties = globalProperties;
        this.bankingAgrarioRepository = bankingAgrarioRepository;
        // Cargar la configuración del ISO-Agrario
        packagerAgrario = new GenericPackager(new ClassPathResource("iso8583/base24_agrario.xml").getInputStream());
        log.debug("CONFIGURACION DEL ARCHIVO ISO - EXITOSA");
    }

    public Mono<Response> service(@NotNull Mono<Request> wsRequest, OperationType opType) {
        return wsRequest.flatMap(request -> {
            Response respuesta = null;
            Input parameters = request.getData();
            try {
                String requestType = (opType.equals(OperationType.PAYMENT)) ? "PAGO" : "CONSULTA";
                log.info("{}", "************ INICIANDO - PROCESO DE " + requestType + " CONNECTOR AGRARIO ************");

                Input.validateParameters(request.getData(), opType, statusCodeConfig);
                if (parameters.getImei().split(IMEI_SEPARATOR)[ConstantNumbers.LENGTH_3] != null) {
                    setIdentLog(parameters.getImei().split(IMEI_SEPARATOR)[ConstantNumbers.LENGTH_3]);
                }

                log.info("PETICION INICIAL \n" + new ObjectMapper().writeValueAsString(parameters));

                parameters.setImei(validateTercIdImei(parameters));

                // Transformar la trama a enviar
                IParser parser = parserFactory.getParser(opType);
                ISOMsg sendMesg = parser.parseRequest(parameters, packagerAgrario);

                // Procesar la respuesta del operador
                if (opType.equals(OperationType.PAYMENT)) {
                    ISOMsg respOper = sendMessageToAgrario(sendMesg);
                    respuesta = parser.parseResponse(respOper, parameters);
                } else {
                    bankingAgrarioRepository.logISOMsg(sendMesg);
                    respuesta = parser.parseResponseQuery(parameters, opType);
                }

            } catch (ServiceException se) {
                log.error("RESPUESTA - ERROR SERVICIO: " + se.toString());
                // Armar el objeto de respuesta con el error específico
                respuesta = generateErrorResponse(se, HttpStatus.NOT_ACCEPTABLE);

            } catch (Exception e) {
                log.error("RESPUESTA - ERROR: " + e.getMessage());
                // Armar el objeto de respuesta con el error específico
                ServiceException se = new ServiceException(ErrorType.PROCESSING, "500", e.toString());
                respuesta = generateErrorResponse(se, HttpStatus.INTERNAL_SERVER_ERROR);

            } finally {
                try {
                    log.info("RESPUESTA CONNECTOR AGRARIO \n" + new ObjectMapper().writeValueAsString(respuesta));
                } catch (JsonProcessingException e) {
                    log.error(e.getMessage());
                }
            }
            log.info("{}", "************ FINALIZADO - PROCESO CONNECTOR AGRARIO  ************");
            return Mono.just(respuesta);
        });
    }

    // Valida el valor del tercId para la transaccion
    private String validateTercIdImei(Input parameters) throws ServiceException {
        StatusCode statusCode;
        StringBuilder imeiUpdated = new StringBuilder();
        String[] imeiParts = parameters.getImei().concat(" ").split(IMEI_SEPARATOR);
        String[] shortReference = parameters.getShortReferenceNumber().split(IMEI_SEPARATOR);
        boolean isSubscriber = SUBSCRIBER.equalsIgnoreCase(shortReference[ConstantNumbers.LENGTH_3]);

        if (isSubscriber) {
            if (!bankProperties.isMoviiTransactions()) {
                statusCode = statusCodeConfig.of("C06");
                throw new ServiceException(ErrorType.CONFIGURATION, statusCode.getCode(), statusCode.getMessage());
            }
            imeiParts[ConstantNumbers.LENGTH_10] = bankProperties.getTercIdSubscriber();
            imeiParts[ConstantNumbers.LENGTH_11] = bankProperties.getHomologateBankSubscriber();

        } else if (imeiParts[ConstantNumbers.LENGTH_10].trim().equals("") || imeiParts[ConstantNumbers.LENGTH_11].trim().equals("")) {
            if (!bankProperties.isTxWithoutHomologation()) {
                statusCode = statusCodeConfig.of("C07");
                throw new ServiceException(ErrorType.CONFIGURATION, statusCode.getCode(), statusCode.getMessage());
            }
            imeiParts[ConstantNumbers.LENGTH_10] = bankProperties.getTercIdChannel();
            imeiParts[ConstantNumbers.LENGTH_11] = bankProperties.getHomologateBankChannel();

        } else {
            return parameters.getImei();
        }

        for (String part : imeiParts) {
            imeiUpdated.append(part);
            imeiUpdated.append("|");
        }
        imeiUpdated.deleteCharAt(imeiUpdated.lastIndexOf("|"));
        imeiUpdated.trimToSize();

        return imeiUpdated.toString();
    }

    // Enviar las tramas ISO al banco agrario directo
    private ISOMsg sendMessageToAgrario(ISOMsg sendMesg) throws ISOException, IOException {
        ISOMsg respOper;
        BASE24TCPCERPChannel connectAgrario;

        // A. Conectar al banco agrario
        log.info("Conectandose al Banco Agrario con la IP: " + bankProperties.getAgrarioIp() + " y puerto: " + bankProperties.getAgrarioPort());
        Integer connectionTimeout = bankProperties.getAgrarioConnectionTimeout();
        long initSeconds = System.currentTimeMillis();
        connectAgrario = bankingAgrarioRepository.openSockeConnection(packagerAgrario, bankProperties.getAgrarioIp(), Integer.parseInt(bankProperties.getAgrarioPort()), connectionTimeout);
        long timeConnection = System.currentTimeMillis() - initSeconds;

        // B. Verificar si supero el tiempo maximo de conexion
        if (timeConnection > connectionTimeout) {
            // Forzar el Timeout, por tiempo de comunicación soprepasado
            throw new SocketException("Tiempo de conexión superado para enviar la transacción...[BANCO AGRARIO]");
        }

        log.info("Conexion exitosa al Banco Agrario");
        log.info("Tiempo empleado en crear la conexion para la comunicación con Banco Agrario = " + timeConnection + " [Tiempo maximo configurado = " + connectionTimeout + " ].");

        // C. Transmitir el mensaje
        log.info("Enviando transaccion a Banco Agrario...");
        bankingAgrarioRepository.sendRequest(connectAgrario, sendMesg);

        // D. Obtener la respuesta y desconectar
        log.info("Leyendo respuesta de Banco Agrario...");
        respOper = bankingAgrarioRepository.getResponse(connectAgrario);
        connectAgrario.disconnect();

        return respOper;
    }

    // Generar respuesta errada, por error del servicio
    private Response generateErrorResponse(ServiceException e, HttpStatus httpStatus) {
        ErrorDetail error = new ErrorDetail(e.getErrorType().ordinal(), e.getCode(), e.getMessage());
        Outcome outcome = new Outcome(httpStatus, error);
        Response response = new Response();
        response.setOutcome(outcome);
        return response;
    }

    // Enviar valores de variables a imprimir en log
    private void setIdentLog(String correlationId) {
        MDC.putCloseable("component", this.globalProperties.getApplicationName());
        MDC.putCloseable("correlation-id", correlationId);
    }

}
