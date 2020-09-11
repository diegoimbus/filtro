package co.moviired.digitalcontent.incomm.service;

import co.moviired.base.domain.enumeration.ErrorType;
import co.moviired.base.domain.exception.DataException;
import co.moviired.digitalcontent.incomm.model.request.Input;
import co.moviired.digitalcontent.incomm.model.request.Request;
import co.moviired.digitalcontent.incomm.model.response.Response;
import co.moviired.digitalcontent.incomm.properties.IncommProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;

@Slf4j
@RestController
@RequestMapping("${spring.application.root}")
public final class IncommController {

    private final IncommService incommService;
    private final IncommProperties incommProperties;

    public IncommController(@NotNull IncommService pincommService,
                            @NotNull IncommProperties pincommProperties) {
        super();
        this.incommService = pincommService;
        this.incommProperties = pincommProperties;
    }

    @GetMapping(value = "${spring.application.services.rest.ping}")
    public Mono<String> echo() {
        return incommService.getEcho();
    }

    // PINES
    @PostMapping(value = "${spring.application.services.rest.pines.activate}")
    public Mono<Response> processPinesActivateTransaction(@NotNull @RequestBody Mono<Request> request) {
        return incommService.processPinesActivate(request);
    }

    @DeleteMapping(value = "${spring.application.services.rest.pines.inactivate}")
    public Mono<Response> processPinesInactivateTransaction(@NotNull @RequestBody Mono<Request> request) {
        return incommService.processPinesReverse(request);
    }

    @PostMapping(value = "${spring.application.services.rest.pines.process}")
    public Mono<Response> processPin(@NotNull @RequestBody Mono<Request> pRequest) {
        return pRequest.flatMap(request -> {
            Mono<Response> response;
            try {
                Input data = Input.parseInput(request.getData(), request.getMeta());
                if ((incommProperties.getProcessCodeActivationPinIncomm().equalsIgnoreCase(data.getOperation()))) {
                    response = incommService.processPinesActivate(Mono.just(request));

                } else if (incommProperties.getProcessCodeDeactivationPinIncomm().equalsIgnoreCase(data.getOperation())) {
                    response = incommService.processPinesReverse(Mono.just(request));
                } else {
                    throw new DataException("-2", "No se encuentra configurado el codigo de operación.");
                }

            } catch (DataException e) {
                log.error(e.getMessage());
                response = Mono.just(new Response("99", "Error inesperado", ErrorType.DATA, HttpStatus.INTERNAL_SERVER_ERROR));
            }

            return response;
        });

    }

    // CARD
    @PostMapping(value = "${spring.application.services.rest.card.activate}")
    public Mono<Response> processActivateCard(@NotNull @RequestBody Mono<Request> request) {
        return incommService.processCardActivate(request);
    }

    @DeleteMapping(value = "${spring.application.services.rest.card.inactivation}")
    public Mono<Response> processInactivationCard(@NotNull @RequestBody Mono<Request> request) {
        return incommService.processCardInactivate(request);
    }

    @PostMapping(value = "${spring.application.services.rest.card.process}")
    public Mono<Response> processCard(@NotNull @RequestBody Mono<Request> pRequest) {
        return pRequest.flatMap(request -> {
            Mono<Response> response;
            try {
                Input data = Input.parseInput(request.getData(), request.getMeta());
                if ((incommProperties.getProcessCodeActivation().equalsIgnoreCase(data.getOperation())) ||
                        (incommProperties.getProcessCodeActivation2().equalsIgnoreCase(data.getOperation()))) {
                    response = incommService.processCardActivate(Mono.just(request));
                } else if (incommProperties.getProcessCodeDeactivation().equalsIgnoreCase(data.getOperation())) {
                    response = incommService.processCardInactivate(Mono.just(request));
                } else {
                    throw new DataException("-2", "No se encuentra configurado el codigo de operación.");
                }

            } catch (DataException e) {
                log.error(e.getMessage());
                response = Mono.just(new Response("99", e.getMessage(), ErrorType.DATA, HttpStatus.INTERNAL_SERVER_ERROR));
            }

            return response;
        });

    }

    @PutMapping(value = "${spring.application.services.rest.card.reversion}")
    public Mono<Response> processReversionCard(@NotNull @RequestBody Mono<Request> request) {
        return incommService.processCardReversion(request);
    }

}

