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
import com.fasterxml.jackson.databind.ObjectMapper;
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
public class IncommService implements Serializable {
    private static final long serialVersionUID = 1483310166901071817L;

    private static final String SEPARATOR = "\\|";
    private static final String ERROR_TIMEOUT = "TIME OUT OPER INCOMM";
    private static final String LBL_REQUEST_VALUE = "REQUEST OBJ COMUN - Value..... [";

    private static final String LBL_PASS = "password";
    private static final String LBL_OTP = "otp";
    private static final String LBL_PIN = "pin";
    private static final String LBL_MPIN = "mpin";

    private static final String ERROR_UNEXPECTED = "Error inesperado";
    private static final String LBL_TRANSACTION_START = "Solicitud de transacción";
    private static final String LBL_REQUEST = "El servicio recibe:";
    private static final String LBL_VALUE = "Value [{}]";
    private static final String LINE = "------------------------------------------------------------------";

    private final IIncommRepository incommRepository;
    private final BuildTxnHelper buildTxnHelper;
    private final GlobalProperties config;
    private final ErrorHelper errorHelper;

    private final ObjectMapper objectMapper;
    private final String[] hideFields;
    // Bandera para saber si el tercero está vivo y saber si se peueden enviar transaccionesss
    private boolean incommAlive = Boolean.TRUE;

    public IncommService(@NotNull BuildTxnHelper pbuildTxnHelper,
                         @NotNull IIncommRepository incommRepository,
                         @NotNull GlobalProperties config,
                         @NotNull ErrorHelper errorHelper) {
        super();
        this.buildTxnHelper = pbuildTxnHelper;
        this.incommRepository = incommRepository;
        this.config = config;
        this.errorHelper = errorHelper;

        this.objectMapper = new ObjectMapper();

        this.hideFields = new String[]{"2"};
    }

    // SERVICE METHODS

    // ECHO: Alive Method!!!
    final Mono<String> getEcho() {
        String response = "OK";

        try {
            verifyHealthCheckIncomm();

        } catch (Exception | ProcessingException e) {
            response = "FAIL. Cause: " + e.getMessage();
        }

        return Mono.just(response);
    }

