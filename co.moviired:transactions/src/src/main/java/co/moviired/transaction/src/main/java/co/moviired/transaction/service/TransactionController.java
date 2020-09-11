package co.moviired.transaction.service;

import co.moviired.transaction.domain.request.RequestManager;
import co.moviired.transaction.domain.response.TransactionResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;


@Slf4j
@RestController()
@AllArgsConstructor
@RequestMapping("${spring.application.services.rest.uri}")
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping(value = "${spring.application.services.rest.ping}")
    public final Mono<String> ping() {
        return Mono.just(transactionService.ping());
    }

    @GetMapping(value = "${spring.application.services.rest.getServiceTypes}")
    public final Mono<TransactionResponse> getServiceTypes(@RequestHeader(name = "Authorization") String authorization,
                                                           @RequestHeader(name = "merchantId") String merchantId,
                                                           @RequestHeader(name = "posId") String posId) {
        return transactionService.getServiceTypes(Mono.just(authorization));
    }

    @PostMapping(value = "${spring.application.services.rest.getTransactions}")
    public Mono<TransactionResponse> getTransactions(@RequestHeader(name = "Authorization") String authorization,
                                                     @RequestBody Mono<RequestManager> request) {

        return transactionService.getTransactions(request, authorization);
    }


    @GetMapping(value = "${spring.application.services.rest.getTransactionsDetail}")
    public Mono<TransactionResponse> getTransactionsDetail(@RequestHeader(name = "Authorization") String authorization,
                                                           @PathVariable String origin,
                                                           @PathVariable String transactionId) {

        return transactionService.getTransactionDetail(transactionId, authorization, origin);
    }

}

