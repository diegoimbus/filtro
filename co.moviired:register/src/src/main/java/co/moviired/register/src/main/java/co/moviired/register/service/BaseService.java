package co.moviired.register.service;

import co.moviired.base.domain.StatusCode;
import co.moviired.base.domain.exception.DataException;
import co.moviired.base.domain.exception.ParsingException;
import co.moviired.base.util.Security;
import co.moviired.connector.connector.ReactiveConnector;
import co.moviired.register.config.StatusCodeConfig;
import co.moviired.register.domain.dto.MahindraDTO;
import co.moviired.register.domain.dto.RegisterDTO;
import co.moviired.register.domain.enums.register.ServiceStatusCode;
import co.moviired.register.domain.model.entity.PendingUser;
import co.moviired.register.domain.model.register.ResponseStatus;
import co.moviired.register.exceptions.ServiceException;
import co.moviired.register.helper.SignatureHelper;
import co.moviired.register.helper.UtilsHelper;
import co.moviired.register.properties.GlobalProperties;
import co.moviired.register.properties.MahindraProperties;
import co.moviired.register.repository.IPendingUserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.google.gson.Gson;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static co.moviired.register.helper.ConstantsHelper.*;

@Data
@Slf4j
public class BaseService implements Serializable {

    private final GlobalProperties globalProperties;
    private final StatusCodeConfig statusCodeConfig;
    private final SignatureHelper signatureHelper;
    private final IPendingUserRepository pendingUserRepository;

    public BaseService(@NotNull GlobalProperties pGlobalProperties,
                       @NotNull StatusCodeConfig pStatusCodeConfig,
                       @NotNull SignatureHelper pSignatureHelper,
                       @NotNull IPendingUserRepository pPendingUserRepository) {
        super();
        this.globalProperties = pGlobalProperties;
        this.statusCodeConfig = pStatusCodeConfig;
        this.signatureHelper = pSignatureHelper;
        this.pendingUserRepository = pPendingUserRepository;
    }

    protected String logsStart(String requestType, String body) {
        return logsStart(requestType, body, null);
    }

    protected String logsStart(String requestType, String body, String correlativeI) {
        String correlative = UtilsHelper.asignarCorrelativo(correlativeI);
        log.info(EMPTY_STRING);
        log.info(LBL_START);
        log.info(LBL_REQUEST_SERVICE_TYPE, requestType, body);
        return correlative;
    }

    protected void logsEnd(Object response) {
        String component = this.globalProperties.getName().toUpperCase();
        log.info(LBL_RESPONSE_SERVICE, component, response);
        log.info(LBL_END);
        log.info(EMPTY_STRING);
    }

    // HANDLE ERROR ****************************************************************************************************

    protected void handleThrowableError(@NotNull String message, Throwable e, RegisterDTO response) {
        log.error(message, e.getMessage());
        if (e instanceof ServiceException) {
            response.setStatus(new ResponseStatus(((ServiceException) e).getCode(), e.getMessage()));
        } else {
            response.setStatus(new ResponseStatus(
                    this.getStatusCodeConfig().of(ServiceStatusCode.SERVER_ERROR.getStatusCode()).getCode(),
                    this.getStatusCodeConfig().of(ServiceStatusCode.SERVER_ERROR.getStatusCode()).getMessage()));
        }
    }

    protected void handleExceptionError(@NotNull String message, Throwable e, RegisterDTO response) {
        log.error(message, e.getMessage());
        response.setStatus(new ResponseStatus(
                this.getStatusCodeConfig().of(ServiceStatusCode.SERVER_ERROR.getStatusCode()).getCode(),
                this.getStatusCodeConfig().of(ServiceStatusCode.SERVER_ERROR.getStatusCode()).getMessage()));
    }

