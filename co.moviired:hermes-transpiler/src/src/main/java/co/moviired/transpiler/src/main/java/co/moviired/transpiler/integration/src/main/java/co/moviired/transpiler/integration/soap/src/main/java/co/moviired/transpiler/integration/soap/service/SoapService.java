package co.moviired.transpiler.integration.soap.service;

import co.moviired.transpiler.exception.ParseException;
import co.moviired.transpiler.hermes.service.impl.HermesService;
import co.moviired.transpiler.integration.IHermesParser;
import co.moviired.transpiler.integration.soap.dto.soap.PrepaidProductsActivation;
import co.moviired.transpiler.integration.soap.dto.soap.PrepaidProductsActivationResponse;
import co.moviired.transpiler.integration.soap.dto.soap.TransactionPrepaidSale;
import co.moviired.transpiler.integration.soap.parser.SoapParserFactory;
import co.moviired.transpiler.jpa.movii.domain.dto.hermes.IHermesRequest;
import co.moviired.transpiler.jpa.movii.domain.enums.OperationType;
import co.moviired.transpiler.jpa.movii.domain.enums.Protocol;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.UUID;

@Slf4j
@Service
public class SoapService implements Serializable {

    private static final long serialVersionUID = 5849709191274023030L;
    private static final String PMASK = "****";

    private final SoapParserFactory parserFactory;
    private final HermesService hermesService;
    private final XmlMapper xmlMapper;
    private final ObjectMapper jsonMapper;

    public SoapService(SoapParserFactory pparserFactory, HermesService phermesService) {
        super();
        this.parserFactory = pparserFactory;
        this.hermesService = phermesService;

        // JSON/XML Mapper
        this.xmlMapper = new XmlMapper();
        this.jsonMapper = new ObjectMapper();
    }

    public final Mono<PrepaidProductsActivationResponse> proccess(@NotNull OperationType opType, @NotNull Mono<PrepaidProductsActivation> soapRequest) {
        return soapRequest.flatMap(request -> {
            Mono<PrepaidProductsActivationResponse> soapResponse;

            // Generar el identificador único de operación
            String uuidOperation = UUID.randomUUID().toString().replace("-", "");

            try {
                // Enmascarar el usuario y la clave en el LOG
                PrepaidProductsActivation rl = new PrepaidProductsActivation(request);
                rl.setUserName(PMASK);
                rl.setPassword(PMASK);

                log.info("Transacción iniciada: [" + uuidOperation + "]");
                log.info("[" + uuidOperation + "] SOAP REQUEST  - Type  [" + opType + "]");
                log.info("[" + uuidOperation + "] SOAP REQUEST  - Value [" + this.xmlMapper.writeValueAsString(rl) + "]");

                StopWatch watch = new StopWatch();
                watch.start();
                // Obtener el parser específico a la operación
                IHermesParser parser = parserFactory.getParser(opType);
                if (parser == null) {
                    throw new ParseException("Tipo de operación inválida");
                }

                // Transformar la petición SOAP a Hermes
                IHermesRequest hermesRequest = parser.parseRequest(this.jsonMapper.writeValueAsString(request));
                hermesRequest.setLogId(uuidOperation);
                hermesRequest.setProtocol(Protocol.SOAP);

                watch.stop();
                log.info("[{}] Tiempo de ejecución Parse SOAP: {} millis", uuidOperation, watch.getTime());

                // Procesar la petición
                soapResponse = hermesService.service(opType, Mono.just(hermesRequest))
                        .flatMap(hermesResponse -> {
                            // Start medición de tiempo
                            StopWatch watchS = new StopWatch();
                            watchS.start();
                            PrepaidProductsActivationResponse response;
                            try {
                                // Transformar la respuesta Hermes a Rest
                                String jsonResp = parser.parseResponse(hermesResponse);
                                response = this.jsonMapper.readValue(jsonResp, PrepaidProductsActivationResponse.class);

                                log.info("[" + uuidOperation + "] SOAP RESPONSE - Value [" + this.xmlMapper.writeValueAsString(response) + "]");

                            } catch (Exception e) {
                                log.error("[" + uuidOperation + "] SOAP RESPONSE - ERROR: " + e.getMessage(), e);

                                // Armar el objeto de respuesta con el error específico
                                response = generateErrorResponse(e);

                            } finally {
                                log.info("Transacción finalizada: [" + uuidOperation + "]");
                            }

                            watchS.stop();
                            log.info("[{}] Total servicio SOAP: {} millis", uuidOperation, watchS.getTime());

                            // Devolver la respuesta transformada o vacía (en caso de error)
                            return Mono.just(response);
                        });


            } catch (Exception e) {
                log.error("[" + uuidOperation + "] SOAP RESPONSE - ERROR: " + e.getMessage(), e);
                log.info("Transacción finalizada: [" + uuidOperation + "]");

                // Armar el objeto de respuesta con el error específico
                soapResponse = Mono.just(generateErrorResponse(e));
            }

            return soapResponse;
        });
    }

    // Util's METHODS

    // Generar respuesta errada, por error del servicio
    private PrepaidProductsActivationResponse generateErrorResponse(Exception e) {
        TransactionPrepaidSale tps = new TransactionPrepaidSale();
        tps.setAnswerCode("500");
        tps.setErrorDesc(e.getMessage());
        tps.setErrorDescription(e.getMessage());
        PrepaidProductsActivationResponse response = new PrepaidProductsActivationResponse();
        response.setTransactionPrepaidSale(tps);

        return response;
    }

}

