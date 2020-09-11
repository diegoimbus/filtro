package co.moviired.digitalcontent.incomm.service;

import co.moviired.base.domain.enumeration.ErrorType;
import co.moviired.base.domain.exception.CommunicationException;
import co.moviired.base.domain.exception.DataException;
import co.moviired.base.domain.exception.ProcessingException;
import co.moviired.base.util.Security;
import co.moviired.connector.helper.ISOMsgHelper;
import co.moviired.digitalcontent.incomm.helper.BuildTxnHelper;
import co.moviired.digitalcontent.incomm.helper.ErrorHelper;
import co.moviired.digitalcontent.incomm.helper.Utilidades;
import co.moviired.digitalcontent.incomm.model.request.Input;
import co.moviired.digitalcontent.incomm.model.request.Request;
import co.moviired.digitalcontent.incomm.model.response.Response;
import co.moviired.digitalcontent.incomm.properties.GlobalProperties;
import co.moviired.digitalcontent.incomm.repository.IIncommRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import lombok.extern.slf4j.Slf4j;
import org.jpos.iso.ISOMsg;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Slf4j
@Service
public final class IncommService implements Serializable {
    private static final long serialVersionUID = 1483310166901071817L;

    private static final String SEPARATOR = "\\|";
    private static final String ERROR_TIMEOUT = "TIME OUT OPER INCOMM";
    private static final String LBL_REQUEST_VALUE = "REQUEST OBJ COMUN - Value..... [{}]";

    private static final String LBL_PASS = "password";
    private static final String LBL_OTP = "otp";
    private static final String LBL_PIN = "pin";
    private static final String LBL_MPIN = "mpin";

    private static final String LBL_META = "meta: ";
    private static final String LBL_DATA = "data: ";

    private static final String ERROR_UNEXPECTED = "Error inesperado";
    private static final String LBL_TRANSACTION_START = "Solicitud de transacción";
    private static final String LBL_REQUEST = "El servicio recibe:";
    private static final String LBL_VALUE = "Value [{}]";
    private static final String LINE = "------------------------------------------------------------------";

    private static final int FIELD_39 = 39;
    private static final int FIELD_63 = 63;

    private final IIncommRepository incommRepository;
    private final BuildTxnHelper buildTxnHelper;
    private final GlobalProperties config;
    private final ErrorHelper errorHelper;

    private final String[] hideFields;
    private final ObjectWriter jsonWriter;

    // Bandera para saber si el tercero está vivo y saber si se peueden enviar transaccionesss
    private boolean incommAlive = Boolean.TRUE;

    public IncommService(@NotNull BuildTxnHelper pbuildTxnHelper,
                         @NotNull IIncommRepository pincommRepository,
                         @NotNull GlobalProperties pconfig,
                         @NotNull ErrorHelper perrorHelper) {
        super();
        this.buildTxnHelper = pbuildTxnHelper;
        this.incommRepository = pincommRepository;
        this.config = pconfig;
        this.errorHelper = perrorHelper;

        this.jsonWriter = new ObjectMapper().writer();
        this.hideFields = new String[]{"2"};
        this.incommRepository.setLoggerHiddenField(this.hideFields);
    }

    // SERVICE METHODS

    // ECHO: Alive Method!!!
    public Mono<String> getEcho() {
        String response = "OK";

        try {
            verifyHealthCheckIncomm();

        } catch (Exception | ProcessingException e) {
            response = "FAIL. Cause: " + e.getMessage();
        }

        return Mono.just(response);
    }

