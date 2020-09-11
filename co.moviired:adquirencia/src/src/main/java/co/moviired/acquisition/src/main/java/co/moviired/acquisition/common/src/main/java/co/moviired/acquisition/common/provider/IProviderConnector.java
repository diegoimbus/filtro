package co.moviired.acquisition.common.provider;

import co.moviired.connector.connector.ReactiveConnector;
import co.moviired.acquisition.common.config.GlobalProperties;
import co.moviired.acquisition.common.model.IModel;
import co.moviired.acquisition.common.model.network.HttpRequest;
import co.moviired.acquisition.common.util.UtilsHelper;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;
import java.lang.reflect.InvocationTargetException;

import static co.moviired.acquisition.common.util.ConstantsHelper.*;

/**
 * This class provide invoker for call rest services
 *
 * @param <P> is the properties class for third component
 * @param <D> is the DTO class for third component
 * @param <F> is the factory class for third component
 */
@Data
@Slf4j
public abstract class IProviderConnector<P extends IProviderProperties, D extends IModel, F extends IProviderFactory<P>> {

    private final ObjectMapper objectMapper;
    private final ReactiveConnector reactiveConnector;
    private final F factory;
    private final P properties;
    private final Class<?> dClass;
    private final GlobalProperties globalProperties;

    /**
     * @param globalPropertiesI global properties for current component
     * @param propertiesI       properties for third component
     * @param factoryI          factory for create bodys for call third component
     * @param objectMapperI     mapper for third request and responses
     * @param dClassI           class of DTO class for third component
     */
    public IProviderConnector(@NotNull GlobalProperties globalPropertiesI, @NotNull P propertiesI, @NotNull F factoryI, @NotNull ObjectMapper objectMapperI, Class<?> dClassI) {
        this.factory = factoryI;
        this.properties = propertiesI;
        this.globalProperties = globalPropertiesI;
        this.objectMapper = objectMapperI;
        this.dClass = dClassI;
        this.reactiveConnector = new ReactiveConnector(properties.getUrl(), properties.getConnectionTimeout(), properties.getReadTimeout());
    }

    /**
     * this method call a service in third component
     *
     * @return this method return the response of third component
     */
    @SuppressWarnings("unused")
    protected Mono<D> invoke(HttpRequest<D> httpRequest, String correlative, String identification, boolean... logsControl) {
        return Mono.just(httpRequest)
                .flatMap(req -> {
                    if (httpRequest.getBody() != null) {
                        return Mono.just(httpRequest.getBody());
                    } else {
                        return getDefaultInstance();
                    }
                })
                .flatMap(body -> {
                    httpRequest.setBody(body);
                    showLogsRequest(httpRequest.getPath(), httpRequest.getBody(), logsControl);
                    String request = (httpRequest.getMediaType().equals(MediaType.APPLICATION_XML))
                            ? httpRequest.getBody().toXml() : httpRequest.getBody().toJson();
                    return Mono.just(request);
                })
                .flatMap(request -> reactiveConnector.exchange(httpRequest.getHttpMethod(), httpRequest.getPath(), request,
                        String.class, httpRequest.getMediaType(), httpRequest.getHeaders())
                        .flatMap(stringResponse -> getResponse(stringResponse, correlative, identification, logsControl)))
                .onErrorResume(error -> {
                    UtilsHelper.assignCorrelative(globalProperties, correlative);
                    log.error(LBL_ERROR_THIRD, properties.getName().toUpperCase(), identification, error.getMessage());
                    return Mono.error(error);
                });
    }

    private Mono<D> getDefaultInstance() {
        try {
            //noinspection unchecked
            return Mono.just((D) dClass.getConstructor().newInstance());
        } catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
            return Mono.error(e);
        }
    }

    private void showLogsRequest(String path, D body, boolean... logsControl) {
        if (logsControl.length <= 0 || logsControl[0]) {
            log.info(LOG_THIRD_REQUEST, properties.getName().toUpperCase(), properties.getUrl() + path, body.protectedToString());
        } else {
            log.debug(LOG_THIRD_REQUEST, properties.getName().toUpperCase(), properties.getUrl() + path, body.protectedToString());
        }
    }

    private Mono<D> getResponse(Object stringResponse, String correlative, String identification, boolean... logsControl) {
        UtilsHelper.assignCorrelative(globalProperties, correlative);
        try {
            //noinspection unchecked,BlockingMethodInNonBlockingContext
            D response = this.objectMapper.readValue((String) stringResponse, (Class<D>) dClass);
            if (logsControl.length <= 1 || logsControl[1]) {
                log.info(LBL_RESPONSE, properties.getName().toUpperCase(), identification, response.protectedToString());
            } else {
                log.debug(LBL_RESPONSE, properties.getName().toUpperCase(), identification, response.protectedToString());
            }
            return Mono.just(response);
        } catch (Exception e) {
            log.error(LBL_ERROR_THIRD, properties.getName().toUpperCase(), identification, e.getMessage());
            return Mono.error(e);
        }
    }
}

