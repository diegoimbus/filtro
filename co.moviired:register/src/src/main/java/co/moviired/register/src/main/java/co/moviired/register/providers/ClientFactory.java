package co.moviired.register.providers;

import co.moviired.connector.connector.ReactiveConnector;
import co.moviired.register.domain.enums.register.OperationType;
import co.moviired.register.domain.enums.register.ProviderType;
import co.moviired.register.exceptions.ParseException;
import co.moviired.register.properties.MahindraProperties;
import co.moviired.register.providers.mahindra.response.CommandRegistryResponse;
import co.moviired.register.providers.mahindra.response.CommandUserQueryInfoResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.Serializable;

import static co.moviired.register.domain.enums.register.OperationType.REGISTRY_MERCHANT;
import static co.moviired.register.domain.enums.register.OperationType.USER_QUERY_INFO;
import static co.moviired.register.domain.enums.register.ProviderType.MAHINDRA;

@Component
public class ClientFactory implements Serializable {

    private static final String INVALID_OPERATION = "Operación inválida.";
    private static final String INVALID_PROVIDER = "Provider inválido.";

    private final ReactiveConnector mahindraClient;
    private final XmlMapper xmlMapper;
    private final MahindraProperties mahindraProperties;


    public ClientFactory(ReactiveConnector pMahindraClient,
                         MahindraProperties pMahindraProperties) {
        super();
        this.mahindraClient = pMahindraClient;
        this.mahindraProperties = pMahindraProperties;

        this.xmlMapper = new XmlMapper();
        this.xmlMapper.configure(SerializationFeature.INDENT_OUTPUT, false);
    }

    public final ReactiveConnector getClient(ProviderType providerType) throws ParseException {
        if (providerType.equals(MAHINDRA)) {
            return this.mahindraClient;
        }

        throw new ParseException(INVALID_OPERATION);
    }

    // Devolver la petición en su representación String: XML, JSON; según el tipo de operación
    public String getRequestString(ProviderType providerType, IRequest mhRequest) throws JsonProcessingException, ParseException {
        String hr;
        if (providerType.equals(MAHINDRA)) {
            hr = this.xmlMapper.writeValueAsString(mhRequest);
        } else {
            throw new ParseException(INVALID_PROVIDER);
        }

        return hr;
    }

    // Obtener el MIME type asociado a la petición y respuesta; según el tipo de operación
    public MediaType getMediaType(ProviderType providerType, OperationType opType) throws ParseException {
        MediaType type;

        if (providerType.equals((MAHINDRA))) {
            if (opType.equals(USER_QUERY_INFO) || opType.equals(REGISTRY_MERCHANT)) {
                type = MediaType.APPLICATION_XML;
            } else {
                throw new ParseException(INVALID_OPERATION);
            }
        } else {
            throw new ParseException(INVALID_PROVIDER);
        }

        return type;
    }


    // Obtener el MIME type asociado a la petición y respuesta; según el tipo de operación
    public HttpMethod getHttpMethod(ProviderType providerType, OperationType opType) throws ParseException {
        HttpMethod type;

        if (providerType.equals(MAHINDRA)) {
            if (opType.equals(REGISTRY_MERCHANT) || opType.equals(USER_QUERY_INFO)) {
                type = HttpMethod.POST;
            } else {
                throw new ParseException(INVALID_OPERATION);
            }
        } else {
            throw new ParseException(INVALID_PROVIDER);
        }

        return type;
    }

    // Obtener el MIME type asociado a la petición y respuesta; según el tipo de operación
    public String getUrl(ProviderType providerType) throws ParseException {
        String url;

        if (providerType.equals(MAHINDRA)) {
            url = this.mahindraProperties.getUrl();
        } else {
            throw new ParseException(INVALID_PROVIDER);
        }

        return url;
    }


    // Leer la respuesta especifica en el COMMAND general
    public IResponse readValue(@NotNull OperationType opType, @NotNull String mhr) throws ParseException, IOException {
        IResponse response = null;

        switch (opType) {
            case USER_QUERY_INFO:
                response = this.xmlMapper.readValue(mhr, CommandUserQueryInfoResponse.class);
                break;
            case REGISTRY_MERCHANT:
                response = this.xmlMapper.readValue(mhr, CommandRegistryResponse.class);
                break;
            default:
                throw new ParseException(INVALID_OPERATION);
        }
        return response;
    }
}