    // PINES: Compra
    public Mono<Response> processPinesActivate(@NotNull Mono<Request> pinRequest) {
        return pinRequest.flatMap(request -> {
            // Generar el identificador único de operación
            setCorrelationId();
            Mono<Response> response;
            try {
                // Imprimir el LOG de inicio de transacción
                printTransactionStart("PIN - ACTIVACION", request);
                Input data = Input.parseInput(request.getData(), request.getMeta());
                log.debug("REQUEST OBJ COMUN PINES - Value..... [{}]", secureLogWrite(data));

                // Armar la petición
                ISOMsg isoRequest = buildTxnHelper.requestPinSale(data);
                log.info("==> Request INCOMM - PINES Value:");
                ISOMsgHelper.logISOMsg(isoRequest, hideFields);

                // Enviar la petición
                ISOMsg isoResponse = incommRepository.sendRequest(isoRequest);
                if (isoResponse == null) {
                    isoResponse = (ISOMsg) isoRequest.clone();
                    isoResponse.setResponseMTI();
                    log.info("SE GENERO TIMEOUT DE CONEXION CON OPERADOR");
                    isoResponse.set(FIELD_39, "99");
                    isoResponse.set(FIELD_63, ERROR_TIMEOUT);

                    processPinesReverse(pinRequest);
                }

                // Obtener la respuesta
                response = printTransactionEnd("PIN - ACTIVACION", isoResponse);

            } catch (DataException da) {
                response = Mono.just(new Response(da.getCode(), da.getMessage(), ErrorType.DATA, HttpStatus.PROCESSING));
            } catch (CommunicationException co) {
                response = Mono.just(new Response(co.getCode(), co.getMessage(), ErrorType.COMMUNICATION, HttpStatus.REQUEST_TIMEOUT));
            } catch (Exception e) {
                String[] error = this.errorHelper.getError("99", ERROR_UNEXPECTED).split(SEPARATOR);
                response = Mono.just(new Response(error[0], error[1], ErrorType.PROCESSING, HttpStatus.INTERNAL_SERVER_ERROR));
            }

            return response;
        });
    }

    // PINES: Reverse
    public Mono<Response> processPinesReverse(@NotNull Mono<Request> pinRequest) {
        return pinRequest.flatMap(request -> {
            // Generar el identificador único de operación
            setCorrelationId();
            Mono<Response> response;
            try {
                // Imprimir el LOG de inicio de transacción
                printTransactionStart("PIN - REVERSE", request);
                Input data = Input.parseInput(request.getData(), request.getMeta());
                log.debug(LINE);
                log.debug("REQUEST OBJ COMUN PINES - Value..... [{}]", secureLogWrite(data));

                // Armar la petición
                ISOMsg isoRequest = buildTxnHelper.requestPinReverso(data);
                log.info("==> Request INCOMM - PINES Value:");
                ISOMsgHelper.logISOMsg(isoRequest, hideFields);

                // Enviar la petición
                ISOMsg isoResponse = incommRepository.sendRequest(isoRequest);
                if (isoResponse == null) {
                    isoResponse = (ISOMsg) isoRequest.clone();
                    isoResponse.setResponseMTI();
                    log.info("SE GENERO TIMEOUT DE CONEXION CON OPERADOR");
                    isoResponse.set(FIELD_39, "99");
                    isoResponse.set(FIELD_63, ERROR_TIMEOUT);
                }

                // Obtener la respuesta
                response = printTransactionEnd("PIN - REVERSE", isoResponse);

            } catch (DataException da) {
                response = Mono.just(new Response(da.getCode(), da.getMessage(), ErrorType.DATA, HttpStatus.PROCESSING));
            } catch (CommunicationException co) {
                response = Mono.just(new Response(co.getCode(), co.getMessage(), ErrorType.COMMUNICATION, HttpStatus.REQUEST_TIMEOUT));
            } catch (Exception e) {
                String[] error = this.errorHelper.getError("99", ERROR_UNEXPECTED).split(SEPARATOR);
                response = Mono.just(new Response(error[0], error[1], ErrorType.PROCESSING, HttpStatus.INTERNAL_SERVER_ERROR));
            }

            return response;
        });
    }

