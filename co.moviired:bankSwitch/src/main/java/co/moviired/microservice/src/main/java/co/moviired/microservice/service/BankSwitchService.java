package co.moviired.microservice.service;
/*
 * Copyright @2019. Movii, SAS. Todos los derechos reservados.
 *
 * @author Bejarano, Cindy
 * @version 1, 2019
 * @since 1.0
 */


import co.moviired.microservice.conf.BankProductsProperties;
import co.moviired.microservice.conf.GlobalProperties;
import co.moviired.microservice.conf.SwitchProperties;
import co.moviired.microservice.domain.enums.ErrorType;
import co.moviired.microservice.domain.enums.OperationType;
import co.moviired.microservice.domain.request.Input;
import co.moviired.microservice.domain.request.Request;
import co.moviired.microservice.domain.response.ErrorDetail;
import co.moviired.microservice.domain.response.Outcome;
import co.moviired.microservice.domain.response.Response;
import co.moviired.microservice.exception.ServiceException;
import co.moviired.microservice.provider.IParser;
import co.moviired.microservice.provider.ParserFactory;
import co.moviired.microservice.repository.BankingSwitchRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.channel.ASCIIChannel;
import org.jpos.iso.packager.GenericPackager;
import org.slf4j.MDC;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.Serializable;
import java.net.SocketException;

@Service
@Slf4j
public class BankSwitchService implements Serializable {

    private static final long serialVersionUID = 6188499835234843887L;

    // Constants
    private static final String FORMATTED_LOG_4 = "{} {} {} {}";
    private static final String NO_RESPUESTA = "No se obtuvo respuesta desde el SWITCH";

    // Repositories and Services
    private final GenericPackager packager;
    private final SwitchProperties config;
    private final ParserFactory parserFactory;
    private final BankingSwitchRepository bankingMicroerviceRepository;
    private final BankProductsProperties bankProducts;
    private final GlobalProperties globalProperties;
    private String enviandoTransaccion = "Enviando transacción a SWITCH";


    public BankSwitchService(BankingSwitchRepository bankingMicroerviceRepository, SwitchProperties config, ParserFactory pparserFactory, BankProductsProperties pbankProducts, GlobalProperties pglobalProperties) throws ISOException, IOException {
        super();
        log.debug("{} {} {}", "Configuración del servicio: ", BankSwitchService.class.getName(), " - INICIADA");
        this.bankingMicroerviceRepository = bankingMicroerviceRepository;
        this.parserFactory = pparserFactory;
        this.bankProducts = pbankProducts;
        this.globalProperties = pglobalProperties;

        // Cargar la configuracion y errores desde el prorties
        this.config = config;

        // Cargar la configuración del ISO-Message
        packager = new GenericPackager(new ClassPathResource("iso8583/iso-message.xml").getInputStream());
        log.debug("{} {} {}", "Configuración del servicio: ", BankSwitchService.class.getName(), " - EXITOSA");
    }

    // Generar respuesta errada, por error del servicio
    private Response generateErrorResponse(ServiceException e, HttpStatus httpStatus) {
        ErrorDetail error = new ErrorDetail(e.getTipo(), e.getCodigo(), e.getMensaje());
        Outcome outcome = new Outcome(httpStatus, error);

        Response response = new Response();
        response.setOutcome(outcome);
        return response;
    }

