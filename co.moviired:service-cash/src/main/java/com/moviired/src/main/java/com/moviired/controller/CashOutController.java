package com.moviired.controller;

import co.moviired.base.util.Generator;
import com.moviired.helper.Constant;
import com.moviired.helper.Utilidad;
import com.moviired.model.request.CashOutRequest;
import com.moviired.model.response.Data;
import com.moviired.model.response.NetworkResponse;
import com.moviired.service.CashOutService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.function.Function;

@Slf4j
@RestController
@RequestMapping("${spring.application.services.rest.uri}")
public class CashOutController {

    private final CashOutService cashOutService;

    public CashOutController(@NotNull CashOutService pCashOutService) {
        this.cashOutService = pCashOutService;
    }

    /**
     * Metodo ping.
     *
     * @param
     * @return ResponseEntity<String>.
     */
    @GetMapping(value = "${spring.application.services.rest.cash-out.ping}")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("OK");
    }

    /**
     * service findNetworks.
     *
     * @param request
     * @return ResponseEntity<List < NetworkResponse>>
     */
    @PostMapping(value = "${spring.application.services.rest.cash-out.find-networks}")
    public ResponseEntity<List<NetworkResponse>> findNetworks(@RequestBody CashOutRequest request) {
        Utilidad.assignCorrelative(String.valueOf(Generator.correlationId()));
        return ResponseEntity.ok(cashOutService.findNetworks(request));
    }

    /**
     * service findCostByNetwork.
     *
     * @param agentCode
     * @return ResponseEntity<Data>
     */
    @GetMapping(value = "${spring.application.services.rest.cash-out.cost-tx-network}")
    public ResponseEntity<Data> findCostByNetwork(@PathVariable("agentCode") String agentCode) {
        Utilidad.assignCorrelative(String.valueOf(Generator.correlationId()));
        return ResponseEntity.ok(cashOutService.findCostByNetwork(agentCode));
    }


    /**
     * service pendingBySuscriber (buscar cashOuts pendientes por un subscriber en una red).
     *
     * @param userPass,agentCode,posId,request
     * @return ResponseEntity<Data>
     */
    @PostMapping(value = "${spring.application.services.rest.cash-out.pending-by-subscriber}")
    public ResponseEntity<Data> pendingBySuscriber(@Valid @RequestHeader(value = Constant.AUTHORIZATION) String userPass,
                                                   @RequestHeader(value = Constant.MERCHANT_ID) @NotNull String agentCode,
                                                   @RequestHeader(value = Constant.POS_ID) @NotNull String posId,
                                                   @Valid @RequestBody CashOutRequest request) {

        request.setAgentCode(agentCode);
        ResponseEntity<Data> response = responseWrapper(request, userPass, cashOutService::pendingBySubscriber);
        ResponseEntity<Data> resp = new ResponseEntity<>(response.getBody(), HttpStatus.valueOf(Integer.parseInt(response.getBody().getCode())));
        response.getBody().setCode(Constant.TRANSACTION_OK_00);
        return resp;
    }

    /**
     * service pendingByMerchant (buscar cashOuts pendientes por un merchant).
     *
     * @param userPass,agentCode,posId,request
     * @return ResponseEntity<Data>
     */
    @PostMapping(value = "${spring.application.services.rest.cash-out.pending-by-merchant}")
    public ResponseEntity<Data> pendingByMerchant(@NotEmpty @RequestHeader(value = Constant.AUTHORIZATION) String userPass,
                                                  @RequestHeader(value = Constant.MERCHANT_ID) @NotNull String agentCode,
                                                  @RequestHeader(value = Constant.POS_ID) @NotNull String posId,
                                                  @Valid @RequestBody CashOutRequest request) {
        request.setAgentCode(agentCode);
        ResponseEntity<Data> response = responseWrapper(request, userPass, cashOutService::pendingByMerchant);
        ResponseEntity<Data> resp = new ResponseEntity<>(response.getBody(), HttpStatus.valueOf(Integer.parseInt(response.getBody().getCode())));
        response.getBody().setCode(Constant.TRANSACTION_OK_00);
        return resp;
    }

    /**
     * service initialize (Inicia cashOut).
     *
     * @param userPass,agentCode,posId,request
     * @return ResponseEntity<Data>
     */
    @PostMapping(value = "${spring.application.services.rest.cash-out.initialize}")
    public ResponseEntity<Data> initialize(@NotEmpty @RequestHeader(value = Constant.AUTHORIZATION) @NotNull String userPass,
                                           @RequestHeader(value = Constant.MERCHANT_ID) @NotNull String agentCode,
                                           @RequestHeader(value = Constant.POS_ID) @NotNull String posId,
                                           @Valid @RequestBody CashOutRequest request
    ) {
        request.setAgentCode(agentCode);
        ResponseEntity<Data> response = responseWrapper(request, userPass, cashOutService::initialize);
        ResponseEntity<Data> resp = new ResponseEntity<>(response.getBody(), HttpStatus.valueOf(Integer.parseInt(response.getBody().getCode())));
        response.getBody().setCode(Constant.TRANSACTION_OK_00);
        return resp;
    }


    /**
     * service pendingByMerchant (buscar cashOuts pendientes por un merchant).
     *
     * @param userPass,agentCode,posId,request
     * @return ResponseEntity<Data>
     */
    @PostMapping(value = "${spring.application.services.rest.cash-out.pending-by-merchant-phoneNumber}")
    public ResponseEntity<Data> pendingByMerchant(@NotEmpty @RequestHeader(value = Constant.AUTHORIZATION) String userPass,
                                                  @RequestHeader(value = Constant.MERCHANT_ID) @NotNull String agentCode,
                                                  @RequestHeader(value = Constant.POS_ID) @NotNull String posId,
                                                  @PathVariable("phoneNumber") Long phoneNumber,
                                                  @Valid @RequestBody CashOutRequest pendingByMerchandRequest) {

        pendingByMerchandRequest.setPhoneNumber(phoneNumber.toString());
        pendingByMerchandRequest.setAgentCode(agentCode);
        ResponseEntity<Data> response = responseWrapper(pendingByMerchandRequest, userPass, cashOutService::pendingByMerchant);
        ResponseEntity<Data> resp = new ResponseEntity<>(response.getBody(), HttpStatus.valueOf(Integer.parseInt(response.getBody().getCode())));
        response.getBody().setCode(Constant.TRANSACTION_OK_00);
        return resp;
    }


    /**
     * service complete (completa cashOut).
     *
     * @param userPass,agentCode,posId,request
     * @return ResponseEntity<Data>
     */
    @PostMapping(value = "${spring.application.services.rest.cash-out.complete}")
    public ResponseEntity<Data> complete(@RequestHeader(value = Constant.AUTHORIZATION) @NotNull String userPass,
                                         @RequestHeader(value = Constant.MERCHANT_ID) @NotNull String agentCode,
                                         @PathVariable("phoneNumber") @NotNull String phoneNumber,
                                         @RequestHeader(value = Constant.POS_ID) @NotNull String posId,
                                         @Valid @RequestBody CashOutRequest requestCashOutComplete) {

        requestCashOutComplete.setAgentCode(agentCode);
        requestCashOutComplete.setPhoneNumber(phoneNumber);
        requestCashOutComplete.setCorrelationId(String.valueOf(Generator.correlationId()));
        requestCashOutComplete.setPosId(posId);
        ResponseEntity<Data> response = responseWrapper(requestCashOutComplete, userPass, cashOutService::complete);
        ResponseEntity<Data> resp = new ResponseEntity<>(response.getBody(), HttpStatus.valueOf(Integer.parseInt(response.getBody().getCode())));
        response.getBody().setCode(Constant.TRANSACTION_OK_00);
        return resp;
    }


    /**
     * service decline (declina cashOut).
     *
     * @param userPass,agentCode,posId,request
     * @return ResponseEntity<Data>
     */
    @PostMapping(value = "${spring.application.services.rest.cash-out.decline}")
    public ResponseEntity<Data> decline(@RequestHeader(value = Constant.AUTHORIZATION) @NotNull String userPass,
                                        @RequestHeader(value = Constant.MERCHANT_ID) @NotNull String agentCode,
                                        @RequestHeader(value = Constant.POS_ID) @NotNull String posId,
                                        @Valid @RequestBody CashOutRequest request) {
        request.setAgentCode(agentCode);
        ResponseEntity<Data> response = responseWrapper(request, userPass, cashOutService::decline);
        ResponseEntity<Data> resp = new ResponseEntity<>(response.getBody(), HttpStatus.valueOf(Integer.parseInt(response.getBody().getCode())));
        response.getBody().setCode(Constant.TRANSACTION_OK_00);
        return resp;
    }


    /**
     * service reverse (reversa cashOut).
     *
     * @param userPass,agentCode,posId,request
     * @return ResponseEntity<Data>
     */
    @PostMapping(value = "${spring.application.services.rest.cash-out.reverse}")
    public ResponseEntity<Data> reverse(@RequestHeader(value = Constant.AUTHORIZATION) @NotNull String userPass,
                                        @RequestHeader(value = Constant.MERCHANT_ID) @NotNull String agentCode,
                                        @RequestHeader(value = Constant.POS_ID) @NotNull String posId,
                                        @Valid @RequestBody CashOutRequest request) {

        request.setAgentCode(agentCode);
        ResponseEntity<Data> response = responseWrapper(request, userPass, cashOutService::reverse);
        ResponseEntity<Data> resp = new ResponseEntity<>(response.getBody(), HttpStatus.valueOf(Integer.parseInt(response.getBody().getCode())));
        response.getBody().setCode(Constant.TRANSACTION_OK_00);
        return resp;
    }

    /**
     * metodo responseWrapper (procesa entrada del service).
     *
     * @param userPass,agentCode,posId,request
     * @return ResponseEntity<Data>
     */
    //procesa del usuario
    private ResponseEntity<Data> responseWrapper(CashOutRequest request, String userPass, Function<CashOutRequest, Data> action) {
        try {
            String[] userPassword = userPass.split(":");
            request.setUserLogin(userPassword[0]);
            request.setPin(userPassword[1]);
            //prueba
            return ResponseEntity.ok(action.apply(request));
        } catch (Exception e) {
            log.error(e.getMessage());
            Data data = new Data();
            data.setErrorMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(data);
        }
    }


}

