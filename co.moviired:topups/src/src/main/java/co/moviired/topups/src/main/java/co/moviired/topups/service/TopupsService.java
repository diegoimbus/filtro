package co.moviired.topups.service;

import co.moviired.base.domain.exception.ParsingException;
import co.moviired.base.helper.CryptoHelper;
import co.moviired.connector.connector.ReactiveConnector;
import co.moviired.topups.conf.MahindraProperties;
import co.moviired.topups.exception.DataException;
import co.moviired.topups.exception.ParseException;
import co.moviired.topups.mahindra.parser.IMahindraParser;
import co.moviired.topups.mahindra.parser.MahindraParserFactory;
import co.moviired.topups.mahindra.service.MahindraClientFactory;
import co.moviired.topups.model.domain.dto.mahindra.ICommandRequest;
import co.moviired.topups.model.domain.dto.mahindra.ICommandResponse;
import co.moviired.topups.model.domain.dto.mahindra.recharge.response.CommandRechargeIntegration;
import co.moviired.topups.model.domain.dto.recharge.IRechargeIntegrationResponse;
import co.moviired.topups.model.domain.dto.recharge.request.RechargeIntegrationHeaderRequest;
import co.moviired.topups.model.domain.dto.recharge.request.RechargeIntegrationRequest;
import co.moviired.topups.model.domain.dto.recharge.response.RechargeIntegrationResponse;
import co.moviired.topups.model.enums.OperationType;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author carlossaul.ramirez
 * @version 0.0.1
 */
@Service
@Slf4j
public class TopupsService implements Serializable {

    private static final long serialVersionUID = 3641868998829324426L;

    private static final String RESPONSE_MAHINDRA = "MAHINDRA Response ";

    private final MahindraClientFactory clientFactory;
    private final MahindraParserFactory parserFactory;
    private final CryptoHelper cryptoHelper;
    private final XmlMapper xmlMapper;
    private final MahindraProperties mahindraProperties;

    @Value(value = "${properties.mahindra.recharge.authorizationMatcherRegex}")
    private String authorizationMatcherRegex;

    public TopupsService(
            @NotNull MahindraParserFactory parserFactory,
            @NotNull MahindraClientFactory clientFactory,
            @NotNull CryptoHelper cryptoHelper,
            MahindraProperties mahindraProperties
    ) {
        super();
        this.xmlMapper = new XmlMapper();
        this.xmlMapper.configure(SerializationFeature.INDENT_OUTPUT, false);
        this.clientFactory = clientFactory;
        this.parserFactory = parserFactory;
        this.cryptoHelper = cryptoHelper;
        this.mahindraProperties = mahindraProperties;
    }

    // Recargar dispositivo por medio de mahindra
    public Mono<IRechargeIntegrationResponse> processTopups(
            @NotNull String logIdent,
            @NotNull OperationType operationType,
            @NotNull RechargeIntegrationHeaderRequest header,
            @NotNull Mono<RechargeIntegrationRequest> rechargeRequest) {

        return rechargeRequest.flatMap(recharge -> {
            log.debug("{} }", logIdent);
            try {
                header.setAuthorization(decodeAuthorizationHeaderField(header.getAuthorization()));

                // Validar los Headers
                if (!validateAuthorizationHeaderField(header.getAuthorization())) {
                    throw new DataException("-2", "El 'authorization' es un parámetro obligatorio y/o no cumple con la condición");
                }
                validateInput(recharge);

                // Obtener los procesadores según el tipo de operación
                IMahindraParser parser = parserFactory.getParser(operationType);
                ReactiveConnector client = clientFactory.getClient(operationType);

                // Generar el Request
                ICommandRequest mahindraRequest = parser.parseRequest(logIdent, recharge, header);
                String xmlRequest = xmlMapper.writeValueAsString(mahindraRequest).toUpperCase();

                // Invocar al tercero
                log.info("{} \"StartingConsumeMahindrasService\": \"init\"}", logIdent);
                return client.post(xmlRequest, String.class, MediaType.APPLICATION_XML, null)
                        .flatMap(mahindraResponse -> {
                            IRechargeIntegrationResponse response = null;
                            try {
                                // Obtener la respuesta original del tercero
                                String xmlResponse = (String) mahindraResponse;
                                ICommandResponse commandResponse = readValueFromXmlMahindraResponse(operationType, xmlResponse);
                                log.info("{} \"{}\": \"{}\"}", logIdent, RESPONSE_MAHINDRA, this.xmlMapper.writeValueAsString(commandResponse).toUpperCase());
                                // Transformar la respuesta al formato de salida
                                response = parser.parseResponse(logIdent, mahindraRequest, commandResponse, recharge.getPackageAmount());
                                log.info("{} \"FinalRechargeResponse\": \"{}\"}", logIdent, response);

                            } catch (Exception e) {
                                log.error("{}, \"Error Message\": \"{}\" }", logIdent, e.getMessage(), e);
                            }

                            return (response != null) ? Mono.just(response) : Mono.empty();

                        }).onErrorResume(e -> {
                            RechargeIntegrationResponse errorResp = RechargeIntegrationResponse.builder()
                                    .errorCode("403")
                                    .errorMessage(e.getMessage())
                                    .errorType("0").build();
                            return Mono.just(errorResp);
                        });

            } catch (ParsingException e) {
                log.error("{}, \"Error\": \"{}\"}", logIdent, e.getMessage());

                RechargeIntegrationResponse error = RechargeIntegrationResponse.builder()
                        .errorCode(e.getCode())
                        .errorMessage(e.getMessage())
                        .errorType("0").build();

                return Mono.just(error);
            }catch (Exception e) {
                log.error("{}, \"Error\": \"{}\"}", logIdent, e.getMessage());

                RechargeIntegrationResponse error = RechargeIntegrationResponse.builder()
                        .errorCode("500")
                        .errorMessage(e.getMessage())
                        .errorType("0").build();

                return Mono.just(error);
            }
        });
    }

