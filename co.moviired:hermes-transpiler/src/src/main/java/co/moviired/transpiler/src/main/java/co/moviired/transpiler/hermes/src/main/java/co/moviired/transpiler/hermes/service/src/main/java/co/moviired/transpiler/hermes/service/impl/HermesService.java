package co.moviired.transpiler.hermes.service.impl;

import co.moviired.connector.connector.ReactiveConnector;
import co.moviired.transpiler.conf.GlobalProperties;
import co.moviired.transpiler.exception.ParseException;
import co.moviired.transpiler.helper.ErrorHelper;
import co.moviired.transpiler.hermes.parser.IMahindraParser;
import co.moviired.transpiler.hermes.parser.MahindraParserFactory;
import co.moviired.transpiler.hermes.service.MahindraClientFactory;
import co.moviired.transpiler.jpa.movii.domain.dto.hermes.IHermesRequest;
import co.moviired.transpiler.jpa.movii.domain.dto.hermes.IHermesResponse;
import co.moviired.transpiler.jpa.movii.domain.dto.hermes.general.ResponseHermes;
import co.moviired.transpiler.jpa.movii.domain.dto.hermes.response.BillPayHermesResponse;
import co.moviired.transpiler.jpa.movii.domain.dto.hermes.response.TopUpHermesResponse;
import co.moviired.transpiler.jpa.movii.domain.dto.hermes.response.ValidateBillByEanHermesResponse;
import co.moviired.transpiler.jpa.movii.domain.dto.hermes.response.ValidateBillByReferenceHermesResponse;
import co.moviired.transpiler.jpa.movii.domain.dto.mahindra.ICommandRequest;
import co.moviired.transpiler.jpa.movii.domain.dto.mahindra.ICommandResponse;
import co.moviired.transpiler.jpa.movii.domain.dto.mahindra.billpay.response.CommandBillPay;
import co.moviired.transpiler.jpa.movii.domain.dto.mahindra.topup.response.Command;
import co.moviired.transpiler.jpa.movii.domain.dto.mahindra.validatebill.response.CommandDigitalContent;
import co.moviired.transpiler.jpa.movii.domain.dto.mahindra.validatebill.response.CommandValidateBillByEan;
import co.moviired.transpiler.jpa.movii.domain.dto.mahindra.validatebill.response.CommandValidateBillByReference;
import co.moviired.transpiler.jpa.movii.domain.enums.OperationType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.SerializationUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Map;

@Slf4j
@Service
public class HermesService implements Serializable {

    private static final long serialVersionUID = 6188499835234843887L;
    private static final String PMASK = "****";
    private static final String RESPONSE_LBL = " Response: ";
    private static final String PATRON_LOG = "[{}] {} {} {}";

    private final MahindraClientFactory clientFactory;
    private final MahindraParserFactory parserFactory;
    private final XmlMapper xmlMapper;
    private final ObjectMapper jsonMapper;

    private final GlobalProperties globalProperties;
    private final ErrorHelper errorHelper;

    public HermesService(MahindraClientFactory pclientFactory, MahindraParserFactory pparserFactory, GlobalProperties globalProperties, ErrorHelper errorHelper) {
        super();
        this.parserFactory = pparserFactory;
        clientFactory = pclientFactory;

        // JSON/XML Mapper
        this.xmlMapper = new XmlMapper();
        this.jsonMapper = new ObjectMapper();

        this.globalProperties = globalProperties;
        this.errorHelper = errorHelper;
    }