    protected boolean validateSignaturePendingUser(PendingUser pendingUser) {
        try {
            log.info(LOG_VALIDATING_SIGNATURE_PENDING_USER, pendingUser.getId());
            signatureHelper.validate(pendingUser);
            return true;
        } catch (ParsingException e) {
            log.error(LOG_ERROR_VALIDATING_SIGNATURE_OF_PENDING_USER, pendingUser.getType(), pendingUser.getId(), e.getMessage());
        } catch (DataException e) {
            if (pendingUser.getSignature().equalsIgnoreCase(STRING_LINE)) {
                log.error(LOG_PENDING_USER_NOT_HAS_SIGNATURE, pendingUser.getType(), pendingUser.getId(), e.getMessage());
            } else {
                log.error(LOG_PENDING_USER_IS_ALTERED, pendingUser.getType(), pendingUser.getId(), e.getMessage());
                pendingUser.setAltered(true);
                this.pendingUserRepository.save(pendingUser);
            }
        }
        return false;
    }

    protected PendingUser signPendingUser(PendingUser pendingUser) {
        try {
            pendingUser.setSignature(this.signatureHelper.sign(pendingUser, false));
        } catch (Exception | ParsingException e) {
            log.error(LOG_ERROR_SIGN_PENDING_USER, new Gson().toJson(pendingUser));
            pendingUser.setSignature(STRING_LINE);
        }
        return pendingUser;
    }

    protected StatusCode inactivePendingUsers(List<PendingUser> pendingUsers) {
        StatusCode statusCode = null;
        if (pendingUsers.isEmpty()) {
            statusCode = this.getStatusCodeConfig().of(ServiceStatusCode.NOT_FOUND.getStatusCode());
        } else {
            for (PendingUser pendingUser : pendingUsers) {
                if (!validateSignaturePendingUser(pendingUser)) {
                    statusCode = this.getStatusCodeConfig().of(ServiceStatusCode.SERVER_ERROR.getStatusCode());
                } else {
                    pendingUser.setStatus(false);
                    pendingUser.setDateUpdate(new Date());
                    signPendingUser(pendingUser);
                    this.pendingUserRepository.save(pendingUser);
                }
            }

            if (statusCode == null) {
                statusCode = this.getStatusCodeConfig().of(ServiceStatusCode.SUCCESS.getStatusCode());
            }
        }
        return statusCode;
    }

    // INVOKERS ********************************************************************************************************

    protected Mono<MahindraDTO> invokeMahindra(ReactiveConnector mahindraClient, MahindraProperties mahindraProperties, XmlMapper xmlMapper, MahindraDTO mahindraDTO, String correlative)
            throws CloneNotSupportedException, JsonProcessingException {
        log.info(LOG_THIRD_REQUEST, MAHINDRA_COMPONENT, mahindraProperties.getUrl(), Security.printIgnore(mahindraDTO.toString(), "pin", "mpin"));
        return mahindraClient.exchange(HttpMethod.POST, mahindraProperties.getUrl(),
                xmlMapper.writeValueAsString(mahindraDTO), String.class, MediaType.APPLICATION_XML, new HashMap<>())
                .flatMap(response -> {
                    try {
                        UtilsHelper.asignarCorrelativo(correlative);
                        MahindraDTO mahindraDTOResponse = xmlMapper.readValue((String) response, MahindraDTO.class);
                        log.info(LBL_RESPONSE_SERVICE, MAHINDRA_COMPONENT.toUpperCase(), xmlMapper.writeValueAsString(mahindraDTOResponse));
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

    // ON RESPONSES ****************************************************************************************************

    protected Mono<MahindraDTO> onMahindraResponse(MahindraDTO mahindraResponse, String correlative) {
        UtilsHelper.asignarCorrelativo(correlative);
        if (mahindraResponse.getTxnStatus().matches(SUCCESS_CODE)) {
            return Mono.just(mahindraResponse);
        } else {
            return Mono.error(new ServiceException(mahindraResponse.getMessage(), mahindraResponse.getTxnStatus(), MAHINDRA_COMPONENT));
        }
    }
}

