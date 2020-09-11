package co.moviired.transpiler.integration.iso.service;

import co.moviired.transpiler.hermes.service.impl.HermesService;
import co.moviired.transpiler.integration.IHermesParser;
import co.moviired.transpiler.jpa.movii.domain.dto.hermes.IHermesRequest;
import co.moviired.transpiler.jpa.movii.domain.dto.hermes.general.ResponseHermes;
import co.moviired.transpiler.jpa.movii.domain.dto.hermes.request.EchoHermesRequest;
import co.moviired.transpiler.jpa.movii.domain.dto.hermes.response.EchoHermesResponse;
import co.moviired.transpiler.jpa.movii.domain.enums.OperationType;
import co.moviired.transpiler.jpa.movii.domain.enums.Protocol;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.jpos.iso.ISOBasePackager;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOField;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.packager.GenericPackager;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
public class IsoService implements Serializable {

    private static final long serialVersionUID = 7353787682700892273L;

    private static final String LBL_ERROR_RESPONSE = "[{}] ISO RESPONSE - ERROR: {}";
    private static final String LBL_TRANSACTION_START = "[{}] Transacción iniciada";
    private static final String LBL_TRANSACTION_END = "[{}] Transacción finalizada";
    private static final String LBL_REQUEST_TYPE = "[{}] - Cliente: [{}] - ISO REQUEST - Type [{}]";
    private static final String LBL_REQUEST_VALUE = "[{}] - Cliente: [{}] - ISO REQUEST - Value [{}]";
    private static final String LBL_RESPONSE_TYPE = "[{}] - Cliente: [{}] - ISO RESPONSE - Type [{}]";
    private static final String LBL_RESPONSE_VALUE = "[{}] - Cliente: [{}] - ISO RESPONSE - Value [{}]";
    private static final String SPACE_CONNECTOR = " - ";

    // Services/Repositories
    private final HermesService hermesService;
    private final String clientName;
    // Iso8583 Packer definitions
    private transient ISOBasePackager packager;

    public IsoService(
            @NotNull ApplicationContext ctx,
            @NotNull String clientName,
            @NotNull String xmlPackager) throws IOException, ISOException {
        super();
        this.packager = getPackager(xmlPackager);
        this.clientName = clientName;

        // Cargar los servicios desde el contexto
        this.hermesService = ctx.getBean(HermesService.class);
    }

    // SERVICE METHODS

    private static String replaceLast(@NotNull String string, @NotNull String find, @NotNull String replace) {
        int lastIndex = string.lastIndexOf(find);

        if (lastIndex == -1) {
            return string;
        }

        String beginString = string.substring(0, lastIndex);
        String endString = string.substring(lastIndex + find.length());

        return beginString + replace + endString;
    }