    // CARD: Activacion
    public Mono<Response> processCardActivate(@NotNull Mono<Request> cardRequest) {
        return cardRequest.flatMap(request -> {
            // Generar el identificador único de operación
            setCorrelationId();
            Mono<Response> response;
            try {
                // Imprimir el LOG de inicio de transacción
                printTransactionStart("TARJETA - ACTIVACION", request);
                Input data = Input.parseInput(request.getData(), request.getMeta());
                log.debug(LINE);
                log.debug(LBL_REQUEST_VALUE, secureLogWrite(data));

                // Armar la petición
                ISOMsg isoRequest = buildTxnHelper.requestActivationMessage(data);
                log.info("==> Request INCOMM - TARJETA ACTIVACION Value");
                ISOMsgHelper.logISOMsg(isoRequest, hideFields);

                // Enviar la petición
                ISOMsg isoResponse = incommRepository.sendRequest(isoRequest);
                if (isoResponse == null) {
                    isoResponse = (ISOMsg) isoRequest.clone();
                    isoResponse.setResponseMTI();
                    log.info("ParticipantOperIncomm.prepare()::SE GENERO TIMEOUT DE CONEXION CON OPERADOR");
                    isoResponse.set(FIELD_39, "99");
                    isoResponse.set(FIELD_63, ERROR_TIMEOUT);

                    // SE INVOCA LA DESACTIVACIÓN
                    processDesactivationCard(cardRequest);
                }

                // Obtener la respuesta
                response = printTransactionEnd("TARJETA - ACTIVACION", isoResponse);

            } catch (CommunicationException co) {
                // Reversar la transacción por Timeout
                Thread thread = new Thread(() -> processCardReversion(cardRequest));
                thread.start();

                response = Mono.just(new Response(co.getCode(), co.getMessage(), ErrorType.COMMUNICATION, HttpStatus.REQUEST_TIMEOUT));

            } catch (DataException | Exception e) {
                String[] error = this.errorHelper.getError("99", ERROR_UNEXPECTED).split(SEPARATOR);
                response = Mono.just(new Response(error[0], error[1], ErrorType.PROCESSING, HttpStatus.INTERNAL_SERVER_ERROR));
            }

            return response;
        });
    }

    // CARD: Desactivation
    public Mono<Response> processCardInactivate(@NotNull Mono<Request> pinRequest) {
        return pinRequest.flatMap(request -> {
            // Generar el identificador único de operación
            setCorrelationId();
            Mono<Response> response;
            try {
                // Imprimir el LOG de inicio de transacción
                printTransactionStart("TARJETA - DESACTIVACION", request);
                Input data = Input.parseInput(request.getData(), request.getMeta());
                log.debug(LINE);
                log.debug(LBL_REQUEST_VALUE, secureLogWrite(data));

                // Armar la petición
                ISOMsg isoRequest = buildTxnHelper.requestDesactivationMessage(data);
                log.info("==> Request INCOMM - TARJETA DESACTIVACION Value");
                ISOMsgHelper.logISOMsg(isoRequest, hideFields);

                // Enviar la petición
                ISOMsg isoResponse = incommRepository.sendRequest(isoRequest);
                if (isoResponse == null) {
                    isoResponse = (ISOMsg) isoRequest.clone();
                    isoResponse.setResponseMTI();
                    log.info("ParticipantOperIncomm.prepare()::SE GENERO TIMEOUT DE CONEXION CON OPERADOR");
                    isoResponse.set(FIELD_39, "99");
                    isoResponse.set(FIELD_63, ERROR_TIMEOUT);
                }

                // Obtener la respuesta
                response = printTransactionEnd("TARJETA - DESACTIVACION", isoResponse);

            } catch (CommunicationException co) {
                response = Mono.just(new Response(co.getCode(), co.getMessage(), ErrorType.COMMUNICATION, HttpStatus.REQUEST_TIMEOUT));

            } catch (DataException | Exception e) {
                String[] error = this.errorHelper.getError("99", ERROR_UNEXPECTED).split(SEPARATOR);
                response = Mono.just(new Response(error[0], error[1], ErrorType.PROCESSING, HttpStatus.INTERNAL_SERVER_ERROR));
            }

            return response;
        });
    }

    // CARD: Reversion
    public Mono<Response> processCardReversion(@NotNull Mono<Request> pinRequest) {
        return pinRequest.flatMap(request -> {
            // Generar el identificador único de operación
            setCorrelationId();
            Mono<Response> response;
            try {
                // Imprimir el LOG de inicio de transacción
                printTransactionStart("TARJETA - REVERSION", request);
                Input data = Input.parseInput(request.getData(), request.getMeta());
                log.debug(LINE);
                log.debug(LBL_REQUEST_VALUE, secureLogWrite(data));

                // Armar la petición
                ISOMsg isoRequest = buildTxnHelper.requestReversionMessage(data);
                log.info("==> Request INCOMM - REVERSION Value");
                ISOMsgHelper.logISOMsg(isoRequest, hideFields);

                // Obtener la respuesta
                response = printTransactionEnd("TARJETA - REVERSION", incommRepository.sendRequest(isoRequest));

            } catch (CommunicationException co) {
                response = Mono.just(new Response(co.getCode(), co.getMessage(), ErrorType.COMMUNICATION, HttpStatus.REQUEST_TIMEOUT));
            } catch (DataException | Exception e) {
                String[] error = this.errorHelper.getError("99", ERROR_UNEXPECTED).split(SEPARATOR);
                response = Mono.just(new Response(error[0], error[1], ErrorType.PROCESSING, HttpStatus.INTERNAL_SERVER_ERROR));
            }

            return response;
        });
    }