    // Valida que el código de autorización sea --> 9999999999:9999
    public boolean validateAuthorizationHeaderField(String authorization) {

        if (authorizationMatcherRegex == null) {
            authorizationMatcherRegex = "^\\d{10}+[:]+\\d{4}$";
        }
        return authorization.matches(authorizationMatcherRegex);
    }

    private void validateInput(RechargeIntegrationRequest requestFormat) throws DataException {
        if (requestFormat.getIssuerDate() == null) {
            throw new DataException("-2", "issuerData es un parámetro obligatorio");
        }

        if (requestFormat.getIssuerId() == null) {
            throw new DataException("-2", "issuerId es un parámetro obligatorio");
        }

        if (requestFormat.getIssuerName() == null) {
            throw new DataException("-2", "issuerName es un parámetro obligatorio");
        }

        if (requestFormat.getSource() == null) {
            throw new DataException("-2", "source es un parámetro obligatorio");
        }

        if (requestFormat.getIp() == null) {
            throw new DataException("-2", "ip es un parámetro obligatorio");
        }

        if (requestFormat.getAmount()== null) {
            throw new DataException("-2", "amount es un parámetro obligatorio");
        }

        if (requestFormat.getEanCode() == null) {
            throw new DataException("-2", "eanCode es un parámetro obligatorio");
        }

    }
    //  Decode authorization
    private String decodeAuthorizationHeaderField(String authorization) throws ParsingException {
        String splitBy = mahindraProperties.getAuthSplitter();
        String decodeAuth = "";
        if (authorization == null) {
            return decodeAuth;
        }

        String[] vautorization = authorization.split(splitBy);
        if ((!vautorization[0].trim().matches("")) && (!vautorization[1].trim().matches(""))) {
            decodeAuth = cryptoHelper.decoder(vautorization[0]) + splitBy + cryptoHelper.decoder(vautorization[1]);
        }

        return decodeAuth;
    }

    // Convertir respuesta xml mahindra a objeto command
    private ICommandResponse readValueFromXmlMahindraResponse(OperationType type, String xmlResponse) throws ParseException, IOException {
        if (!OperationType.RTMMREQ.equals(type)) {
            throw new ParseException("Operación Inválida");
        }
        return this.xmlMapper.readValue(xmlResponse, CommandRechargeIntegration.class);
    }


    public String getCorrelationId(String correlationId , String ipRequest) {
        if (correlationId != null && !correlationId.equals("")) {
            return correlationId;
        } else
        {
            SimpleDateFormat fecha = new SimpleDateFormat("yyMMddHHmmssSSS");
            String prefix = "111";
            if ( ipRequest != null) {
                String[] ip = ipRequest.split("\\.");
                if (ip.length > 3) {
                    prefix = ip[3];
                }
            }
            StringBuilder ipConditional = new StringBuilder();
            ipConditional.append(prefix);
            ipConditional.append(fecha.format(new Date()));
            ipConditional.append(Math.round(Math.random() * 100));

            return ipConditional.toString();
        }
    }
}

