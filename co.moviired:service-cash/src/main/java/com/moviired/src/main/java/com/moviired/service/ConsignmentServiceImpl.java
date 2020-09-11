package com.moviired.service;

import co.moviired.base.domain.StatusCode;
import co.moviired.base.domain.exception.DataException;
import co.moviired.base.domain.exception.ServiceException;
import co.moviired.base.helper.CryptoHelper;
import co.moviired.base.util.Generator;
import co.moviired.connector.connector.ReactiveConnector;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.moviired.client.mahindra.command.CommandConsultBalanceRequest;
import com.moviired.client.mahindra.command.CommandConsultBalanceResponse;
import com.moviired.conf.StatusCodeConfig;
import com.moviired.helper.Constant;
import com.moviired.model.Configurations;
import com.moviired.model.response.impl.ResponseConsultBalance;
import com.moviired.properties.CmdConsultBalanceProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;

/**
 * Copyright @2019 MOVIIRED. Todos los derechos reservados.
 *
 * @author carlossaul.ramirez
 */

@Service
@Slf4j
public class ConsignmentServiceImpl implements Serializable {

    private static final long serialVersionUID = -1143184049994629351L;

    private static final String STARTED = "STARTED";
    private static final String FINISHED = "FINISHED";
    private static final String LOG_FORMATED = " {} {} {}";
    private static final String LOG_COMPONENT = "PROCESS SRV-CASH";

    private static final String LOG_ERROR_PATTERN = " [Error:{}]";

    private final XmlMapper xmlMapper = new XmlMapper();

    private final ReactiveConnector mahindraClient; // NOSONAR

    private final Configurations configurations; // NOSONAR

    private final StatusCodeConfig statusCodeConfig;

    private final CryptoHelper cryptoHelperAuthorization;

    private final CmdConsultBalanceProperties cmdConsultBalanceProperties;

    public ConsignmentServiceImpl(
            @NotNull @Qualifier("mahindraClient") ReactiveConnector pMahindraClient,
            @NotNull Configurations pConfigurations,
            @NotNull @Qualifier("statusCodeConfig") StatusCodeConfig pStatusCodeConfig,
            @NotNull @Qualifier("cryptoHelperAuthorization") CryptoHelper pCryptoHelperAuthorization,
            CmdConsultBalanceProperties pCmdConsultBalanceProperties) {
        this.mahindraClient = pMahindraClient;
        this.configurations = pConfigurations;
        this.statusCodeConfig = pStatusCodeConfig;
        this.cryptoHelperAuthorization = pCryptoHelperAuthorization;
        this.cmdConsultBalanceProperties = pCmdConsultBalanceProperties;
    }


    private String asignarCorrelativo(String correlativo) {
        String correlative = correlativo;
        if (correlativo == null || correlativo.isEmpty()) {
            correlative = String.valueOf(Generator.correlationId());
        }

        MDC.putCloseable("correlation-id", correlative);
        MDC.putCloseable("component", this.configurations.getApplicationName());
        return correlative;
    }


    /**
     * metodo consultBalance (retorna el balance del usuario pasado por authorization)
     *
     * @param authorization,correlationId
     * @return ResponseEntity<ResponseConsultBalance>
     */
    public ResponseEntity<ResponseConsultBalance> consultBalance(String authorization, String correlationId) {
        ResponseConsultBalance response = ResponseConsultBalance.builder().errorCode("400").errorType("0").build();
        int httpStatus = Constant.HTTP_STATUS_200;

        // ASIGNAR IP Y CORRELATIVO A LA PETICIÓN
        correlationId = this.asignarCorrelativo(correlationId);
        log.info(LOG_FORMATED, LOG_COMPONENT, STARTED, "consultBalance");

        try {
            // validate required and optional fields
            String decodeBase64Authorization = new String(Base64.decodeBase64(authorization.getBytes()));
            String[] auth = decodeBase64Authorization.split(":");
            if (auth.length != 2) {
                throw new DataException("400", "Error en parametro de authorización");
            }

            auth[1] = cryptoHelperAuthorization.decoder(auth[1]);

            CommandConsultBalanceRequest request = CommandConsultBalanceRequest.builder()
                    .type(cmdConsultBalanceProperties.getTypeSubscriber())
                    .msisdn(auth[0])
                    .provider(cmdConsultBalanceProperties.getProvider())
                    .payid(cmdConsultBalanceProperties.getPayId())
                    .mpin(auth[1]).build();

            String xmlRequest = xmlMapper.writeValueAsString(request).toUpperCase();

            String result = (String) mahindraClient.post(xmlRequest, String.class, MediaType.APPLICATION_XML, null).block();

            //CONSULTA CONTRA SUBSCRIBER
            CommandConsultBalanceResponse bodyResult = xmlMapper.readValue(result, CommandConsultBalanceResponse.class);
            StatusCode statusCode = statusCodeConfig.of(bodyResult.getTxnStatus(), bodyResult.getMessage());
            if (!StatusCode.Level.SUCCESS.equals(statusCode.getLevel())) {
                //CONSULTA CONTRA CHANNEL
                request.setType(cmdConsultBalanceProperties.getTypeChannel());
                xmlRequest = xmlMapper.writeValueAsString(request).toUpperCase();
                result = (String) mahindraClient.post(xmlRequest, String.class, MediaType.APPLICATION_XML, null).block();
                //CONSULTA CONTRA SUBSCRIBER
                bodyResult = xmlMapper.readValue(result, CommandConsultBalanceResponse.class);
            }

            statusCode = statusCodeConfig.of(bodyResult.getTxnStatus(), bodyResult.getMessage());
            if (StatusCode.Level.SUCCESS.equals(statusCode.getLevel())) {
                response.setErrorCode(statusCode.getCode());
                response.setBalance(bodyResult.getBalance());
                response.setErrorMessage(statusCode.getMessage());
                httpStatus = Constant.HTTP_STATUS_200;
                response.setErrorMessage(statusCode.getMessage());
                response.setCorrelationId(correlationId);
                response.setTransactionDate(new Date());
                response.setTransactionId(bodyResult.getTxnid());
            } else {
                response.setErrorCode(statusCode.getCode());
                response.setBalance(bodyResult.getBalance());
                response.setErrorMessage(statusCode.getMessage());
                response.setCorrelationId(correlationId);
                response.setTransactionDate(new Date());
            }

        } catch (ServiceException | IOException e) {
            log.error(LOG_ERROR_PATTERN, e.getMessage());
            response.setErrorMessage(e.getMessage());
            httpStatus = Constant.HTTP_STATUS_400;
        }
        log.info(LOG_FORMATED, LOG_COMPONENT, FINISHED, "consultBalance");
        return new ResponseEntity<>(response, HttpStatus.valueOf(httpStatus));
    }


}