    // UTIL METHODS

    // JOB: Hacer ECHO al operador para saber si está vivo
    @Scheduled(fixedRateString = "${properties.IC_ECHOTIME}", initialDelayString = "${properties.IC_ECHOTIME}")
    private void verifyHealthCheckIncomm() throws ProcessingException {
        // Conectar con InComm
        try {
            // Armar la petición
            ISOMsg echoRequest = this.buildTxnHelper.requestHealthCheck();
            ISOMsg isoResponse = incommRepository.sendRequest(echoRequest);

            if (isoResponse == null) {
                log.info("Se genero Timeout con Operador se rechaza la recarga ");
                isoResponse = echoRequest;
                isoResponse.set(FIELD_39, "99");
            } else {
                if (isoResponse.getString(FIELD_39).equalsIgnoreCase("00")) {
                    log.info("InComm HealthCheck [OK]");
                }
            }

            // Si OK, habilitar las peticiones hacia InComm
            incommAlive = Boolean.TRUE;

        } catch (Exception e) {
            // Detener las peticiones hacia InComm
            incommAlive = Boolean.FALSE;
            log.error("InComm HealthCheck [FAIL]. Cause: " + e.getMessage());
            log.error("\t--> InComm Transactions DISABLED");

            // Propagar la excepción
            throw new ProcessingException("99", e);
        }
    }

    private void processDesactivationCard(@NotNull Mono<Request> cardRequest) {
        if (config.isReverse()) {
            Response response;
            try {
                log.info("Generando invocación de Desactivación de CARD");
                int retries = this.config.getRetries();
                int delay = this.config.getDelay();
                log.info("Se generaran " + retries + " Intentos de desactivación.");

                for (int i = 0; i < retries; ++i) {
                    log.info("Intento de Desactivación numero: " + (i + 1));
                    response = this.processCardInactivate(cardRequest).block();
                    if ((response != null) && ("200".equals(response.getOutcome().getError().getErrorCode()))) {
                        log.info("Desactivación exitosa");
                        break;
                    }
                    log.info("Desactivación fallida");
                    this.sleep(delay);
                }
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
    }

    private void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (Exception var3) {
            log.error(ERROR_UNEXPECTED, var3);
        }

    }

    private String secureLogWrite(@NotNull Object value) throws JsonProcessingException {
        return Security.printIgnore(this.jsonWriter.writeValueAsString(value), LBL_PIN, LBL_PASS, LBL_MPIN, LBL_OTP);
    }

    private void printTransactionStart(@NotNull String transactionName, @NotNull Request request) throws JsonProcessingException, CommunicationException {
        // Verificar si se debe ejecutar la transacción
        if (!this.incommAlive) {
            throw new CommunicationException();
        }

        // Imprimir el LOG inicial
        log.info(LINE);
        log.info(LBL_TRANSACTION_START);
        log.info(LINE);
        log.info("REQUEST - Operations [{}]", transactionName);
        log.info(LBL_REQUEST);
        log.info(LBL_META + LBL_VALUE, secureLogWrite(request.getMeta()));
        log.info(LBL_DATA + LBL_VALUE, secureLogWrite(request.getData()));
    }

    private Mono<Response> printTransactionEnd(@NotNull String transactionName, @NotNull ISOMsg isoResponse) throws JsonProcessingException {
        log.info("<== Response From {} Value:", transactionName);
        ISOMsgHelper.logISOMsg(isoResponse, hideFields);
        Response response = buildTxnHelper.parseResponse(isoResponse, errorHelper);
        log.info("==> RESPONSE - {} [{}]", transactionName, secureLogWrite(response));
        return Mono.just(response);
    }

    // Crear el CorrelationID
    private void setCorrelationId() {
        MDC.putCloseable("correlation-id", Utilidades.generateCorrelationId());
        MDC.putCloseable("component", "authentication");
    }

}

