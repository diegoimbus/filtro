package com.moviired.controller;

import com.moviired.excepciones.ManagerException;
import com.moviired.helper.Constant;
import com.moviired.helper.Utilidad;
import com.moviired.model.Configurations;
import com.moviired.model.request.RequestFormat;
import com.moviired.model.response.Data;
import com.moviired.service.MoviiredCashApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;

@Slf4j
@RestController(value = "cashApi")
@RequestMapping("${spring.application.services.rest.uri}")
@SuppressWarnings({"unchecked", "unused"})
public class CashApi {

    private static final String NO_DISPONIBLE = "Servicio no disponible.";

    private final MoviiredCashApi iMoviiredCashApi;
    private final Configurations configurations;


    @Autowired
    public CashApi(
            @NotNull Configurations pconfigurations,
            @NotNull MoviiredCashApi moviiredCashApi) {
        super();
        this.configurations = pconfigurations;
        this.iMoviiredCashApi = moviiredCashApi;
    }

    @PostMapping(value = "${spring.application.methods.cashIn}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public final ResponseEntity<Data> cashIn(@RequestBody RequestFormat request,
                                             @PathVariable String phoneNumber,
                                             @RequestHeader(value = Constant.MERCHANT_ID) String merchantId,
                                             @RequestHeader(value = Constant.POS_ID) String posId,
                                             @RequestHeader(value = Constant.AUTHORIZATION) String userpass) {
        Data data;
        try {
            if (!configurations.isCashInEnabled()) {
                throw new ManagerException(Constant.NUMBER_TWO_NEGATIVE_EXCEPTION, Constant.TRANSACTION_ERROR, NO_DISPONIBLE);
            }

            RequestFormat requestFormat = RequestFormat.vCashIn(request, phoneNumber, userpass, merchantId, posId);
            data = this.iMoviiredCashApi.cashIn(requestFormat);

        } catch (Exception e) {
            data = Utilidad.generateErrorResponseCashIn(e);

        } finally {
            log.info("**************Finalizando el servicio de CashIn**************");
        }

        ResponseEntity<Data> resp = new ResponseEntity<>(data, HttpStatus.valueOf(Integer.parseInt(data.getCode())));
        data.setCode(null);
        return resp;
    }

    @GetMapping(value = "${spring.application.methods.cashOutPending}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public final ResponseEntity<Data> cashOutPending(@PathVariable String phoneNumber,
                                                     @RequestHeader(value = Constant.MERCHANT_ID) String merchantId,
                                                     @RequestHeader(value = Constant.POS_ID) String posId,
                                                     @RequestHeader(value = Constant.AUTHORIZATION) String userpass) {
        Data data;
        ResponseEntity<Data> dataResponseEntity;

        try {
            log.info("**************Iniciando el servicio de CashOutPending**************");
            if (!configurations.isCashOutPendingEnabled()) {
                throw new ManagerException(Constant.NUMBER_TWO_NEGATIVE_EXCEPTION, Constant.TRANSACTION_ERROR, NO_DISPONIBLE);
            }

            RequestFormat requestFormat = RequestFormat.vCashOutP(phoneNumber, userpass, merchantId, posId);

            data = this.iMoviiredCashApi.cashOutPending(requestFormat);

        } catch (Exception e) {
            data = Utilidad.generateErrorResponseCashIn(e);

        } finally {
            log.info("**************Finalizando el servicio de CashOutPending**************");
        }

        dataResponseEntity = new ResponseEntity<>(data, HttpStatus.valueOf(Integer.parseInt(data.getCode())));
        data.setCode(null);

        return dataResponseEntity;
    }

