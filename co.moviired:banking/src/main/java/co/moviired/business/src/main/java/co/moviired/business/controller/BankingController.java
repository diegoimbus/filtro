package co.moviired.business.controller;

import co.moviired.business.domain.dto.banking.request.RequestFormatBanking;
import co.moviired.business.domain.dto.banking.response.Response;
import co.moviired.business.domain.dto.banking.response.ResponseAgreement;
import co.moviired.business.domain.enums.OperationType;
import co.moviired.business.domain.enums.Seller;
import co.moviired.business.service.BankingService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;

@Slf4j
@AllArgsConstructor
@RestController("BankingController")
@RequestMapping("${server.servlet.context-path}")
public class BankingController {

    private final BankingService bankingBusinesssService;

    @GetMapping(value = "${spring.application.services.rest.ping}")
    public final Mono<String> ping() {
        // Si llega la petición return I'm Alive!
        return Mono.just("I'm Alive!");
    }

    @GetMapping(value = "${spring.application.services.rest.banking.allAgreements}")
    public final Mono<ResponseAgreement> getFilterAgreements(@PathVariable @NotNull Seller source,
                                                             @RequestHeader(required = false) Integer id,
                                                             @RequestHeader(required = false) String textFilter,
                                                             @RequestHeader(value = "Authorization") @NotNull String userpass) {
        return bankingBusinesssService.listAgreements(source, userpass, id, textFilter);
    }

    // *** Banco AGRARIO ***

    @PostMapping(value = "${spring.application.services.rest.banking.money}")
    public final Mono<Response> query(@RequestBody Mono<RequestFormatBanking> requestBanking,
                                      @PathVariable @NotNull String referenceNumber,
                                      @RequestHeader(value = "merchantId") @NotNull String merchantId,
                                      @RequestHeader(value = "posId") @NotNull String posId,
                                      @RequestHeader(value = "Authorization") @NotNull String userpass,
                                      @RequestHeader(value = "correlationId", required = false) String correlationId) {
        return bankingBusinesssService.service(OperationType.QUERY, requestBanking, referenceNumber, merchantId, posId, userpass, correlationId);
    }

    @PutMapping(value = "${spring.application.services.rest.banking.money}")
    public final Mono<Response> deposit(@NotNull @RequestBody Mono<RequestFormatBanking> requestBanking,
                                        @PathVariable @NotNull String referenceNumber,
                                        @RequestHeader(value = "merchantId") @NotNull String merchantId,
                                        @RequestHeader(value = "posId") @NotNull String posId,
                                        @RequestHeader(value = "Authorization") @NotNull String userpass,
                                        @RequestHeader(value = "correlationId", required = false) String correlationId) {
        return bankingBusinesssService.service(OperationType.DEPOSIT, requestBanking, referenceNumber, merchantId, posId, userpass, correlationId);
    }

    @DeleteMapping(value = "${spring.application.services.rest.banking.money}")
    public final Mono<Response> cashOut(@RequestBody Mono<RequestFormatBanking> requestBanking,
                                        @PathVariable @NotNull String referenceNumber,
                                        @RequestHeader(value = "merchantId") @NotNull String merchantId,
                                        @RequestHeader(value = "posId") @NotNull String posId,
                                        @RequestHeader(value = "Authorization") @NotNull String userpass,
                                        @RequestHeader(value = "correlationId", required = false) String correlationId) {
        return bankingBusinesssService.service(OperationType.WITHDRAWAL, requestBanking, referenceNumber, merchantId, posId, userpass, correlationId);
    }

    // Obligaciones: Pago/Consulta Banco Agrario

    @PutMapping(value = "${spring.application.services.rest.banking.obligation}")
    public final Mono<Response> payObligation(@NotNull @RequestBody Mono<RequestFormatBanking> requestBanking,
                                              @PathVariable @NotNull String referenceNumber,
                                              @RequestHeader(value = "merchantId") @NotNull String merchantId,
                                              @RequestHeader(value = "posId") @NotNull String posId,
                                              @RequestHeader(value = "Authorization") @NotNull String userpass,
                                              @RequestHeader(value = "correlationId", required = false) String correlationId) {
        return bankingBusinesssService.service(OperationType.PAY_OBLIGATION, requestBanking, referenceNumber, merchantId, posId, userpass, correlationId);
    }

