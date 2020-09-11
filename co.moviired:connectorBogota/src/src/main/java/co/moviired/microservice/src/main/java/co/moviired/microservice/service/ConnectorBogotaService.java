package co.moviired.microservice.service;

import co.moviired.base.domain.enumeration.ErrorType;
import co.moviired.base.domain.exception.*;
import co.moviired.microservice.conf.BankProperties;
import co.moviired.microservice.conf.GlobalProperties;
import co.moviired.microservice.constants.ConstantNumbers;
import co.moviired.microservice.domain.enums.OperationType;
import co.moviired.microservice.domain.request.Input;
import co.moviired.microservice.domain.request.Request;
import co.moviired.microservice.domain.response.ErrorDetail;
import co.moviired.microservice.domain.response.Outcome;
import co.moviired.microservice.domain.response.Response;
import co.moviired.microservice.provider.IParser;
import co.moviired.microservice.provider.ParserFactory;
import co.moviired.microservice.repository.BankingBogotaRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.channel.BASE24Channel;
import org.jpos.iso.packager.GenericPackager;
import org.slf4j.MDC;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;
import java.io.IOException;

@Slf4j
@Service
public class ConnectorBogotaService {

    private final ParserFactory parserFactory;
    private final GlobalProperties globalProperties;
    private final BankProperties bankProperties;
    private final BankingBogotaRepository bankingBogotaRepository;
    private final GenericPackager packagerBogota;
    private static final String IMEI_SEPARATOR = "\\|";
    private static final String SUBSCRIBER = "SUBSCRIBER";

    public ConnectorBogotaService(ParserFactory parserFactory, GlobalProperties globalProperties, BankProperties bankProperties,
                                  BankingBogotaRepository bankingBogotaRepository) throws ISOException, IOException {
        super();
        this.parserFactory = parserFactory;
        this.globalProperties = globalProperties;
        this.bankProperties = bankProperties;
        this.bankingBogotaRepository = bankingBogotaRepository;
        // Cargar la configuración del ISO-Bogota
        packagerBogota = new GenericPackager(new ClassPathResource("iso8583/base24_bogota.xml").getInputStream());
        log.debug("{} {} {}", "Configuración del servicio: ", ConnectorBogotaService.class.getName(), " - EXITOSA");
    }

    public Mono<Response> service(@NotNull Mono<Request> wsRequest, OperationType opType) {

        return wsRequest.flatMap(request -> {
            Response respuesta = null;
            Input parameters = request.getData();

            try {

                String requestType = (opType.equals(OperationType.QUERY)) ? "CONSULTA" : "PAGO";
                log.info("************ INICIANDO - PROCESO DE " + requestType + " CONNECTOR BOGOTA ************");

                validateParameters(parameters, opType);
                if (parameters.getImei().split(IMEI_SEPARATOR)[ConstantNumbers.LENGTH_3] != null) {
                    setIdentLog(parameters.getImei().split(IMEI_SEPARATOR)[ConstantNumbers.LENGTH_3]);
                }

                log.info("PETICION INICIAL \n" + new ObjectMapper().writeValueAsString(parameters));

                parameters.setImei(validateTercIdImei(parameters));

                IParser parser = parserFactory.getParser(opType);
                ISOMsg sendMesg = parser.parseRequest(parameters, packagerBogota);

                // Procesar la respuesta del operador
                ISOMsg respOper = sendMessageToBogota(sendMesg);
                respuesta = parser.parseResponse(respOper, parameters);

                // Verificar si se obtuvo respuesta
                if (respuesta == null) {
                    throw new ServiceException(ErrorType.DATA, "99", "NO SE OBTUVO RESPUESTA");
                }

            } catch (ServiceException se) {
                log.error("RESPUESTA - EXCEPCION SERVICIO: " + se.toString());
                // Armar el objeto de respuesta con el error específico
                respuesta = generateErrorResponse(se, HttpStatus.NOT_ACCEPTABLE);

            } catch (Exception e) {
                log.error("RESPUESTA - ERROR: " + e.getMessage());
                // Armar el objeto de respuesta con el error específico
                ServiceException se = new ServiceException(ErrorType.PROCESSING, "500", e.toString());
                respuesta = generateErrorResponse(se, HttpStatus.INTERNAL_SERVER_ERROR);

            } finally {
                if (respuesta != null) {
                    try {
                        log.info("RESPUESTA CONNECTOR BOGOTA \n" + new ObjectMapper().writeValueAsString(respuesta));
                    } catch (JsonProcessingException e) {
                        log.error(e.getMessage(), e);
                    }
                }
            }
            log.info("{}", "************ FINALIZADO - PROCESO CONNECTOR BOGOTA  ************");
            return Mono.just(respuesta);
        });
    }

