package co.moviired.microservice.service;

/*
 * Copyright @2019. Movii, SAS. Todos los derechos reservados.
 *
 * @author Cindy Bejarano, Oscar Lopez
 * @since 1.0.0
 */

import co.moviired.microservice.domain.enums.OperationType;
import co.moviired.microservice.domain.request.Request;
import co.moviired.microservice.domain.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("${spring.application.services.uri}")
public class ConnectorBogotaController {

    @Autowired
    private ConnectorBogotaService connectorService;

    @GetMapping(value = "${spring.application.services.rest.ping}")
    public final Mono<String> ping() {
        return Mono.just("I'm Alive!");
    }

    @PostMapping(value = "${spring.application.services.rest.query}")
    public final Mono<Response> processQuery(@RequestBody Mono<Request> request) {
        return connectorService.service(request, OperationType.QUERY);
    }

    @PostMapping(value = "${spring.application.services.rest.payBill}")
    public final Mono<Response> processPayBill(@RequestBody Mono<Request> request) {
        return connectorService.service(request, OperationType.PAY_BILL);
    }

}

