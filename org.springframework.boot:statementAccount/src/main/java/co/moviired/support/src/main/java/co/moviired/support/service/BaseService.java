package co.moviired.support.service;

import co.moviired.connector.connector.ReactiveConnector;
import co.moviired.support.properties.GlobalProperties;
import co.moviired.support.properties.ServiceManagerProperties;
import co.moviired.support.conf.StatusCodeConfig;
import co.moviired.support.domain.client.mahindra.MahindraDTO;
import co.moviired.support.domain.dto.ExtractDTO;
import co.moviired.support.domain.dto.ResponseStatus;
import co.moviired.support.domain.dto.ServiceManagerDTO;
import co.moviired.support.exceptions.ServiceException;
import co.moviired.support.properties.EmailGeneratorProperties;
import co.moviired.support.properties.MahindraProperties;
import co.moviired.support.util.UtilsHelper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;
import java.util.HashMap;

import static co.moviired.support.util.ConstantsHelper.*;

@Slf4j
public class BaseService {

    private final XmlMapper xmlMapper;
    private final ObjectMapper jsonMapper;
    private final GlobalProperties globalProperties;
    private final StatusCodeConfig statusCodeConfig;

    public BaseService(@NotNull GlobalProperties pglobalProperties, @NotNull StatusCodeConfig pstatusCodeConfig) {
        this.globalProperties = pglobalProperties;
        this.xmlMapper = new XmlMapper();
        this.xmlMapper.configure(SerializationFeature.INDENT_OUTPUT, false);
        this.statusCodeConfig = pstatusCodeConfig;
        this.jsonMapper = new ObjectMapper();
        this.jsonMapper.configure(SerializationFeature.INDENT_OUTPUT, false);
    }

    String logsStart(String requestType, String body) {
        String correlative = UtilsHelper.asignarCorrelativo(null);
        log.info(EMPTY_STRING);
        log.info(LBL_START);
        log.info(LBL_REQUEST_TYPE, requestType, body);
        return correlative;
    }

    void logsEnd(Object response) {
        String component = globalProperties.getApplicationName().toUpperCase();
        log.info(LBL_RESPONSE, component, new Gson().toJson(response));
        log.info(LBL_END);
        log.info(EMPTY_STRING);
    }

    // INVOKERS ********************************************************************************************************

    Mono<MahindraDTO> invokeMahindra(ReactiveConnector mahindraConnector, MahindraProperties mahindraProperties, MahindraDTO mahindraDTO, String correlative)
            throws CloneNotSupportedException, JsonProcessingException {
        log.info(LOG_THIRD_REQUEST, MAHINDRA_COMPONENT, mahindraProperties.getUrl(), mahindraDTO.toStringProtected());
        return mahindraConnector.exchange(HttpMethod.POST, mahindraProperties.getUrl(),
                this.xmlMapper.writeValueAsString(mahindraDTO), String.class, MediaType.APPLICATION_XML, new HashMap<>())
                .flatMap(response -> {
                    try {
                        UtilsHelper.asignarCorrelativo(correlative);
                        log.info(LBL_RESPONSE, MAHINDRA_COMPONENT.toUpperCase(), response);
                        MahindraDTO mahindraDTOResponse = this.xmlMapper.readValue((String) response, MahindraDTO.class);
                        return Mono.just(mahindraDTOResponse);
                    } catch (Exception e) {
                        log.error(LBL_ERROR, MAHINDRA_COMPONENT.toUpperCase(), e.getMessage());
                        return Mono.error(e);
                    }
                })
                .onErrorResume(error -> {
                    log.error(LBL_ERROR, MAHINDRA_COMPONENT.toUpperCase(), error.getMessage());
                    return Mono.error(error);
                });
    }

    Mono<ServiceManagerDTO> invokeServiceManager(ReactiveConnector serviceManagerConnector,
                                                 ServiceManagerProperties serviceManagerProperties,
                                                 ServiceManagerDTO request, String authentication, String correlative) {
        log.info(LOG_THIRD_REQUEST, SERVICE_MANAGER_COMPONENT, serviceManagerProperties.getUrl() +
                serviceManagerProperties.getUrlPathCrudEmail(), new Gson().toJson(request));
        return serviceManagerConnector.exchange(HttpMethod.POST, serviceManagerProperties.getUrlPathCrudEmail(), request,
                String.class, MediaType.APPLICATION_JSON, new ImmutableMap.Builder<String, String>()
                        .put(AUTHORIZATION_HEADER, authentication).build())
                .flatMap(response -> {
                    try {
                        UtilsHelper.asignarCorrelativo(correlative);
                        log.info(LBL_RESPONSE, SERVICE_MANAGER_COMPONENT.toUpperCase(), response);
                        return Mono.just(this.jsonMapper.readValue((String) response, ServiceManagerDTO.class));
                    } catch (Exception e) {
                        log.error(LBL_ERROR, SERVICE_MANAGER_COMPONENT.toUpperCase(), e.getMessage());
                        return Mono.error(e);
                    }
                })
                .flatMap(response -> onServiceManagerResponse(response, correlative))
                .onErrorResume(error -> {
                    log.error(LBL_ERROR, SERVICE_MANAGER_COMPONENT.toUpperCase(), error.getMessage());
                    return Mono.error(error);
                });
    }

