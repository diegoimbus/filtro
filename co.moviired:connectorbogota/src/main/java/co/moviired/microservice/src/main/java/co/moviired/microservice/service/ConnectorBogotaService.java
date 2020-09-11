package co.moviired.microservice.service;

import co.moviired.base.domain.enumeration.ErrorType;
import co.moviired.base.domain.exception.DataException;
import co.moviired.base.domain.exception.ServiceException;
import co.moviired.microservice.conf.BankProductsProperties;
import co.moviired.microservice.conf.GlobalProperties;
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
import java.net.SocketException;

/*
 * Copyright @2019. Movii, SAS. Todos los derechos reservados.
 *
 * @author Cindy Bejarano, Oscar Lopez
 * @since 1.0.0
 */

@Service
@Slf4j
public class ConnectorBogotaService {

    private static final String MOVII = "SUBSCRIBER";
    private static final String IMEI_SEPARATOR = "\\|";
    private static final Integer IMEI_SOURCE_POSITION = 2;

    private final GenericPackager packagerBogota;
    private final ParserFactory parserFactory;
    private final BankingBogotaRepository bankingBogotaRepository;
    private final GlobalProperties globalProperties;
    private final BankProductsProperties bankProductsProperties;

    public ConnectorBogotaService(@NotNull ParserFactory pparserFactory,
                                  @NotNull GlobalProperties pglobalPropertie,
                                  @NotNull BankProductsProperties pbankProductsProperties,
                                  @NotNull BankingBogotaRepository pbankingBogotaRepository) throws ISOException, IOException {
        super();
        log.debug("{} {} {}", "Configuración del servicio: ", ConnectorBogotaService.class.getName(), " - INICIADA");
        this.globalProperties = pglobalPropertie;

        // Cargar la configuracion y errores desde el prorties
        this.parserFactory = pparserFactory;
        this.bankProductsProperties = pbankProductsProperties;
        this.bankingBogotaRepository = pbankingBogotaRepository;

        // Cargar la configuración del ISO-Bogota
        packagerBogota = new GenericPackager(new ClassPathResource("iso8583/base24_bogota.xml").getInputStream());
        log.debug("{} {} {}", "Configuración del servicio: ", ConnectorBogotaService.class.getName(), " - EXITOSA");
    }

    Mono<Response> service(@NotNull Mono<Request> wsRequest, OperationType opType) {
        return wsRequest.flatMap(request -> {
            Input parameters;
            Response respuesta = null;
            try {
                String requestType = (opType.equals(OperationType.QUERY)) ? "CONSULTA" : "PAGO";
                log.info("************ INICIANDO - PROCESO DE " + requestType + " CONNECTOR BOGOTA ************");

                parameters = Input.parseInput(request.getData());
                if (parameters.getImei() == null) {
                    throw new DataException("-2", "El imei es un parámetro obligatorio");
                } else if (!parameters.getImei().split(IMEI_SEPARATOR)[3].trim().isEmpty()) {
                    setIdentLog(parameters.getImei().split(IMEI_SEPARATOR)[3]);
                }

                log.info("PETICION INICIAL \n" + new ObjectMapper().writeValueAsString(parameters));

                // Rechazar las transacciones de MOVII
                if (!bankProductsProperties.getMoviiTransactions().equalsIgnoreCase("true")) {
                    String[] imeiParts = parameters.getImei().split(IMEI_SEPARATOR);
                    if ((imeiParts.length >= IMEI_SOURCE_POSITION) && (MOVII.equalsIgnoreCase(imeiParts[IMEI_SOURCE_POSITION]))) {
                        throw new ServiceException(ErrorType.CONFIGURATION, "403", bankProductsProperties.getErrorMessageMoviiTx());
                    }
                }

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
                log.error("RESPONSE - SERVICIO: " + se.toString());
                // Armar el objeto de respuesta con el error específico
                respuesta = generateErrorResponse(se, HttpStatus.NOT_ACCEPTABLE);

            } catch (Exception e) {
                log.error("REST RESPONSE - ERROR: " + e.getMessage());
                // Armar el objeto de respuesta con el error específico
                ServiceException se = new ServiceException(ErrorType.PROCESSING, "500", e.toString());
                respuesta = generateErrorResponse(se, HttpStatus.INTERNAL_SERVER_ERROR);

            } finally {
                if (respuesta != null) {
                    try {
                        log.info("RESPONSE CONNECTOR BOGOTA \n" + new ObjectMapper().writeValueAsString(respuesta));
                    } catch (JsonProcessingException e) {
                        log.error(e.getMessage(), e);
                    }
                }
            }
            log.info("{}", "************ FINALIZADO - PROCESO CONNECTOR BOGOTA  ************");
            return Mono.just(respuesta);
        });
    }

    //SendMessage a Banco bogota directo
    private ISOMsg sendMessageToBogota(ISOMsg sendMesg) throws ISOException, IOException {
        BASE24Channel connectBogota;
        long createComIn;
        long createComFin;
        ISOMsg respOper;

        // B. CONECTAR AL BANCO BOGOTA
        log.info("Conectándose al banco bogota con la IP: " + this.bankProductsProperties.getBogotaIP() + " y puerto: " + this.bankProductsProperties.getBogotaPort());
        Integer connectionTimeout = this.bankProductsProperties.getBogotaTimeout();
        createComIn = System.currentTimeMillis();
        connectBogota = this.bankingBogotaRepository.openSockeConnection(packagerBogota, this.bankProductsProperties.getBogotaIP(), Integer.parseInt(this.bankProductsProperties.getBogotaPort()), connectionTimeout);
        createComFin = System.currentTimeMillis() - createComIn;

        // Verificar si se superó el tiempo máximo de conexión
        if (createComFin > this.bankProductsProperties.getBogotaTimeout()) {
            // Forzar el Timeout, por tiempo de comunicación soprepasado
            throw new SocketException("Tiempo de conexión superado para enviar la transacción...[BANCO BOGOTA]");
        }

        // Conexión exitosa
        log.info("Conexión exitosa al BANCO BOGOTA.");
        log.info("Tiempo empleado en crear la conexión para la comunicación con BANCO BOGOTA = " + createComFin + " [Tiempo máximo Configurado = " + connectionTimeout + " ].");

        // C. TRANSMITIR EL MENSAJE
        log.info("Enviando transacción a BOGOTA");
        this.bankingBogotaRepository.sendRequest(connectBogota, sendMesg);
        // D. OBTENER LA RESPUESTA
        log.info("Leyendo respuesta de BANCO BOGOTA");

        respOper = this.bankingBogotaRepository.getResponse(connectBogota);
        connectBogota.disconnect();

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

    private void setIdentLog(String correlationId) {
        MDC.putCloseable("component", this.globalProperties.getApplicationName());
        MDC.putCloseable("correlation-id", correlationId);
    }

}