    // PINES: Compra
    public final Mono<Response> processPinesActivate(@NotNull Mono<Request> pinRequest) {
        return pinRequest.flatMap(request -> {
            // Generar el identificador único de operación
            asignarCorrelativo("");
            Mono<Response> response;
            try {

                if (!this.incommAlive) {
                    throw new CommunicationException();
                }

                log.info(LBL_TRANSACTION_START);
                log.info("REQUEST - Operations [SALE PIN]");
                log.info(LBL_REQUEST);
                log.info(LBL_VALUE, Security.printIgnore(this.objectMapper.writer().writeValueAsString(request), LBL_PIN, LBL_PASS, LBL_MPIN, LBL_OTP));

                Input data = Input.parseInput(request.getData(), request.getMeta());
                log.debug("REQUEST OBJ COMUN PINES - Value..... [" + Security.printIgnore(this.objectMapper.writer().writeValueAsString(data), LBL_PIN, LBL_PASS, LBL_MPIN, LBL_OTP) + "]");

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
                    isoResponse.set(39, "99");
                    isoResponse.set(63, ERROR_TIMEOUT);

                    processPinesReverse(pinRequest);
                }

                // Obtener la respuesta
                log.info("<== Response From INCOMM - PINES Value:");
                ISOMsgHelper.logISOMsg(isoResponse, hideFields);
                response = Mono.just(buildTxnHelper.parseResponse(isoResponse, errorHelper));
                log.info("<== RESPONSE - PINES [" + Security.printIgnore(this.objectMapper.writer().writeValueAsString(response.block()), LBL_PIN, LBL_PASS, LBL_MPIN, LBL_OTP) + "]");

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
    public final Mono<Response> processPinesReverse(@NotNull Mono<Request> pinRequest) {
        return pinRequest.flatMap(request -> {
            // Generar el identificador único de operación
            asignarCorrelativo("");
            Mono<Response> response;
            try {

                if (!this.incommAlive) {
                    throw new CommunicationException();
                }

                log.info(LINE);
                log.info(LBL_TRANSACTION_START);
                log.info(LINE);
                log.info("REQUEST - Operations [PIN REVERSE]");
                log.info(LBL_REQUEST);
                log.info(LBL_VALUE, Security.printIgnore(this.objectMapper.writer().writeValueAsString(request), LBL_PIN, LBL_PASS, LBL_MPIN, LBL_OTP));

                Input data = Input.parseInput(request.getData(), request.getMeta());
                log.debug(LINE);
                log.debug("REQUEST OBJ COMUN PINES - Value..... [" + Security.printIgnore(this.objectMapper.writer().writeValueAsString(data), LBL_PIN, LBL_PASS, LBL_MPIN, LBL_OTP) + "]");

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
                    isoResponse.set(39, "99");
                    isoResponse.set(63, ERROR_TIMEOUT);

                }
                // Obtener la respuesta
                log.info("<== Response From INCOMM - PINES Value:");
                ISOMsgHelper.logISOMsg(isoResponse, hideFields);
                response = Mono.just(buildTxnHelper.parseResponse(isoResponse, errorHelper));
                log.info("<== RESPONSE - PINES [" + Security.printIgnore(this.objectMapper.writer().writeValueAsString(response.block()), LBL_PIN, LBL_PASS, LBL_MPIN, LBL_OTP) + "]");

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
    public final Mono<Response> processCardActivate(@NotNull Mono<Request> cardRequest) {
        return cardRequest.flatMap(request -> {
            // Generar el identificador único de operación
            asignarCorrelativo("");
            Mono<Response> response;
            try {
                if (!this.incommAlive) {
                    throw new CommunicationException();
                }

                log.info(LINE);
                log.info(LBL_TRANSACTION_START);
                log.info(LINE);
                log.info("REQUEST - Operations [TARJETA - ACTIVACION]");
                log.info(LBL_REQUEST);
                log.info(LBL_VALUE, Security.printIgnore(this.objectMapper.writer().writeValueAsString(request), LBL_PIN, LBL_PASS, LBL_MPIN, LBL_OTP));

                Input data = Input.parseInput(request.getData(), request.getMeta());
                log.debug(LINE);
                log.debug(LBL_REQUEST_VALUE + Security.printIgnore(this.objectMapper.writer().writeValueAsString(data), LBL_PIN, LBL_PASS, LBL_MPIN, LBL_OTP) + "]");

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
                    isoResponse.set(39, "99");
                    isoResponse.set(63, ERROR_TIMEOUT);

                    // SE INVOCA LA DESACTIVACIÓN
                    processDesactivationCard(cardRequest);
                }

                // Obtener la respuesta
                log.info("<== Response From INCOMM - TARJETA ACTIVACION Value:");
                ISOMsgHelper.logISOMsg(isoResponse, hideFields);
                response = Mono.just(buildTxnHelper.parseResponse(isoResponse, errorHelper));
                log.info("==> RESPONSE - TARJETA ACTIVACION [" + Security.printIgnore(this.objectMapper.writer().writeValueAsString(response.block()), LBL_PIN, LBL_PASS, LBL_MPIN, LBL_OTP) + "]");

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
    public final Mono<Response> processCardInactivate(@NotNull Mono<Request> pinRequest) {
        return pinRequest.flatMap(request -> {
            // Generar el identificador único de operación
            asignarCorrelativo("");
            Mono<Response> response;
            try {

                if (!this.incommAlive) {
                    throw new CommunicationException();
                }

                log.info(LINE);
                log.info(LBL_TRANSACTION_START);
                log.info(LINE);
                log.info("REQUEST - Operations [TARJETA - DESACTIVACION]");
                log.info(LBL_REQUEST);
                log.info(LBL_VALUE, Security.printIgnore(this.objectMapper.writer().writeValueAsString(request), LBL_PIN, LBL_PASS, LBL_MPIN, LBL_OTP));

                Input data = Input.parseInput(request.getData(), request.getMeta());
                log.debug(LINE);
                log.debug(LBL_REQUEST_VALUE + Security.printIgnore(this.objectMapper.writer().writeValueAsString(data), LBL_PIN, LBL_PASS, LBL_MPIN, LBL_OTP) + "]");
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
                    isoResponse.set(39, "99");
                    isoResponse.set(63, ERROR_TIMEOUT);
                }
                // Obtener la respuesta
                log.info("<== Response From INCOMM - TARJETA DESACTIVACION Value:");
                ISOMsgHelper.logISOMsg(isoResponse, hideFields);
                response = Mono.just(buildTxnHelper.parseResponse(isoResponse, errorHelper));
                log.info("==> RESPONSE - TARJETA DESACTIVACION [" + Security.printIgnore(this.objectMapper.writer().writeValueAsString(response.block()), LBL_PIN, LBL_PASS, LBL_MPIN, LBL_OTP) + "]");

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
    public final Mono<Response> processCardReversion(@NotNull Mono<Request> pinRequest) {
        return pinRequest.flatMap(request -> {
            // Generar el identificador único de operación
            asignarCorrelativo("");
            Mono<Response> response;
            try {
                if (!this.incommAlive) {
                    throw new CommunicationException();
                }

                log.info(LINE);
                log.info(LBL_TRANSACTION_START);
                log.info(LINE);
                log.info("REQUEST - Operations [REVERSION]");
                log.info(LBL_REQUEST);
                log.info(LBL_VALUE, Security.printIgnore(this.objectMapper.writer().writeValueAsString(request), LBL_PIN, LBL_PASS, LBL_MPIN, LBL_OTP));

                Input data = Input.parseInput(request.getData(), request.getMeta());
                log.debug(LINE);
                log.debug(LBL_REQUEST_VALUE + Security.printIgnore(this.objectMapper.writer().writeValueAsString(data), LBL_PIN, LBL_PASS, LBL_MPIN, LBL_OTP) + "]");
                // Armar la petición
                ISOMsg isoRequest = buildTxnHelper.requestReversionMessage(data);
                log.info("==> Request INCOMM - REVERSION Value");
                ISOMsgHelper.logISOMsg(isoRequest, hideFields);

                // Enviar la petición
                ISOMsg isoResponse = incommRepository.sendRequest(isoRequest);

                // Obtener la respuesta
                log.info("<== Response From INCOMM - REVERSION Value:");
                ISOMsgHelper.logISOMsg(isoResponse, hideFields);
                response = Mono.just(buildTxnHelper.parseResponse(isoResponse, errorHelper));
                log.info("==> RESPONSE - REVERSION [" + Security.printIgnore(this.objectMapper.writer().writeValueAsString(response.block()), LBL_PIN, LBL_PASS, LBL_MPIN, LBL_OTP) + "]");

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

    private void processDesactivationCard(@NotNull Mono<Request> cardRequest) {
        if (config.isReverse()) {
            Mono<Response> response;
            try {
                log.info("Generando invocación de Desactivación de CARD");
                int retries = this.config.getRetries();
                int delay = this.config.getDelay();
                log.info("Se generaran " + retries + " Intentos de desactivación.");

                for (int i = 0; i < retries; ++i) {
                    log.info("Intento de Desactivación numero: " + (i + 1));
                    response = this.processCardInactivate(cardRequest);
                    if (response != null) {
                        if ("200".equals(response.block().getOutcome().getError().getErrorCode())) {
                            log.info("Desactivación exitosa");
                            break;
                        }
                        log.info("Desactivación fallida");
                    }
                    this.sleep(delay);
                }
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
    }

    private void sleep(int millis) {
        try {
            Thread.sleep((long) millis);
        } catch (Exception var3) {
            log.error(ERROR_UNEXPECTED, var3);
        }

    }

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
                isoResponse.set(39, "99");
            } else {
                if (isoResponse.getString(39).equalsIgnoreCase("00")) {
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


    // Crear el CorrelationID
    private String asignarCorrelativo(String correlation) {
        String cId = correlation;
        if (correlation == null || correlation.isEmpty()) {
            cId = Utilidades.generateCorrelationId();
        }

        MDC.putCloseable("correlation-id", cId);
        MDC.putCloseable("component", "authentication");
        return cId;
    }

}