    @GetMapping(value = "${spring.application.methods.validateSubscriber}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public final ResponseEntity<Data> validateSubscriber(@PathVariable String phoneNumber,
                                                         @RequestHeader(value = Constant.MERCHANT_ID) String merchantId,
                                                         @RequestHeader(value = Constant.POS_ID) String posId,
                                                         @RequestHeader(value = Constant.AUTHORIZATION) String userpass) {
        Data data;
        ResponseEntity<Data> dataResponseEntity;

        try {
            log.info("**************Iniciando el servicio de ValidateSubscriber**************");
            if (configurations.isValidateSubscriberEnabled()) {
                throw new ManagerException(Constant.NUMBER_TWO_NEGATIVE_EXCEPTION, Constant.TRANSACTION_ERROR, NO_DISPONIBLE);
            }

            RequestFormat requestFormat = RequestFormat.vSubscriber(phoneNumber, userpass, merchantId, posId);

            data = this.iMoviiredCashApi.validateSubscriber(requestFormat);

        } catch (Exception e) {
            data = Utilidad.generateErrorResponseCashIn(e);
        } finally {
            log.info("**************Finalizando el servicio de ValidateSubscriber**************");
        }

        dataResponseEntity = new ResponseEntity<>(data, HttpStatus.valueOf(Integer.parseInt(data.getCode())));
        data.setCode(null);

        return dataResponseEntity;
    }


    @GetMapping(value = "${spring.application.methods.validateTransactions}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public final ResponseEntity<Data> validateTransactions(@PathVariable String correlationId,
                                                           @RequestHeader(value = Constant.MERCHANT_ID) String merchantId,
                                                           @RequestHeader(value = Constant.POS_ID) String posId,
                                                           @RequestHeader(value = Constant.AUTHORIZATION) String userpass) {
        Data data;
        ResponseEntity<Data> dataResponseEntity;

        try {
            log.info("**************Iniciando el servicio de ValidateTransactions**************");

            // Verificar si el servicio está habilitado
            if (configurations.isValidateTransactionsEnabled()) {
                throw new ManagerException(Constant.NUMBER_TWO_NEGATIVE_EXCEPTION, Constant.TRANSACTION_ERROR, NO_DISPONIBLE);
            }

            // Validar la entrada
            RequestFormat requestFormat = RequestFormat.vTransaction(correlationId, userpass, merchantId, posId);

            // Ejecutar la operación
            data = this.iMoviiredCashApi.validateTransaction(requestFormat);

        } catch (Exception e) {
            data = Utilidad.generateErrorResponseCashIn(e);
        } finally {
            log.info("**************Finalizando el servicio de ValidateTransactions**************");
        }

        dataResponseEntity = new ResponseEntity<>(data, HttpStatus.valueOf(Integer.parseInt(data.getCode())));
        data.setCode(null);

        return dataResponseEntity;
    }


    @GetMapping(value = "${spring.application.methods.validateUser}")
    public final ResponseEntity<Data> validateUser(@PathVariable String phoneNumber,
                                                   @PathVariable String amount,
                                                   @RequestHeader(value = "correlationId") String correlationId,
                                                   @RequestHeader(value = Constant.MERCHANT_ID) String merchantId,
                                                   @RequestHeader(value = Constant.POS_ID) String posId,
                                                   @RequestHeader(value = Constant.AUTHORIZATION) String userpass) {
        Data data;
        ResponseEntity<Data> dataResponseEntity;

        log.info("**************Iniciando el servicio de validateUser **************");
        Utilidad.assignCorrelative(correlationId);
        RequestFormat requestFormat = new RequestFormat();
        requestFormat.setPhoneNumber(phoneNumber);
        requestFormat.setAmount(Integer.parseInt(amount));
        requestFormat.setMerchantId(merchantId);
        String[] authorization = userpass.split(":");
        requestFormat.setUsuario(authorization[0]);
        requestFormat.setMpin(authorization[1]);

        data = this.iMoviiredCashApi.validateUser(requestFormat);

        dataResponseEntity = new ResponseEntity<>(data, HttpStatus.valueOf(Integer.parseInt(data.getCode())));
        data.setCode(Constant.TRANSACTION_OK_00);

        log.info("**************Finalizando el servicio de ValidateUser**************");
        return dataResponseEntity;
    }

}

