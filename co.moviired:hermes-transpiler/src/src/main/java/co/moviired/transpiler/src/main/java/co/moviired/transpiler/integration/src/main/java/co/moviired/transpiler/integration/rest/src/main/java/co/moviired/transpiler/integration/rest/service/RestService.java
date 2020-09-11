package co.moviired.transpiler.integration.rest.service;

import co.moviired.transpiler.exception.ParseException;
import co.moviired.transpiler.hermes.service.impl.HermesService;
import co.moviired.transpiler.integration.IHermesParser;
import co.moviired.transpiler.integration.rest.dto.IRestRequest;
import co.moviired.transpiler.integration.rest.dto.IRestResponse;
import co.moviired.transpiler.integration.rest.dto.billpay.request.RequestBillPayDTO;
import co.moviired.transpiler.integration.rest.dto.billpay.response.ResponseBillPayDTO;
import co.moviired.transpiler.integration.rest.dto.cashout.request.RequestCashOutDTO;
import co.moviired.transpiler.integration.rest.dto.cashout.response.ResponseCashOutDTO;
import co.moviired.transpiler.integration.rest.dto.common.response.Error;
import co.moviired.transpiler.integration.rest.dto.common.response.Outcome;
import co.moviired.transpiler.integration.rest.dto.digitalcontent.response.ResponseDigitalContentDTO;
import co.moviired.transpiler.integration.rest.dto.topup.request.RequestTopUpDTO;
import co.moviired.transpiler.integration.rest.dto.topup.response.ResponseTopUpDTO;
import co.moviired.transpiler.integration.rest.dto.validatebill.response.ResponseValidateBillByEanDTO;
import co.moviired.transpiler.integration.rest.dto.validatebill.response.ResponseValidateBillByReferenceDTO;
import co.moviired.transpiler.integration.rest.parser.RestParserFactory;
import co.moviired.transpiler.jpa.movii.domain.dto.hermes.IHermesRequest;
import co.moviired.transpiler.jpa.movii.domain.enums.OperationType;
import co.moviired.transpiler.jpa.movii.domain.enums.Protocol;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.Serializable;
import java.util.UUID;

@Slf4j
@Service
public class RestService implements Serializable {

    private static final long serialVersionUID = -1738595034555472938L;
    private static final String PMASK = "****";

    private final RestParserFactory parserFactory;
    private final HermesService hermesService;
    private final ObjectMapper jsonMapper;

    public RestService(RestParserFactory pparserFactory, HermesService phermesService) {
        super();
        this.parserFactory = pparserFactory;
        this.hermesService = phermesService;

        // JSON/XML Mapper
        this.jsonMapper = new ObjectMapper();
    }

    public final Mono<IRestResponse> proccess(@NotNull OperationType opType, @NotNull Mono<IRestRequest> restRequest) {
        return restRequest.flatMap(request -> {
            Mono<IRestResponse> restResponse;

            // Generar el identificador único de operación
            String uuidOperation = UUID.randomUUID().toString().replace("-", "");

            try {
                // Enmascarar el usuario y la clave en el LOG
                IRestRequest rl = requestMask(request);
                log.info("Transacción iniciada: [" + uuidOperation + "]");
                log.info("[" + uuidOperation + "] REST REQUEST  - Type  [" + opType + "]");
                log.info("[" + uuidOperation + "] REST REQUEST  - Value [" + this.jsonMapper.writeValueAsString(rl) + "]");

                StopWatch watch = new StopWatch();
                watch.start();
                // Obtener el parser específico a la operación
                IHermesParser parser = parserFactory.getParser(opType);
                if (parser == null) {
                    throw new ParseException("Tipo de operación inválida");
                }

                // Transformar la petición REST a HermesRequest
                IHermesRequest hermesRequest = parser.parseRequest(this.jsonMapper.writeValueAsString(request));
                hermesRequest.setLogId(uuidOperation);
                hermesRequest.setProtocol(Protocol.REST);

                watch.stop();
                log.info("[{}] Tiempo de ejecución Parse Rest: {} millis", uuidOperation, watch.getTime());

                // Procesar la petición
                restResponse = hermesService.service(opType, Mono.just(hermesRequest))
                        .flatMap(hermesResponse -> {

                            // Start medición de tiempo
                            StopWatch watchS = new StopWatch();
                            watchS.start();
                            IRestResponse response;
                            try {
                                // Transformar la respuesta Hermes a Rest
                                String jsonResp = parser.parseResponse(hermesResponse);

                                // Transformar a JSON la respuesta según el tipo de respuesta
                                response = getResponse(opType, jsonResp);
                                jsonResp = jsonResp.replace("ean13BillerCode", "EAN13BillerCode");
                                log.info("[" + uuidOperation + "] REST RESPONSE - Value [" + jsonResp.replace("code2", "Code") + "]");

                            } catch (Exception e) {
                                log.error("[" + uuidOperation + "] REST RESPONSE - ERROR: " + e.getMessage());

                                // Armar el objeto de respuesta con el error específico
                                response = generateErrorResponse(opType, e);

                            } finally {
                                log.info("Transacción finalizada: [" + uuidOperation + "]");
                            }

                            watchS.stop();
                            log.info("[{}] Total servicio Rest: {} millis", uuidOperation, watchS.getTime());

                            // Devolver la respuesta transformada o vacía (en caso de error)
                            return Mono.just(response);
                        });

            } catch (Exception e) {
                log.error("[" + uuidOperation + "] REST RESPONSE - ERROR: " + e.getMessage(), e);
                log.info("Transacción finalizada: [" + uuidOperation + "]");

                // Armar el objeto de respuesta con el error específico
                restResponse = Mono.just(generateErrorResponse(opType, e));
            }

            return restResponse;
        });
    }

