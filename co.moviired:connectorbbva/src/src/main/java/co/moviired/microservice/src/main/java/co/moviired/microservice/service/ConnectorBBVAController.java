package co.moviired.microservice.service;

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
public class ConnectorBBVAController {

    private final ConnectorBBVAService connectorService;

    public ConnectorBBVAController(@NotNull final ConnectorBBVAService pconnectorService) {
        super();
        this.connectorService = pconnectorService;
    }

    @GetMapping(value = "${spring.application.services.rest.ping}")
    public final Mono<String> ping() {
        return Mono.just("I'm Alive!");
    }

    @PostMapping(value = "${spring.application.services.rest.query.manual}")
    public final Mono<Response> processManual(@RequestBody Mono<Request> request) {
        return connectorService.service(request, OperationType.MANUAL_QUERY);
    }

    @PostMapping(value = "${spring.application.services.rest.query.automatic}")
    public final Mono<Response> processAutomatic(@RequestBody Mono<Request> request) {
        return connectorService.service(request, OperationType.AUTOMATIC_QUERY);
    }

    @PostMapping(value = "${spring.application.services.rest.payment}")
    public final Mono<Response> processPayment(@RequestBody Mono<Request> request) {
        return connectorService.service(request, OperationType.PAYMENT);
    }


}

