package com.moviired.controller;


import co.moviired.base.util.Generator;
import com.moviired.helper.Constant;
import com.moviired.model.request.CashOutRequest;
import com.moviired.model.response.Data;
import com.moviired.service.CashOutService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.function.Function;

@Slf4j
@RestController
@RequestMapping("${spring.application.services.rest.uri-baloto}")
public class CashOutControllerBaloto {

    private final CashOutService cashOutService;

    public CashOutControllerBaloto(CashOutService pCashOutService) {
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
     * service pendingByMerchant (buscar cashOuts pendientes por un merchant).
     *
     * @param userPass,agentCode,posId,request
     * @return ResponseEntity<Data>
     */
    @GetMapping(value = "${spring.application.services.rest.cash-out.pending-by-merchant-phoneNumber-baloto}")
    public ResponseEntity<Data> pendingByMerchand(@NotEmpty @RequestHeader(value = Constant.AUTHORIZATION) String userPass,
                                                  @RequestHeader(value = Constant.MERCHANT_ID) @NotNull String agentCode,
                                                  @RequestHeader(value = Constant.POS_ID) @NotNull String posId,
                                                  @PathVariable("phoneNumber") Long phoneNumber) {
        CashOutRequest request = new CashOutRequest();
        request.setPhoneNumber(phoneNumber.toString());
        request.setAgentCode(agentCode);
        return responseWrapper(request, userPass, cashOutService::pendingByMerchant);
    }

    /**
     * service initialize (Inicia cashOut).
     *
     * @param userPass,agentCode,posId,request
     * @return ResponseEntity<Data>
     */
    @PostMapping(value = "${spring.application.services.rest.cash-out.initialize-baloto}")
    public ResponseEntity<Data> initialize(@RequestHeader(value = Constant.AUTHORIZATION) @NotNull String userPass,
                                           @RequestHeader(value = Constant.MERCHANT_ID) @NotNull String agentCode,
                                           @RequestHeader(value = Constant.POS_ID) @NotNull String posId,
                                           @RequestBody CashOutRequest request
    ) {

        request.setAgentCode(agentCode);
        return responseWrapper(request, userPass, cashOutService::initialize);
    }


    /**
     * service complete (completa cashOut).
     *
     * @param userPass,agentCode,posId,request
     * @return ResponseEntity<Data>
     */
    @PostMapping(value = "${spring.application.services.rest.cash-out.complete-baloto}")
    public ResponseEntity<Data> complete(@RequestHeader(value = Constant.AUTHORIZATION) @NotNull String userPass,
                                         @RequestHeader(value = Constant.MERCHANT_ID) @NotNull String agentCode,
                                         @PathVariable("phoneNumber") @NotNull String phoneNumber,
                                         @RequestHeader(value = Constant.POS_ID) @NotNull String posId,
                                         @RequestBody CashOutRequest request) {

        request.setAgentCode(agentCode);
        request.setPhoneNumber(phoneNumber);
        return responseWrapper(request, userPass, cashOutService::complete);
    }

    /**
     * service reverse (reversa cashOut).
     *
     * @param userPass,agentCode,posId,request
     * @return ResponseEntity<Data>
     */
    @PostMapping(value = "${spring.application.services.rest.cash-out.reverse-baloto}")
    public ResponseEntity<Data> reverse(@RequestHeader(value = Constant.AUTHORIZATION) @NotNull String userPass,
                                        @RequestHeader(value = Constant.MERCHANT_ID) @NotNull String agentCode,
                                        @RequestHeader(value = Constant.POS_ID) @NotNull String posId,
                                        @RequestBody CashOutRequest request) {

        request.setAgentCode(agentCode);
        return responseWrapper(request, userPass, cashOutService::reverse);
    }

    /**
     * metodo responseWrapper (procesa entrada del service).
     *
     * @param userPass,agentCode,posId,request
     * @return ResponseEntity<Data>
     */
    private ResponseEntity<Data> responseWrapper(CashOutRequest request, String userPass, Function<CashOutRequest, Data> action) {
        try {

            String[] userPassword = userPass.split(":");
            request.setUserLogin(userPassword[0]);
            request.setPin(userPassword[1]);

            if (null == request.getCorrelationId() || request.getCorrelationId().isEmpty()) {
                request.setCorrelationId(String.valueOf(Generator.correlationId()));
            }

            assignCorrelative(request.getCorrelationId());

            return ResponseEntity.ok(action.apply(request));
        } catch (Exception e) {
            log.error(e.getMessage());
            Data data = new Data();
            data.setErrorMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(data);
        }
    }

    /**
     * metodo assignCorrelative (asignacion de correlativo).
     *
     * @param correlative
     * @return ResponseEntity<Data>
     */
    private void assignCorrelative(String correlative) {

        MDC.putCloseable("correlation-id", correlative);
        MDC.putCloseable("component", "cash-out");

    }

}

