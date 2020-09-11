package co.moviired.microservice.service;
/*
 * Copyright @2019. Movii, SAS. Todos los derechos reservados.
 *
 * @author Bejarano, Cindy
 * @version 1, 2019
 * @since 1.0
 */

import co.moviired.microservice.domain.enums.OperationType;
import co.moviired.microservice.domain.request.Request;
import co.moviired.microservice.domain.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;

@Slf4j
@RestController
@RequestMapping("${spring.application.services.uri}")
public class BankSwitchController {

    private final BankSwitchService bankingService;

    public BankSwitchController(@NotNull final BankSwitchService pbankingService) {
        super();
        this.bankingService = pbankingService;
    }

    @GetMapping(value = "${spring.application.services.rest.ping}")
    public final Mono<String> ping() {
        return Mono.just("I'm Alive!");
    }

    @PostMapping(value = "${spring.application.services.rest.cashIn}")
    public final Mono<Response> processCashIn(@RequestBody Mono<Request> request) {
        return bankingService.service(request, OperationType.CASH_IN);
    }

    @PostMapping(value = "${spring.application.services.rest.query}")
    public final Mono<Response> processQuery(@RequestBody Mono<Request> request) {
        return bankingService.service(request, OperationType.QUERY);
    }

    @PostMapping(value = "${spring.application.services.rest.cashOut}")
    public final Mono<Response> processCashOut(@RequestBody Mono<Request> request) {
        return bankingService.service(request, OperationType.CASH_OUT);
    }

}

