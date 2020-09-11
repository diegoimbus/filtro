package co.movii.auth.server.providers;

import co.movii.auth.server.domain.dto.Request;
import co.movii.auth.server.domain.enums.OperationType;
import co.movii.auth.server.domain.enums.ProviderType;
import co.movii.auth.server.exception.ParseException;
import co.movii.auth.server.properties.MahindraProperties;
import co.movii.auth.server.properties.SupportProfileProperties;
import co.movii.auth.server.providers.mahindra.response.CommandResponse;
import co.movii.auth.server.providers.supportprofile.response.ProfileNameResponse;
import co.movii.auth.server.security.crypt.CryptoUtility;
import co.moviired.connector.connector.ReactiveConnector;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public final class ClientFactory implements Serializable {

    private static final long serialVersionUID = -8111145829471817085L;
    private static final String INVALID_OPERATION = "Operación inválida";
    private static final String INVALID_PROVIDER = "Provider invalido";
    private static final String CORRELATION_ID = "correlationId";

    private final ReactiveConnector mhTransactionalClient;
    private final ReactiveConnector profileClient;
    private final XmlMapper xmlMapper;
    private final ObjectMapper objectMapper;
    private final SupportProfileProperties profileProperties;
    private final MahindraProperties mahindraProperties;
    private final CryptoUtility cryptoUtility;

    public ClientFactory(@Qualifier("mhTransactionalClient") ReactiveConnector pmhTransactionalClient,
                         @Qualifier("profileClient") ReactiveConnector pprofileClient,
                         SupportProfileProperties pprofileProperties,
                         MahindraProperties pmahindraProperties, CryptoUtility pcryptoUtility) {
        super();
        this.mhTransactionalClient = pmhTransactionalClient;
        this.profileProperties = pprofileProperties;
        this.mahindraProperties = pmahindraProperties;

        this.profileClient = pprofileClient;
        this.cryptoUtility = pcryptoUtility;
        this.xmlMapper = new XmlMapper();
        this.xmlMapper.configure(SerializationFeature.INDENT_OUTPUT, false);
        this.objectMapper = new ObjectMapper();
        this.objectMapper.configure(SerializationFeature.INDENT_OUTPUT, false);
    }

    public ReactiveConnector getClient(ProviderType providerType) throws ParseException {
        ReactiveConnector client;

        switch (providerType) {
            case MAHINDRA:
                client = this.mhTransactionalClient;
                break;
            case SUPPORT_PROFILE:
                client = this.profileClient;
                break;
            default:
                throw new ParseException(INVALID_OPERATION);
        }

        return client;
    }


    public Map<String, String> getHeaders(@NotNull OperationType operationType, ProviderType providerType, Request request) {
        HashMap<String, String> headers = null;
        UUID uuid;
        String randomUUIDString;
        // Creating a random UUID (Universally unique identifier).
        uuid = UUID.randomUUID();
        randomUUIDString = uuid.toString();
        if (providerType == ProviderType.SUPPORT_PROFILE) {
            headers = new HashMap<>();
            switch (operationType) {
                case CHANGE_PASSWORD:
                case LOGIN:


                    if (request.getCorrelationId() == null) {
                        headers.put(CORRELATION_ID, randomUUIDString);
                    } else {
                        headers.put(CORRELATION_ID, request.getCorrelationId());
                    }
                    headers.put("Authorization", cryptoUtility.encryptAES(request.getUserLogin()) + ":" + cryptoUtility.encryptAES(request.getPin()));
                    break;

                case PROFILE_NAME:
                case USER_QUERY_INFO:
                case RESET_PASSWORD:
                case GENERATE_OTP:
                    if (request.getCorrelationId() == null) {
                        headers.put(CORRELATION_ID, randomUUIDString);
                    } else {
                        headers.put(CORRELATION_ID, request.getCorrelationId());
                    }
                    break;
                default:
                    headers = null;
            }
        }
        return headers;
    }


    // Devolver la petición en su representación String: XML, JSON; según el tipo de operación
    public String getRequestString(ProviderType providerType, IRequest mhRequest) throws JsonProcessingException, ParseException {
        String hr;
        switch (providerType) {
            case MAHINDRA:
                hr = this.xmlMapper.writeValueAsString(mhRequest).toUpperCase();
                break;

            case SUPPORT_PROFILE:
                hr = this.objectMapper.writer().writeValueAsString(mhRequest);
                break;

            default:
                throw new ParseException(INVALID_PROVIDER);
        }

        return hr;
    }

    // Obtener el MIME type asociado a la petición y respuesta; según el tipo de operación
    public MediaType getMediaType(ProviderType providerType, OperationType opType) throws ParseException {
        MediaType type;
        switch (providerType) {
            case MAHINDRA:
                switch (opType) {
                    case GENERATE_OTP:
                    case CHANGE_PASSWORD:
                    case RESET_PASSWORD:
                    case USER_QUERY_INFO:
                    case LOGIN:
                        type = MediaType.APPLICATION_XML;
                        break;
                    default:
                        throw new ParseException(INVALID_OPERATION);
                }
                break;
            case SUPPORT_PROFILE:
                if (!OperationType.PROFILE_NAME.equals(opType)) {
                    throw new ParseException(INVALID_OPERATION);
                }
                type = MediaType.APPLICATION_JSON;
                break;
            default:
                throw new ParseException(INVALID_PROVIDER);
        }
        return type;
    }


    // Obtener el MIME type asociado a la petición y respuesta; según el tipo de operación
    public HttpMethod getHttpMethod(ProviderType providerType, OperationType opType) throws ParseException {
        HttpMethod type;
        switch (providerType) {
            case MAHINDRA:
                switch (opType) {
                    case GENERATE_OTP:
                    case CHANGE_PASSWORD:
                    case RESET_PASSWORD:
                    case USER_QUERY_INFO:
                    case LOGIN:
                        type = HttpMethod.POST;
                        break;
                    default:
                        throw new ParseException(INVALID_OPERATION);
                }
                break;
            case SUPPORT_PROFILE:
                if (!OperationType.PROFILE_NAME.equals(opType)) {
                    throw new ParseException(INVALID_OPERATION);
                }
                type = HttpMethod.GET;
                break;
            default:
                throw new ParseException(INVALID_PROVIDER);
        }
        return type;
    }

    // Obtener el MIME type asociado a la petición y respuesta; según el tipo de operación
    public String getUrl(ProviderType providerType, OperationType opType, Request data) throws ParseException {
        String url;
        switch (providerType) {
            case MAHINDRA:
                url = this.mahindraProperties.getUrl();
                break;
            case SUPPORT_PROFILE:
                if (!OperationType.PROFILE_NAME.equals(opType)) {
                    throw new ParseException(INVALID_OPERATION);
                }
                url = this.profileProperties.getUrl() + this.profileProperties.getPathGetName() + "/" + data.getName();
                break;
            default:
                throw new ParseException(INVALID_PROVIDER);
        }
        return url;
    }


    // Leer la respuesta especifica en el COMMAND general
    public IResponse readValue(ProviderType providerType, @NotNull OperationType opType, @NotNull String mhr) throws ParseException, IOException {
        IResponse response = null;

        switch (providerType) {
            case MAHINDRA:
                switch (opType) {
                    case GENERATE_OTP:
                    case CHANGE_PASSWORD:
                    case RESET_PASSWORD:
                    case LOGIN:
                        String lmhr = mhr.replace("&", "y").replace("&", "y").replace("&", "y");
                        response = this.xmlMapper.readValue(lmhr, CommandResponse.class);
                        break;
                    case USER_QUERY_INFO:
                        response = this.xmlMapper.readValue(mhr, CommandResponse.class);
                        break;
                    default:
                        throw new ParseException(INVALID_OPERATION);
                }
                break;
            case SUPPORT_PROFILE:
                if (!OperationType.PROFILE_NAME.equals(opType)) {
                    throw new ParseException(INVALID_OPERATION);
                }
                response = this.objectMapper.readValue(mhr, ProfileNameResponse.class);
                break;
            default:
                throw new ParseException(INVALID_PROVIDER);
        }

        return response;
    }
}