    // Valida el valor del tercId para la transaccion
    private String validateTercIdImei(Input parameters) throws ServiceException {
        StringBuilder imeiUpdated = new StringBuilder();
        String[] imeiParts = parameters.getImei().concat(" ").split(IMEI_SEPARATOR);
        String[] shortReference = parameters.getShortReferenceNumber().split(IMEI_SEPARATOR);
        boolean isSubscriber = SUBSCRIBER.equalsIgnoreCase(shortReference[ConstantNumbers.LENGTH_3]);

        if (isSubscriber) {
            // Rechazar las transacciones de SUBSCRIBERS
            if (!bankProperties.isTxSubscriber()) {
                throw new ServiceException(ErrorType.CONFIGURATION, "403", bankProperties.getErrorMessageSubscriberTx());
            }
            // Si la transacción es de SUBSCRIBERS se asgina el tercId definido
            imeiParts[ConstantNumbers.LENGTH_10] = bankProperties.getTercIdSubscriber();

        } else if (imeiParts[ConstantNumbers.LENGTH_10].trim().equals("")) {
            // Si la transaccion es de CHANNEL y no tiene tercId se asgina el tercId definido
            imeiParts[ConstantNumbers.LENGTH_10] = bankProperties.getTercIdChannel();

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

    // Envia las tramas ISO a Banco bogota directo
    private ISOMsg sendMessageToBogota(ISOMsg sendMesg) throws CommunicationException, ProcessingException {
        long createComIn;
        long createComFin;
        ISOMsg respOper;
        BASE24Channel connectBogota;

        log.info("Conectándose al banco bogota con la IP: " + this.bankProperties.getBogotaIP() + " y puerto: " + this.bankProperties.getBogotaPort());
        Integer connectionTimeout = bankProperties.getBogotaTimeoutConnection();
        createComIn = System.currentTimeMillis();
        connectBogota = bankingBogotaRepository.openSockeConnection(packagerBogota, bankProperties.getBogotaIP(), Integer.parseInt(bankProperties.getBogotaPort()), connectionTimeout);
        createComFin = System.currentTimeMillis() - createComIn;

        // Verificar si se superó el tiempo máximo de conexión
        if (createComFin > connectionTimeout) {
            throw new CommunicationException("-1", "Tiempo de conexión superado para enviar la transacción...[BANCO BOGOTA]");
        }

        log.info("Conexión exitosa al BANCO BOGOTA.");
        log.info("Tiempo empleado en crear la conexión para la comunicación con BANCO BOGOTA = " + createComFin + " [Tiempo máximo Configurado = " + connectionTimeout + " ].");

        log.info("Enviando transacción a BOGOTA");
        bankingBogotaRepository.sendRequest(connectBogota, sendMesg);

        log.info("Leyendo respuesta de BANCO BOGOTA");
        respOper = bankingBogotaRepository.getResponse(connectBogota);

        return respOper;
    }

    // Valida que los parametros de data no se encuentren nulos o vacios
    private void validateParameters(Input parameters, OperationType opType) throws DataException {
        if (parameters.getShortReferenceNumber() == null || parameters.getShortReferenceNumber().isBlank())
            throw new DataException("-2", "El shortReferenceNumber es un parámetro obligatorio");

        if (parameters.getImei() == null || parameters.getImei().isBlank())
            throw new DataException("-2", "El imei es un parámetro obligatorio");

        if (parameters.getLastName() == null)
            throw new DataException("-2", "El lastName es un parámetro obligatorio");

        if (opType.equals(OperationType.PAY_BILL)) {
            if (parameters.getValueToPay() == null || parameters.getValueToPay().isBlank())
                throw new DataException("-2", "El valueToPay es un parámetro obligatorio");

            if (parameters.getEchoData() == null || parameters.getEchoData().isBlank())
                throw new DataException("-2", "El echoData es un parámetro obligatorio");

            parameters.castValueToAmount();
        }
    }

    // Generar respuesta errada, por error del servicio
    private Response generateErrorResponse(ServiceException e, HttpStatus httpStatus) {
        ErrorDetail error = new ErrorDetail(e.getMessage(), e.getErrorType().ordinal(), e.getCode());
        Outcome outcome = new Outcome(httpStatus, error);
        Response response = new Response();
        response.setOutcome(outcome);
        return response;
    }

    // Envia los valores a imprimir en el log
    private void setIdentLog(String correlationId) {
        MDC.putCloseable("component", this.globalProperties.getApplicationName());
        MDC.putCloseable("correlation-id", correlationId);
    }

}