    public Mono<IHermesResponse> service(@NotNull OperationType opType, @NotNull Mono<IHermesRequest> hrequest) {
        return hrequest.flatMap(request -> {
            Mono<IHermesResponse> hermesResponse;
            String targetService = getTargetServiceName(opType);
            try {
                // Obtener los procesadores específicos al tipo de transacción
                IMahindraParser parser = this.parserFactory.getParser(opType);
                ReactiveConnector client = clientFactory.getClient(opType, request);
                HttpMethod httpMethod = clientFactory.getHttpMethod(opType, request);
                Map<String, String> headers = clientFactory.getHeaders(opType, request);

                // 2. Transformar de HermesRequest a CommandValidateBillByReference
                ICommandRequest mhRequest = parser.parseRequest(request);
                Object requestParams = getRequestString(opType, mhRequest);
                ICommandRequest logReq = requestMask(mhRequest);
                log.info(PATRON_LOG, request.getLogId(), targetService, " Request: ", logReq);

                //3. Validar el tiempo que lleva la petición (TimeOut)
                if (!validateTimeOut(request.getLogId(), "HERMES", request.getRequestDate(), request.getClient().getTimeZone())) {
                    String[] timeOutError = errorHelper.getError("504");
                    log.error(PATRON_LOG, request.getLogId(), "HERMES", RESPONSE_LBL, timeOutError[0] + "|" + timeOutError[1]);

                    ResponseHermes rh = new ResponseHermes();
                    rh.setStatusCode(timeOutError[0]);
                    rh.setStatusMessage(timeOutError[1]);
                    IHermesResponse hr = generateHermesResponse(opType, rh);
                    hr.setRequest(request);
                    return Mono.just(hr);
                }

                // 4. Invocar a Mahindra
                hermesResponse = client.exchange(httpMethod, requestParams, String.class, getMediaType(opType), headers)
                        .flatMap(objResponse -> {
                            IHermesResponse response = null;
                            try {
                                // Transformar (XML, JSON) response a  response especifico del parser
                                String mhr = ((String) objResponse);
                                log.info("[" + request.getLogId() + "] " + targetService + RESPONSE_LBL + mhr.replace("\n", "").replace("\r", ""));
                                ICommandResponse mhResponse = readValue(opType, mhr);

                                // Transformar Mahindra response a Hermes response
                                response = parser.parseResponse(request, mhResponse);

                            } catch (ParseException | IOException e) {
                                log.error("\n{}\n", e.getMessage());
                            }

                            // Devolver la respuesta transformada o vacía (en caso de error)
                            return (response != null) ? Mono.just(response) : Mono.empty();
                        })
                        .onErrorResume(e -> {
                            String errorMessage = e.getMessage();
                            if (errorMessage == null) {
                                errorMessage = "TIMEOUT DEL OPERADOR";
                            }
                            log.error("[" + request.getLogId() + "] " + targetService + RESPONSE_LBL + errorMessage);

                            ResponseHermes rh = new ResponseHermes();
                            rh.setStatusCode("403");
                            rh.setStatusMessage(errorMessage);
                            IHermesResponse hr = generateHermesResponse(opType, rh);
                            hr.setRequest(request);
                            return Mono.just(hr);
                        });

            } catch (Exception e) {
                log.error("[" + request.getLogId() + "] " + targetService + RESPONSE_LBL + e.getMessage());

                ResponseHermes rh = new ResponseHermes();
                rh.setStatusCode("500");
                rh.setStatusMessage(e.getMessage());
                IHermesResponse hr = generateHermesResponse(opType, rh);
                hr.setRequest(request);
                return Mono.just(hr);
            }

            return hermesResponse;
        });
    }

    // Leer la respuesta especifica en el COMMAND general
    private ICommandResponse readValue(@NotNull OperationType opType, @NotNull String mhr) throws ParseException, IOException {
        ICommandResponse response;

        switch (opType) {
            case TOPUP:
                response = this.xmlMapper.readValue(mhr.toLowerCase(), Command.class);
                break;

            case BILL_PAY:
                response = this.xmlMapper.readValue(mhr.toLowerCase(), CommandBillPay.class);
                break;

            case VALIDATE_BILL_REFERENCE:
                response = this.jsonMapper.readValue(mhr, CommandValidateBillByReference.class);
                break;

            case VALIDATE_BILL_EAN:
                response = this.jsonMapper.readValue(mhr, CommandValidateBillByEan.class);
                break;

            case DIGITAL_CONTENT_CARD:
            case DIGITAL_CONTENT_PINES:
                response = this.jsonMapper.readValue(mhr, CommandDigitalContent.class);
                break;

            default:
                throw new ParseException("Operación inválida");
        }

        return response;
    }

    // Devolver el request enmascarado para imprimir en el log
    private ICommandRequest requestMask(ICommandRequest request) {
        ICommandRequest mhrl = request;

        if (request instanceof co.moviired.transpiler.jpa.movii.domain.dto.mahindra.topup.request.Command) {
            co.moviired.transpiler.jpa.movii.domain.dto.mahindra.topup.request.Command rlt = (co.moviired.transpiler.jpa.movii.domain.dto.mahindra.topup.request.Command) SerializationUtils.clone(request);
            rlt.setMpin(PMASK);
            rlt.setPin(PMASK);
            mhrl = rlt;

        } else if (request instanceof co.moviired.transpiler.jpa.movii.domain.dto.mahindra.billpay.request.CommandBillPay) {
            co.moviired.transpiler.jpa.movii.domain.dto.mahindra.billpay.request.CommandBillPay rbill = (co.moviired.transpiler.jpa.movii.domain.dto.mahindra.billpay.request.CommandBillPay) SerializationUtils.clone(request);
            rbill.setMpin(PMASK);
            rbill.setPin(PMASK);
            mhrl = rbill;

        } else if (request instanceof co.moviired.transpiler.jpa.movii.domain.dto.mahindra.validatebill.request.CommandValidateBillByReference) {
            co.moviired.transpiler.jpa.movii.domain.dto.mahindra.validatebill.request.CommandValidateBillByReference vbill = (co.moviired.transpiler.jpa.movii.domain.dto.mahindra.validatebill.request.CommandValidateBillByReference) SerializationUtils.clone(request);
            vbill.getMeta().setUserName(PMASK);
            vbill.getMeta().setPasswordHash(PMASK);
            mhrl = vbill;

        } else if (request instanceof co.moviired.transpiler.jpa.movii.domain.dto.mahindra.validatebill.request.CommandValidateBillByEan) {
            co.moviired.transpiler.jpa.movii.domain.dto.mahindra.validatebill.request.CommandValidateBillByEan vbill = (co.moviired.transpiler.jpa.movii.domain.dto.mahindra.validatebill.request.CommandValidateBillByEan) SerializationUtils.clone(request);
            vbill.getMeta().setUserName(PMASK);
            vbill.getMeta().setPasswordHash(PMASK);
            mhrl = vbill;
        }

        return mhrl;
    }

