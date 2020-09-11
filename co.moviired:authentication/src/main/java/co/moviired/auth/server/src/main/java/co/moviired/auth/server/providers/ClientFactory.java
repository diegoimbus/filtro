package co.moviired.auth.server.providers;

import co.moviired.auth.server.domain.dto.Request;
import co.moviired.auth.server.domain.enums.OperationType;
import co.moviired.auth.server.domain.enums.ProviderType;
import co.moviired.auth.server.exception.ParseException;
import co.moviired.auth.server.properties.MahindraProperties;
import co.moviired.auth.server.properties.SupportProfileProperties;
import co.moviired.auth.server.properties.SupportUserProperties;
import co.moviired.auth.server.providers.mahindra.response.CommandResponse;
import co.moviired.auth.server.providers.supportprofile.response.ProfileNameResponse;
import co.moviired.auth.server.providers.supportuser.response.*;
import co.moviired.auth.server.security.crypt.CryptoUtility;
import co.moviired.connector.connector.ReactiveConnector;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
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
    private final ReactiveConnector userClientLogin;
    private final ReactiveConnector userClientGetUser;
    private final ReactiveConnector userClientChangePassword;
    private final ReactiveConnector userClientGenerateOTP;
    private final ReactiveConnector userClientResetPassword;
    private final ReactiveConnector profileClient;
    private final XmlMapper xmlMapper;
    private final ObjectMapper objectMapper;
    private final SupportUserProperties userProperties;
    private final SupportProfileProperties profileProperties;
    private final MahindraProperties mahindraProperties;
    private final CryptoUtility cryptoUtility;

    public ClientFactory(ClientExternalFactory clientExternalFactory,
                         SupportProfileProperties pprofileProperties,
                         SupportUserProperties puserProperties,
                         MahindraProperties pmahindraProperties,
                         CryptoUtility pcryptoUtility) {
        super();
        this.mhTransactionalClient = clientExternalFactory.getMhTransactionalClient();
        this.userClientLogin = clientExternalFactory.getUserClientLogin();
        this.userClientGetUser = clientExternalFactory.getUserClientGetUser();
        this.userClientChangePassword = clientExternalFactory.getUserClientChangePassword();
        this.userClientGenerateOTP = clientExternalFactory.getUserClientGenerateOTP();
        this.userClientResetPassword = clientExternalFactory.getUserClientResetPassword();
        this.profileProperties = pprofileProperties;
        this.userProperties = puserProperties;
        this.mahindraProperties = pmahindraProperties;

        this.profileClient = clientExternalFactory.getProfileClient();
        this.cryptoUtility = pcryptoUtility;
        this.xmlMapper = new XmlMapper();
        this.xmlMapper.configure(SerializationFeature.INDENT_OUTPUT, false);
        this.objectMapper = new ObjectMapper();
        this.objectMapper.configure(SerializationFeature.INDENT_OUTPUT, false);
    }

    public ReactiveConnector getClient(ProviderType providerType, OperationType opType) throws ParseException {
        ReactiveConnector client;

        switch (providerType) {
            case MAHINDRA:
                client = this.mhTransactionalClient;
                break;
            case SUPPORT_USER:
                switch (opType) {
                    case GENERATE_OTP:
                        client = this.userClientGenerateOTP;
                        break;
                    case RESET_PASSWORD:
                        client = this.userClientResetPassword;
                        break;
                    case CHANGE_PASSWORD:
                        client = this.userClientChangePassword;
                        break;
                    case LOGIN:
                        client = this.userClientLogin;
                        break;
                    case USER_QUERY_INFO:
                        client = this.userClientGetUser;
                        break;
                    default:
                        throw new ParseException(INVALID_OPERATION);
                }
                break;
            case SUPPORT_PROFILE:
                client = this.profileClient;
                break;
            default:
                throw new ParseException(INVALID_OPERATION);
        }

        return client;
    }


    public Map<String, String> getHeadersSupportProfileUser(@NotNull OperationType operationType, Request request, String randomUUIDString) {
        HashMap<String, String> headers = new HashMap<>();
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
                headers.put(CORRELATION_ID, request.getCorrelationId());
                if (request.getCorrelationId() == null) {
                    headers.put(CORRELATION_ID, randomUUIDString);
                }
                break;
            default:
                headers = null;
        }
        return headers;
    }

    public Map<String, String> getHeaders(@NotNull OperationType operationType, ProviderType providerType, Request request) {
        UUID uuid;
        String randomUUIDString;
        // Creating a random UUID (Universally unique identifier).
        uuid = UUID.randomUUID();
        randomUUIDString = uuid.toString();
        switch (providerType) {
            case SUPPORT_USER:
            case SUPPORT_PROFILE:
                return getHeadersSupportProfileUser(operationType, request, randomUUIDString);
            default:
                return null;
        }
    }


    // Devolver la petición en su representación String: XML, JSON; según el tipo de operación
    public String getRequestString(ProviderType providerType, IRequest mhRequest) throws JsonProcessingException, ParseException {
        String hr;
        switch (providerType) {
            case MAHINDRA:
                hr = this.xmlMapper.writeValueAsString(mhRequest).toUpperCase();
                break;

            case SUPPORT_USER:
            case SUPPORT_PROFILE:
                hr = this.objectMapper.writer().writeValueAsString(mhRequest);
                break;

            default:
                throw new ParseException(INVALID_PROVIDER);
        }

        return hr;
    }

    // Obtener el MIME type asociado a la petición y respuesta; según el tipo de operación
    public MediaType getMediaTypeMahindra(OperationType opType) throws ParseException {
        switch (opType) {
            case GENERATE_OTP:
            case CHANGE_PASSWORD:
            case RESET_PASSWORD:
            case USER_QUERY_INFO:
            case LOGIN:
                return MediaType.APPLICATION_XML;
            default:
                throw new ParseException(INVALID_OPERATION);
        }
    }

    // Obtener el MIME type asociado a la petición y respuesta; según el tipo de operación
    public MediaType getMediaTypeSupportUser(OperationType opType) throws ParseException {
        switch (opType) {
            case GENERATE_OTP:
            case RESET_PASSWORD:
            case CHANGE_PASSWORD:
            case USER_QUERY_INFO:
            case LOGIN:
                return MediaType.APPLICATION_JSON;
            default:
                throw new ParseException(INVALID_OPERATION);
        }
    }

    public MediaType getMediaType(ProviderType providerType, OperationType opType) throws ParseException {
        MediaType type;
        switch (providerType) {
            case MAHINDRA:
                type = getMediaTypeMahindra(opType);
                break;
            case SUPPORT_USER:
                type = getMediaTypeSupportUser(opType);
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
    public HttpMethod getHttpMethodMahindra(OperationType opType) throws ParseException {
        switch (opType) {
            case GENERATE_OTP:
            case CHANGE_PASSWORD:
            case RESET_PASSWORD:
            case USER_QUERY_INFO:
            case LOGIN:
                return HttpMethod.POST;
            default:
                throw new ParseException(INVALID_OPERATION);
        }
    }

    public HttpMethod getHttpMethodSupportUser(OperationType opType) throws ParseException {
        switch (opType) {
            case GENERATE_OTP:
            case CHANGE_PASSWORD:
            case RESET_PASSWORD:
            case USER_QUERY_INFO:
            case LOGIN:
                return HttpMethod.POST;
            default:
                throw new ParseException(INVALID_OPERATION);
        }
    }

    public HttpMethod getHttpMethod(ProviderType providerType, OperationType opType) throws ParseException {
        HttpMethod type;
        switch (providerType) {
            case MAHINDRA:
                return getHttpMethodMahindra(opType);
            case SUPPORT_USER:
                return getHttpMethodSupportUser(opType);
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
            case SUPPORT_USER:
                switch (opType) {
                    case GENERATE_OTP:
                        url = this.userProperties.getUrl() + this.userProperties.getPathGenerateOTP();
                        break;
                    case CHANGE_PASSWORD:
                        url = this.userProperties.getUrl() + this.userProperties.getPathChangePassword();
                        break;
                    case RESET_PASSWORD:
                        url = this.userProperties.getUrl() + this.userProperties.getPathResetPassword();
                        break;
                    case LOGIN:
                        url = this.userProperties.getUrl() + this.userProperties.getPathAutenticacion();
                        break;
                    case USER_QUERY_INFO:
                        url = this.userProperties.getUrl() + this.userProperties.getPathGetUser();
                        break;
                    default:
                        throw new ParseException(INVALID_OPERATION);
                }
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
    public IResponse readValueMahindra(@NotNull OperationType opType, @NotNull String mhr) throws ParseException, IOException {
        switch (opType) {
            case GENERATE_OTP:
            case CHANGE_PASSWORD:
            case RESET_PASSWORD:
            case USER_QUERY_INFO:
                return this.xmlMapper.readValue(mhr, CommandResponse.class);
            case LOGIN:
                String lmhr = mhr.replace("&", "y").replace("&", "y").replace("&", "y");
                return this.xmlMapper.readValue(lmhr, CommandResponse.class);

            default:
                throw new ParseException(INVALID_OPERATION);
        }
    }

    // Leer la respuesta especifica en el COMMAND general
    public IResponse readValueSupportUser(@NotNull OperationType opType, @NotNull String mhr) throws ParseException, IOException {
        switch (opType) {
            case LOGIN:
                LoginResponse loginResponse = this.objectMapper.readValue(mhr, LoginResponse.class);
                if (loginResponse.getUser() != null) {
                    loginResponse.getUser().setMpin("*******");
                }
                return loginResponse;
            case GENERATE_OTP:
                return this.objectMapper.readValue(mhr, GenerateOTPResponse.class);
            case CHANGE_PASSWORD:
                return this.objectMapper.readValue(mhr, ChangePasswordResponse.class);
            case RESET_PASSWORD:
                return this.objectMapper.readValue(mhr, ResetPasswordResponse.class);
            case USER_QUERY_INFO:
                return this.objectMapper.readValue(mhr, GetUserResponse.class);
            default:
                throw new ParseException(INVALID_OPERATION);
        }
    }

    public IResponse readValue(ProviderType providerType, @NotNull OperationType opType, @NotNull String mhr) throws ParseException, IOException {
        IResponse response = null;

        switch (providerType) {
            case MAHINDRA:
                return readValueMahindra(opType, mhr);
            case SUPPORT_USER:
                return readValueSupportUser(opType, mhr);
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