    // PROCESS REQUEST
    public final Mono<String> proccess(@NotBlank String tramaIso,
                                       @NotNull IHermesParser parser,
                                       @NotNull OperationType opType) {

        // MÉTRICAS: Variables
        final int operTypeIndex = 3;
        Mono<String> isoResponse;

        // Generar el identificador único de operación
        String uuidOperation = UUID.randomUUID().toString().replace("-", "");

        try {
            // DEBUG: Mostrar la trama ISO en formato de fields
            Map<Integer, String> isoFieldsReq = this.getMessage(tramaIso);
            String pin = isoFieldsReq.get(52);
            if (pin == null) {
                pin = "";
            }

            // Crear la máscara del PIN (según longitud enviada)
            String mask = "*";
            if (pin.length() > 1) {
                mask = StringUtils.leftPad(mask, pin.length(), "*");
            }

            // Pintar la petición
            log.info(LBL_TRANSACTION_START, uuidOperation);
            log.info(LBL_REQUEST_TYPE, uuidOperation, clientName, (opType + SPACE_CONNECTOR + opType.getCode()));
            log.info(LBL_REQUEST_VALUE, uuidOperation, clientName, (replaceLast(tramaIso, pin, mask)));

            StopWatch watch = new StopWatch();
            watch.start();
            // DEBUG: Mostrar la trama ISO en formato de fields
            isoFieldsReq.put(52, pin);

            // Transformar la petición ISO-8583 a HermesRequest
            IHermesRequest hermesRequest = parser.parseRequest(tramaIso);
            hermesRequest.setLogId(uuidOperation);
            hermesRequest.setProtocol(Protocol.ISO);

            watch.stop();
            log.info("[{}] Tiempo de ejecución Parse ISO: {} millis", uuidOperation, watch.getTime());

            // Procesar la petición
            isoResponse = hermesService.service(opType, Mono.just(hermesRequest))
                    .flatMap(hermesResponse -> {
                        // Start medición de tiempo
                        StopWatch watchS = new StopWatch();
                        watchS.start();
                        String response;
                        try {
                            // Transformar la respuesta Hermes a respuesta ISO-8583 (Block reactive response)
                            response = parser.parseResponse(hermesResponse);

                            // Pintar la respuesta
                            log.info(LBL_RESPONSE_TYPE, uuidOperation, clientName, (opType + SPACE_CONNECTOR + response.substring(operTypeIndex, operTypeIndex + 4)));
                            log.info(LBL_RESPONSE_VALUE, uuidOperation, clientName, response);

                        } catch (Exception e) {
                            log.error(LBL_ERROR_RESPONSE, uuidOperation, e.getMessage(), e);

                            // Armar el objeto de respuesta con el error específico
                            response = generateErrorResponse(e);

                        } finally {
                            log.info(LBL_TRANSACTION_END, uuidOperation);
                        }

                        watchS.stop();
                        log.info("[{}] Total servicio ISO: {} millis", uuidOperation, watchS.getTime());
                        // Devolver la respuesta transformada o vacía (en caso de error)
                        return Mono.just(response);

                    });

        } catch (Exception e) {
            log.error(LBL_ERROR_RESPONSE, uuidOperation, e.getMessage(), e);
            log.info(LBL_TRANSACTION_END, uuidOperation);

            // Armar el objeto de respuesta con el error específico
            isoResponse = Mono.just(generateErrorResponse(e));
        }

        return isoResponse;
    }

    // UTILS METHOD
    private ISOBasePackager getPackager(@NotNull String xmlPackager) throws ISOException, IOException {
        if (this.packager == null) {
            // Cargar el ISO packager
            this.packager = new GenericPackager(new ClassPathResource(xmlPackager).getInputStream());
        }

        return packager;
    }