    Mono<Response> service(@NotNull Mono<Request> wsRequest, OperationType opType) {
        return wsRequest.flatMap(request -> {
            Input parameters;
            Response respuesta = null;
            String correlationId = null;
            String requestType = "";
            try {

                if (opType.equals(OperationType.CASH_OUT)) {
                    requestType = "RETIRO";
                } else if (opType.equals(OperationType.QUERY)) {
                    requestType = "CONSULTA";
                }

                String logIni = " ************ STARTED - PROCESS BANKINGSWITCH " + requestType + "  ************";

                parameters = Input.parseInput(request.getData());

                if (bankProducts.getProductIdAgrarioPayObligations().equals(parameters.getServiceCode())) {
                    logIni = " ************ STARTED - PROCESS BANKINGSWITCH PAGO DE OBLIGACIONES   ************";
                    requestType = "PAGO DE OBLIGACIONES";
                } else if (bankProducts.getProductIdAgrarioDeposit().equals(parameters.getServiceCode())) {

                    logIni = " ************ STARTED - PROCESS BANKINGSWITCH DEPOSITO   ************";
                    requestType = "DEPOSITO";
                }

                log.info(logIni);

                correlationId = parameters.getCorrelationId();
                setVariablesLog(correlationId);

                log.info("{} {} {} {} ", "[", correlationId, "] INITIAL REQUEST \n", new ObjectMapper().writeValueAsString(parameters));

                IParser parser = parserFactory.getParser(opType);
                ISOMsg sendMesg = parser.parseRequest(parameters, config, packager);

                ISOMsg respOper;

                respOper = sendMessageToSwitch(sendMesg, correlationId);

                // Procesar la respuesta del operador
                respuesta = parser.parseResponse(respOper, parameters);

                // Verificar si se obtuvo respuesta
                if (respuesta == null) {
                    throw new ServiceException(ErrorType.DATA, "99", NO_RESPUESTA);
                }
            } catch (ServiceException se) {
                log.error(FORMATTED_LOG_4, "[", correlationId, "] RESPONSE - SERVICIO: ", se.toString());

                // Armar el objeto de respuesta con el error específico
                respuesta = generateErrorResponse(se, HttpStatus.INTERNAL_SERVER_ERROR);

            } catch (Exception e) {
                log.error(FORMATTED_LOG_4, "[", correlationId, "] Rest RESPONSE - ERROR: ", e.getMessage());

                // Armar el objeto de respuesta con el error específico
                ServiceException se = new ServiceException(ErrorType.PROCESSING, "500", e.toString());
                respuesta = generateErrorResponse(se, HttpStatus.INTERNAL_SERVER_ERROR);

            } finally {
                if (respuesta != null) {
                    try {
                        log.info("{} {} {} {} {}", "[", correlationId, "]", "RESPONSE BankingSwitch \n", new ObjectMapper().writeValueAsString(respuesta));
                    } catch (JsonProcessingException e) {
                        log.error(e.getMessage(), e);
                    }
                }

                log.info("{}", "************FINISHED - PROCESS BANKINGSWITCH " + requestType + " ************");
            }


            return Mono.just(respuesta);
        });
    }


    private ISOMsg sendMessageToSwitch(ISOMsg sendMesg, String correlationId) throws ISOException, IOException {


        ASCIIChannel connectSwitch;
        long createComIn;
        long createComFin;
        ISOMsg respOper = null;

        // B. CONECTAR AL SWITCH
        log.info("{} {} {} {} {} {} {}", "[", correlationId, "]", "Conectándose al switch con la IP: ", this.config.getSocketIP(), " y puerto: ", this.config.getSocketPuerto());
        Integer connectionTimeout = this.config.getPeticionTimeout();
        createComIn = System.currentTimeMillis();
        connectSwitch = this.bankingMicroerviceRepository.openSockeConnection(packager, this.config.getSocketIP(), this.config.getSocketPuerto(), connectionTimeout);
        createComFin = System.currentTimeMillis() - createComIn;

        // Verificar si se superó el tiempo máximo de conexión
        if (createComFin > this.config.getConexionTimeout()) {
            // Forzar el Timeout, por tiempo de comunicación soprepasado
            this.bankingMicroerviceRepository.closeSocketConnection(connectSwitch);
            throw new SocketException("[" + correlationId + "]" + "Tiempo de conexión superado para enviar la transacción...[SWITCH]");
        }

        // Conexión exitosa
        log.info(FORMATTED_LOG_4, "[", correlationId, "]", "Conexión exitosa al switch.");
        log.info("{} {} {} {} {} {} {} {}", "[", correlationId, "]", "Tiempo empleado en crear la conexión para la comunicación con SWITCH = ", createComFin, " [Tiempo máximo Configurado = ", connectionTimeout, " ].");

        // C. TRANSMITIR EL MENSAJE
        log.info(FORMATTED_LOG_4, "[", correlationId, "]", enviandoTransaccion);
        this.bankingMicroerviceRepository.sendRequest(connectSwitch, sendMesg, correlationId);
        // D. OBTENER LA RESPUESTA
        log.info(FORMATTED_LOG_4, "[", correlationId, "]", "Leyendo respuesta de SWITCH");

        respOper = this.bankingMicroerviceRepository.getResponse(connectSwitch, correlationId);

        this.bankingMicroerviceRepository.closeSocketConnection(connectSwitch);
        return respOper;
    }

    private void setVariablesLog(String correlationId) {
        MDC.putCloseable("component", this.globalProperties.getApplicationName());
        MDC.putCloseable("correlation-id", correlationId);
    }

}