    // Devolver el objeto de respuesta adecuado según el tipo de operación
    private IHermesResponse generateHermesResponse(OperationType opType, ResponseHermes rh) {
        IHermesResponse hr;
        switch (opType) {
            case VALIDATE_BILL_REFERENCE:
                hr = new ValidateBillByReferenceHermesResponse();
                break;
            case VALIDATE_BILL_EAN:
                hr = new ValidateBillByEanHermesResponse();
                break;
            case BILL_PAY:
                hr = new BillPayHermesResponse();
                break;
            default:
                hr = new TopUpHermesResponse();
                break;
        }
        hr.setResponse(rh);
        return hr;
    }

    // Devolver la petición en su representación String: XML, JSON; según el tipo de operación
    private String getRequestString(OperationType opType, ICommandRequest mhRequest) throws JsonProcessingException {
        String hr;
        switch (opType) {
            case DIGITAL_CONTENT_CARD:
            case DIGITAL_CONTENT_PINES:
            case VALIDATE_BILL_REFERENCE:
            case VALIDATE_BILL_EAN:
                hr = this.jsonMapper.writer().writeValueAsString(mhRequest);
                break;

            default:
                hr = this.xmlMapper.writeValueAsString(mhRequest).toUpperCase();
                break;
        }

        return hr;
    }

    // Devolver el servicio de procesamiento
    private String getTargetServiceName(OperationType opType) {
        String hr;
        switch (opType) {
            case DIGITAL_CONTENT_CARD:
            case DIGITAL_CONTENT_PINES:
                hr = "DIGITAL CONTENT";
                break;

            case VALIDATE_BILL_REFERENCE:
            case VALIDATE_BILL_EAN:
                hr = "GeTrax";
                break;

            default:
                hr = "MAHINDRA";
                break;
        }

        return hr;
    }

    // Obtener el MIME type asociado a la petición y respuesta; según el tipo de operación
    private MediaType getMediaType(OperationType opType) {
        MediaType type;
        switch (opType) {
            case DIGITAL_CONTENT_CARD:
            case DIGITAL_CONTENT_PINES:
            case VALIDATE_BILL_REFERENCE:
            case VALIDATE_BILL_EAN:
                type = MediaType.APPLICATION_JSON;
                break;

            default:
                type = MediaType.APPLICATION_XML;
                break;
        }

        return type;
    }

    // Validar el tiempo de la petición
    private boolean validateTimeOut(String logId, String targetService, String requestDate, String timeZone) {
        try {
            // SI NULL, no transformar Hora
            // != NULL, transformar la hora en base a la zona horario
            // Caso OXXO (Zona horaria de méxico "America/Mexico_City")
            // 2020-06-11 17:30:25.987 UTC-6
            // +2Hr
            // 2020-06-11 18:30:25.987 UTC-5

            //Variables
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/MM/yyyy HH:mm:ss");
            ZoneId zidBogota = ZoneId.of("America/Bogota");

            // Obtener el tiempo de espera configurado
            int serviceWaitTime = globalProperties.getServiceWaitTime();

            // Transformar requestDate de String -> LocalDateTime
            LocalDateTime requestLocalDateTime = LocalDateTime.parse(requestDate, formatter);

            // Obtener la fecha actual en base a la zona horaria
            LocalDateTime nowLocalDateTime;
            if (timeZone == null || timeZone.isEmpty()) {
                nowLocalDateTime = LocalDateTime.now(zidBogota);
            } else {
                nowLocalDateTime = LocalDateTime.now(ZoneId.of(timeZone));
            }

            // Verificar si la fecha de la petición es después de la local
            if(requestLocalDateTime.compareTo(nowLocalDateTime) > 0) {
                nowLocalDateTime = nowLocalDateTime.plusSeconds(serviceWaitTime);
            }

            //Obtener la diferencia en segundos de las fechas
            long seconds = requestLocalDateTime.until(nowLocalDateTime, ChronoUnit.SECONDS);
            log.info("[{}] {} {} {} {} {} {}", logId, targetService, RESPONSE_LBL, "requestDate: " + requestLocalDateTime, " - localDate: " + nowLocalDateTime, ": " + seconds + " seconds", " (waitTime: " + serviceWaitTime + ")");
            return ((requestLocalDateTime.compareTo(nowLocalDateTime) <= 0) && (seconds <= serviceWaitTime));
        } catch (Exception e) {
            log.error(PATRON_LOG, logId, targetService, RESPONSE_LBL, e.getMessage());
            return false;
        }
    }
}

