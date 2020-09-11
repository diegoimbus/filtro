package co.movii.auth.server.service;

import co.movii.auth.server.domain.dto.Request;
import co.movii.auth.server.domain.dto.Response;
import co.movii.auth.server.domain.enums.OperationType;
import co.movii.auth.server.domain.enums.ProviderType;
import co.movii.auth.server.exception.ParseException;
import co.movii.auth.server.helper.UtilHelper;
import co.movii.auth.server.providers.ClientFactory;
import co.movii.auth.server.providers.IRequest;
import co.movii.auth.server.providers.IResponse;
import co.movii.auth.server.providers.mahindra.parser.UserQueryInfoMahindraParser;
import co.movii.auth.server.providers.mahindra.request.CommandUserQueryInfoRequest;
import co.moviired.base.domain.enumeration.ErrorType;
import co.moviired.connector.connector.ReactiveConnector;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Service
public final class QueryUserInfoService implements Serializable {

    private static final String LOG_FORMATTED_4 = "{} {} {} {}";

    private final ClientFactory clientFactory;
    private final XmlMapper xmlMapper;
    private final UserQueryInfoMahindraParser parser;

    public QueryUserInfoService(@NotNull ClientFactory pclientFactory,
                                UserQueryInfoMahindraParser userQueryInfoMahindraParser) {
        super();
        this.clientFactory = pclientFactory;
        this.parser = userQueryInfoMahindraParser;

        this.xmlMapper = new XmlMapper();
        this.xmlMapper.configure(SerializationFeature.INDENT_OUTPUT, false);
    }

    public Response queryUserInfo(Mono<Request> data, String msisdn) {

        AtomicReference<Request> requestAtomicReference = new AtomicReference<>();

        return data.flatMap(request -> {

            asignarCorrelativo(request.getCorrelationId());

            requestAtomicReference.set(request);
            // INVOCAR A USER_QUERY_INFO
            try {

                ProviderType providerType = ProviderType.MAHINDRA;
                // Obtener los procesadores específicos al tipo de transacción
                ReactiveConnector provider = clientFactory.getClient(providerType);
                request.setUserLogin(msisdn);
                IRequest smRequest = parser.parseRequest(request);
                Map<String, String> headers = clientFactory.getHeaders(OperationType.USER_QUERY_INFO, providerType, request);
                IRequest mhrl = new CommandUserQueryInfoRequest((CommandUserQueryInfoRequest) smRequest);

                log.info("URL A INVOCAR: " + this.clientFactory.getUrl(providerType, OperationType.USER_QUERY_INFO, request));
                log.info(LOG_FORMATTED_4, "==>", " Request", ":  ", this.clientFactory.getRequestString(providerType, mhrl));

                return provider.exchange(
                        this.clientFactory.getHttpMethod(providerType, OperationType.USER_QUERY_INFO),
                        this.clientFactory.getUrl(providerType, OperationType.USER_QUERY_INFO, request),
                        this.clientFactory.getRequestString(providerType, smRequest),
                        String.class, this.clientFactory.getMediaType(providerType, OperationType.USER_QUERY_INFO),
                        headers);

            } catch (ParseException | JsonProcessingException e) {
                return Mono.error(e);
            }
        }).flatMap(obResp -> {
            // TRANSFORMAR RESPUESTA DE USER_QUERY_INFO
            ProviderType providerType = ProviderType.MAHINDRA;
            Request request = requestAtomicReference.get();
            try {
                // Transformar XML response a  response
                String mhr = ((String) obResp);
                IResponse mhResponse = this.clientFactory.readValue(providerType, OperationType.USER_QUERY_INFO, mhr);
                log.info(LOG_FORMATTED_4, "<==", " Response", ":  ", this.xmlMapper.writeValueAsString(mhResponse));

                // Transformar response especifica
                return Mono.just(parser.parseResponse(request, mhResponse));

            } catch (IOException | ParseException e) {
                return Mono.error(e);
            }
        }).onErrorResume(e -> {
            log.error(e.getMessage(), e);

            if (e instanceof NumberFormatException) {
                Response response = new Response();
                response.setErrorCode("99");
                response.setErrorMessage("La variable statusOperation debe ser numerica");
                response.setErrorType(ErrorType.DATA.name());
                return Mono.just(response);
            }

            Response response = new Response();
            response.setErrorCode("99");
            response.setErrorMessage(e.getMessage());
            response.setErrorType("ERROR");
            return Mono.just(response);

        }).block();
    }

    // Crear el CorrelationID
    public String asignarCorrelativo(String correlation) {
        String cId = correlation;

        if (correlation == null || correlation.isEmpty()) {
            cId = UtilHelper.generateCorrelationId();
        }

        MDC.putCloseable("correlation-id", cId);
        MDC.putCloseable("component", "authentication");
        return cId;
    }

}

