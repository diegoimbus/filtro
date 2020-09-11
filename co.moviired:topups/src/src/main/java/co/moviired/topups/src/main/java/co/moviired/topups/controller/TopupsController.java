package co.moviired.topups.controller;

import co.moviired.topups.model.domain.dto.recharge.IOperatorResponse;
import co.moviired.topups.model.domain.dto.recharge.IRechargeIntegrationResponse;
import co.moviired.topups.model.domain.dto.recharge.request.RechargeIntegrationHeaderRequest;
import co.moviired.topups.model.domain.dto.recharge.request.RechargeIntegrationRequest;
import co.moviired.topups.model.enums.OperationType;
import co.moviired.topups.service.OperatorService;
import co.moviired.topups.service.TopupsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;

/**
 * @author carlossaul.ramirez
 * @version 0.0.1
 */

@Slf4j
@RestController
public class TopupsController {

    private final TopupsService rechargeService;
    private final OperatorService operatorService;

    public TopupsController(
            @NotNull TopupsService rechargeService,
            @NotNull OperatorService operatorService
    ) {
        super();
        this.rechargeService = rechargeService;
        this.operatorService = operatorService;
    }

    @GetMapping(value = "${spring.application.services.ping}")
    public Mono<String> ping() {
        log.info("I'm Alive!");
        return Mono.just("I'm Alive!");
    }

    @GetMapping(value = {"${spring.application.services.operatorsType}/{type}"})
    public Mono<IOperatorResponse> operatorsByType(@PathVariable(value = "type") String type,
                                                   @RequestHeader(value = "merchantId", required = false) @NotNull String merchantId,
                                                   @RequestHeader(value = "posId", required = false) @NotNull String posId,
                                                   @RequestHeader(value = "correlationId", required = false)String correlationId){

        Mono<String> operatorIdMono =  Mono.just(type);

        final StringBuilder logIdent = new StringBuilder("[")
                .append(rechargeService.getCorrelationId(correlationId, null))
                .append("]");
        log.info("{} ********* START QUERY OPERATORS BY TYPE *********  ",logIdent);
        log.info("{} \"type\":\"{}\"", logIdent,type);
        log.info("{} \"merchantId\":\"{}\",\"posId\":\"{}\"", logIdent,merchantId, posId);
        return operatorService.getOperatorsByType(operatorIdMono, logIdent.toString());
    }


    @PostMapping(value = "${spring.application.services.recharge}/{referenceNumber}")
    public Mono<IRechargeIntegrationResponse> recharge(
            @RequestHeader(value = "Authorization") String authorization,
            @RequestHeader(value = "Content-Type") String contentType,
            @RequestHeader(value = "merchantId") String merchantId,
            @RequestHeader(value = "posId") String posId,
            @PathVariable(value = "referenceNumber") String referenceNumber,
            @RequestBody RechargeIntegrationRequest rechargeRequest) {

        // LOG
        rechargeRequest.setCorrelationId(rechargeService.getCorrelationId(rechargeRequest.getCorrelationId(),rechargeRequest.getIp()));
        final long componentDate = System.currentTimeMillis();
        final StringBuilder logIdent = new StringBuilder("[")
                .append(rechargeRequest.getCorrelationId())
                .append("]");
        log.info(" {} ********* START RECHARGE OPERATION *********  ",logIdent);
        log.info("{}  \"merchantId\":\"{}\",\"posId\":\"{}\", \"referenceNumber\":\"{}\"", logIdent,merchantId, posId, referenceNumber);

        // Generar el Header
        RechargeIntegrationHeaderRequest headerRequest = RechargeIntegrationHeaderRequest.builder()
                .authorization(authorization)
                .componentDate(componentDate)
                .contentType(contentType)
                .merchantId(merchantId)
                .posId(posId)
                .referenceNumber(referenceNumber)
                .build();
        return rechargeService.processTopups(logIdent.toString(), OperationType.RTMMREQ, headerRequest, Mono.just(rechargeRequest));
    }

}

