package co.moviired.microservice.service;

import co.moviired.microservice.domain.enums.OperationType;
import co.moviired.microservice.domain.request.Request;
import co.moviired.microservice.domain.response.Response;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@AllArgsConstructor
@RequestMapping("${server.servlet.context-path}")
public class ConnectorBogotaController {

    private final ConnectorBogotaService connectorService;

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