    Mono<Object> invokeEmailSender(ReactiveConnector emailSenderConnector, EmailGeneratorProperties emailGeneratorProperties,
                                   String path, MultiValueMap<String, String> body, String correlative) {
        log.info(LOG_THIRD_REQUEST, EMAIL_SENDER_COMPONENT, emailGeneratorProperties.getUrl() + path, body.toString());
        return emailSenderConnector.exchange(HttpMethod.POST, path, body, MediaType.APPLICATION_FORM_URLENCODED, new HashMap<>())
                .flatMap(response -> {
                    try {
                        if (response.statusCode() == HttpStatus.OK) {
                            UtilsHelper.asignarCorrelativo(correlative);
                            log.info(LBL_RESPONSE, EMAIL_SENDER_COMPONENT.toUpperCase(), OK);
                            return Mono.just(new Object());
                        } else {
                            log.error(LOG_ERROR_INVOKING_SEND_EMAIL, response.statusCode());
                            return Mono.error(new ServiceException(
                                    response.statusCode().getReasonPhrase(),
                                    String.valueOf(response.statusCode().value()),
                                    EMAIL_SENDER_COMPONENT, null));
                        }
                    } catch (Exception e) {
                        log.error(LBL_ERROR, EMAIL_SENDER_COMPONENT.toUpperCase(), e.getMessage());
                        return Mono.error(e);
                    }
                })
                .onErrorResume(error -> {
                    log.error(LBL_ERROR, EMAIL_SENDER_COMPONENT.toUpperCase(), error.getMessage());
                    return Mono.error(error);
                });
    }

    // ON RESPONSES ****************************************************************************************************

    Mono<MahindraDTO> onMahindraResponse(MahindraDTO mahindraResponse, String correlative) {
        UtilsHelper.asignarCorrelativo(correlative);
        if (mahindraResponse.getTxnStatus().matches(SUCCESS_CODE)) {
            return Mono.just(mahindraResponse);
        } else {
            return Mono.error(new ServiceException(mahindraResponse.getMessage(), mahindraResponse.getTxnStatus(), MAHINDRA_COMPONENT, null));
        }
    }

    private Mono<ServiceManagerDTO> onServiceManagerResponse(ServiceManagerDTO serviceManagerDTO, String correlative) {
        UtilsHelper.asignarCorrelativo(correlative);
        if (serviceManagerDTO.getMessage() != null) {
            return Mono.just(serviceManagerDTO);
        } else {
            return Mono.error(new ServiceException(serviceManagerDTO.getMessage(), "500", SERVICE_MANAGER_COMPONENT, null));
        }
    }

    Mono<MahindraDTO> onLoginResponse(MahindraDTO loginResponse) {
        if (loginResponse.getUserType().equals("CHANNEL")) {
            return Mono.just(loginResponse);
        } else {
            return Mono.error(new ServiceException("The user not is merchant", "500", MAHINDRA_COMPONENT, null));
        }
    }

    // HANDLE ERROR ****************************************************************************************************

    void handleThrowableError(@NotNull String message, Throwable e, ExtractDTO response) {
        log.error(message, e.getMessage());
        if (e instanceof ServiceException) {
            response.setStatus(new ResponseStatus(((ServiceException) e).getCode(), e.getMessage(), ((ServiceException) e).getComponentOfError()));
        } else {
            response.setStatus(new ResponseStatus(
                    statusCodeConfig.of(SERVER_ERROR_CODE).getCode(),
                    statusCodeConfig.of(SERVER_ERROR_CODE).getMessage(),
                    globalProperties.getApplicationName()));
        }
    }

    void handleExceptionError(@NotNull String message, Throwable e, ExtractDTO response) {
        log.error(message, e.getMessage());
        response.setStatus(new ResponseStatus(
                statusCodeConfig.of(SERVER_ERROR_CODE).getCode(),
                statusCodeConfig.of(SERVER_ERROR_CODE).getMessage(),
                globalProperties.getApplicationName()));
    }

    public GlobalProperties getGlobalProperties() {
        return globalProperties;
    }

    public StatusCodeConfig getStatusCodeConfig() {
        return statusCodeConfig;
    }
}