    private Map<Integer, String> getMessage(String request) {
        // Cargar el ISO packager
        Map<Integer, String> isoFields = new HashMap<>();
        try {
            isoFields = IsoHelper.getDataIsoFieldFromMessage(this.packager, request);

        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return isoFields;
    }

    // Generar respuesta errada, por error del servicio
    private String generateErrorResponse(Exception e) {
        String isoResponse = "";
        try {
            // Número de autorización
            String autorizacion = "00000000000";

            // Fecha de respuesta
            String field07 = new SimpleDateFormat("MMddHHmmss").format(Calendar.getInstance().getTime());

            // Número de autorización
            String field38 = "000000";

            // Código respuesta
            String field39 = "99";
            String message = e.getMessage();
            if ((message == null) || (message.trim().isEmpty()) || (message.contains("NullPointerException"))) {
                message = "TRAMA INVALIDA";
            }
            String mensajeRespuesta = "99|" + message;

            // Montos
            String field54 = "0000000000000000";

            // Nuevo Saldo
            String nuevoSaldo = "0";

            // Código de authenticación
            String field63 = autorizacion
                    + "|0|" + mensajeRespuesta + "|14|"
                    + field54
                    + "|000|"
                    + nuevoSaldo
                    + "|000000|0|0|00000";

            // Crear el mensaje de respuesta
            ISOMsg isoMessage = new ISOMsg(OperationType.TOPUP_RESPONSE.getCode());
            isoMessage.setPackager(this.packager);
            isoMessage.set(new ISOField(7, field07));
            isoMessage.set(new ISOField(38, field38));
            isoMessage.set(new ISOField(39, field39));
            isoMessage.set(new ISOField(63, field63));

            // Generar la trama de respuesta
            byte[] b = isoMessage.pack();
            isoResponse = new String(b, Charset.defaultCharset());

            return StringUtils.leftPad(Integer.toHexString(isoResponse.length()), 3, '0') + isoResponse;
        } catch (Exception ie) {
            log.error(ie.getMessage(), ie);
        }


        return isoResponse;
    }

    // PROCESS ECHO
    public final Mono<String> proccessEcho(@NotBlank String tramaIso,
                                           @NotNull IHermesParser parser,
                                           @NotNull OperationType opType) {
        Mono<String> isoResponse;
        final int operTypeIndex = 3;

        // Generar el identificador único de operación
        String uuidOperation = UUID.randomUUID().toString().replace("-", "");

        try {// DEBUG: Mostrar la trama ISO en formato de fields
            Map<Integer, String> isoFields = this.getMessage(tramaIso);
            String pin = isoFields.get(52);
            if (pin == null) {
                pin = "";
            }

            // Pintar la petición
            log.info(LBL_TRANSACTION_START, uuidOperation);
            log.info(LBL_REQUEST_TYPE, uuidOperation, clientName, (opType + SPACE_CONNECTOR + opType.getCode()));
            log.info(LBL_REQUEST_VALUE, uuidOperation, clientName, (replaceLast(tramaIso, pin, "****")));

            // DEBUG: Mostrar la trama ISO en formato de fields
            if (!pin.trim().isEmpty()) {
                isoFields.put(52, pin);
            }

            // Transformar la petición ISO-8583 a HermesRequest
            IHermesRequest ihermesRequest = parser.parseRequest(tramaIso);
            EchoHermesRequest hermesRequest = (EchoHermesRequest) ihermesRequest;

            hermesRequest.setLogId(uuidOperation);
            hermesRequest.setProtocol(Protocol.ISO);

            // Armar la respuesta
            ResponseHermes respHermes = new ResponseHermes();
            respHermes.setStatusCode("200");
            respHermes.setStatusMessage("OK");
            respHermes.setErrorCode("00");
            respHermes.setErrorMessage("OK");
            EchoHermesResponse hermesResponse = new EchoHermesResponse();
            hermesResponse.setRequest(hermesRequest);
            hermesResponse.setResponse(respHermes);
            hermesResponse.setClientTxnId(hermesRequest.getClientTxnId());
            hermesResponse.setNit(StringUtils.leftPad(hermesRequest.getNit(), 11, '0'));
            hermesResponse.setRed(hermesRequest.getRed());
            hermesResponse.setDate(hermesRequest.getDate());

            // DEBUG: Mostrar la trama ISO en formato de fields
            String tramaIsoResp = parser.parseResponse(hermesResponse);

            // Pintar la respuesta
            log.info(LBL_RESPONSE_TYPE, uuidOperation, clientName, (opType + SPACE_CONNECTOR + tramaIsoResp.substring(operTypeIndex, operTypeIndex + 4)));
            log.info(LBL_RESPONSE_VALUE, uuidOperation, clientName, tramaIsoResp);

            // Devolver la respuesta ISO
            isoResponse = Mono.just(tramaIsoResp);

        } catch (Exception e) {
            log.error(LBL_ERROR_RESPONSE, uuidOperation, e.getMessage(), e);

            // Armar el objeto de respuesta con el error específico
            isoResponse = Mono.just(generateErrorResponse(e));

        } finally {
            log.info("[" + uuidOperation + LBL_TRANSACTION_END);
        }

        return isoResponse;
    }

}