    // Util's METHODS

    // Generar respuesta errada, por error del servicio
    private IRestResponse generateErrorResponse(OperationType opType, Exception e) {
        Error error = new Error("0", "500", e.getMessage());
        Outcome outcome = new Outcome("500", e.getMessage(), error);

        IRestResponse response = null;
        // TOPUP
        if (opType.equals(OperationType.TOPUP)) {
            ResponseTopUpDTO responseTopUpDTO = new ResponseTopUpDTO();
            responseTopUpDTO.setOutcome(outcome);
            response = responseTopUpDTO;
        }

        return response;
    }

    // Obtener la respestue de HERMES; según el tipo de operacion
    private IRestResponse getResponse(OperationType opType, String jsonResp) throws IOException {
        IRestResponse response;
        switch (opType) {
            case CASH_OUT:
                response = this.jsonMapper.readValue(jsonResp, ResponseCashOutDTO.class);
                break;

            case BILL_PAY:
                response = this.jsonMapper.readValue(jsonResp, ResponseBillPayDTO.class);
                break;

            case VALIDATE_BILL_EAN:
                jsonResp = jsonResp.replace("ean13BillerCode", "EAN13BillerCode");
                response = this.jsonMapper.readValue(jsonResp, ResponseValidateBillByEanDTO.class);
                break;

            case VALIDATE_BILL_REFERENCE:
                response = this.jsonMapper.readValue(jsonResp, ResponseValidateBillByReferenceDTO.class);
                break;

            case DIGITAL_CONTENT_CARD:
            case DIGITAL_CONTENT_PINES:
                response = this.jsonMapper.readValue(jsonResp, ResponseDigitalContentDTO.class);
                break;
            default:
                response = this.jsonMapper.readValue(jsonResp, ResponseTopUpDTO.class);
        }

        return response;
    }

    // Devolver el request enmascarado para imprimir en el log
    private IRestRequest requestMask(IRestRequest request) {
        IRestRequest rl = request;

        if (request instanceof RequestTopUpDTO) {
            RequestTopUpDTO rlt = (RequestTopUpDTO) SerializationUtils.clone(request);
            rlt.getMeta().setUserName(PMASK);
            rlt.getMeta().setPassword(PMASK);
            rlt.getMeta().setPasswordHash(PMASK);
            rl = rlt;

        } else if (request instanceof RequestBillPayDTO) {
            RequestBillPayDTO rbill = (RequestBillPayDTO) SerializationUtils.clone(request);
            rbill.getMeta().setUserName(PMASK);
            rbill.getMeta().setPassword(PMASK);
            rbill.getMeta().setPasswordHash(PMASK);
            rl = rbill;

        } else if (request instanceof RequestCashOutDTO) {
            RequestCashOutDTO rcash = (RequestCashOutDTO) SerializationUtils.clone(request);
            rcash.getMeta().setUserName(PMASK);
            rcash.getMeta().setPassword(PMASK);
            rcash.getMeta().setPasswordHash(PMASK);
            rl = rcash;
        }

        return rl;
    }
}