    @PostMapping(value = "${spring.application.services.rest.banking.obligation}")
    public final Mono<Response> queryObligation(@RequestBody Mono<RequestFormatBanking> requestBanking,
                                                @PathVariable @NotNull String referenceNumber,
                                                @RequestHeader(value = "merchantId") @NotNull String merchantId,
                                                @RequestHeader(value = "posId") @NotNull String posId,
                                                @RequestHeader(value = "Authorization") @NotNull String userpass,
                                                @RequestHeader(value = "correlationId", required = false) String correlationId) {
        return bankingBusinesssService.service(OperationType.QUERY_OBLIGATION, requestBanking, referenceNumber, merchantId, posId, userpass, correlationId);
    }

    // *** BBVA ***

    @PostMapping(value = "${spring.application.services.rest.banking.withdrawal}")
    public final Mono<Response> queryWithdrawal(@RequestBody Mono<RequestFormatBanking> requestBanking,
                                                @PathVariable @NotNull String referenceNumber,
                                                @RequestHeader(value = "merchantId") @NotNull String merchantId,
                                                @RequestHeader(value = "posId") @NotNull String posId,
                                                @RequestHeader(value = "Authorization") @NotNull String userpass,
                                                @RequestHeader(value = "correlationId", required = false) String correlationId) {
        return bankingBusinesssService.service(OperationType.QUERY_WITHDRAWAL, requestBanking, referenceNumber, merchantId, posId, userpass, correlationId);
    }


    @DeleteMapping(value = "${spring.application.services.rest.banking.withdrawal}")
    public final Mono<Response> withdrawal(@RequestBody Mono<RequestFormatBanking> requestBanking,
                                           @PathVariable @NotNull String referenceNumber,
                                           @RequestHeader(value = "merchantId") @NotNull String merchantId,
                                           @RequestHeader(value = "posId") @NotNull String posId,
                                           @RequestHeader(value = "Authorization") @NotNull String userpass,
                                           @RequestHeader(value = "correlationId", required = false) String correlationId) {
        return bankingBusinesssService.service(OperationType.WITHDRAWAL, requestBanking, referenceNumber, merchantId, posId, userpass, correlationId);
    }

    // ** GLOBAL **

    @PutMapping(value = "${spring.application.services.rest.banking.disburment}")
    public final Mono<Response> disburment(@NotNull @RequestBody Mono<RequestFormatBanking> requestBanking,
                                           @PathVariable @NotNull String referenceNumber,
                                           @RequestHeader(value = "merchantId") @NotNull String merchantId,
                                           @RequestHeader(value = "posId") @NotNull String posId,
                                           @RequestHeader(value = "Authorization") @NotNull String userpass,
                                           @RequestHeader(value = "correlationId", required = false) String correlationId) {
        return bankingBusinesssService.service(OperationType.DEPOSIT, requestBanking, referenceNumber, merchantId, posId, userpass, correlationId);
    }

    // Facturas: Pago/Consulta

    @PutMapping(value = "${spring.application.services.rest.banking.payBill}")
    public final Mono<Response> payBill(@NotNull @RequestBody Mono<RequestFormatBanking> requestBanking,
                                        @RequestHeader(value = "merchantId") @NotNull String merchantId,
                                        @RequestHeader(value = "posId") @NotNull String posId,
                                        @RequestHeader(value = "Authorization") @NotNull String userpass,
                                        @RequestHeader(value = "correlationId", required = false) String correlationId) {
        return bankingBusinesssService.service(OperationType.PAY_BILL, requestBanking, null, merchantId, posId, userpass, correlationId);
    }

    @PostMapping(value = "${spring.application.services.rest.banking.bill}")
    public final Mono<Response> queryBill(@RequestBody Mono<RequestFormatBanking> requestBanking,
                                          @PathVariable @NotNull String referenceNumber,
                                          @RequestHeader(value = "merchantId") @NotNull String merchantId,
                                          @RequestHeader(value = "posId") @NotNull String posId,
                                          @RequestHeader(value = "Authorization") @NotNull String userpass,
                                          @RequestHeader(value = "correlationId", required = false) String correlationId) {
        return bankingBusinesssService.service(OperationType.QUERY_BILL, requestBanking, referenceNumber, merchantId, posId, userpass, correlationId);
    }

}

