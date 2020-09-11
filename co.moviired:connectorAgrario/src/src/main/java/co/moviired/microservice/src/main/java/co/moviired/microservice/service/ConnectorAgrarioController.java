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
public class ConnectorAgrarioController {

    private final ConnectorAgrarioService connectorService;

    @GetMapping(value = "${spring.application.services.rest.ping}")
    public final Mono<String> ping() {
        return Mono.just("I'm Alive!");
    }

    @PostMapping(value = "${spring.application.services.rest.query.manual}")
    public final Mono<Response> processManual(@RequestBody Mono<Request> request) {
        return connectorService.service(request, OperationType.MANUAL);
    }

    @PostMapping(value = "${spring.application.services.rest.query.automatic}")
    public final Mono<Response> processAutomatic(@RequestBody Mono<Request> request) {
        return connectorService.service(request, OperationType.AUTOMATIC);
    }

    @PostMapping(value = "${spring.application.services.rest.payment}")
    public final Mono<Response> processPayment(@RequestBody Mono<Request> request) {
        return connectorService.service(request, OperationType.PAYMENT);
    }

}

