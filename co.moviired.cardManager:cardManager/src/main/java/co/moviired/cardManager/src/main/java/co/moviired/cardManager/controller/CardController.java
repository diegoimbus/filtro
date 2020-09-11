package co.moviired.cardManager.controller;

import co.moviired.cardManager.domain.dto.request.RequestFormatCard;
import co.moviired.cardManager.domain.dto.response.Response;
import co.moviired.cardManager.domain.entity.ReclaimCard;
import co.moviired.cardManager.service.CardManagerService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;


@Slf4j
@AllArgsConstructor
@RestController("CardController")
@RequestMapping("${server.servlet.context-path}")
public class CardController {

    private final CardManagerService cardManagerService;

    @GetMapping(value = "${spring.application.services.rest.ping}")
    public final Mono<String> ping() {
        // Si llega la petición return I'm Alive!
        return Mono.just("I'm Alive!");
    }

    @PostMapping(value = "${spring.application.services.rest.registry}")
    public final Mono<Response> registryCardRequest(@NotNull @RequestBody Mono<RequestFormatCard> requestFormatCard) {
        //Se envia mono con los datos a guardar en el registro
        return cardManagerService.registryCarRequest(requestFormatCard);
    }

    @GetMapping(value = "${spring.application.services.rest.queryCard}")
    public final Mono<Response> queryCardId(@PathVariable ("idType") String idType, @PathVariable ("idNumber") String idNumber) throws JsonProcessingException {
        //se envian los valores necesarios para la consulta de por numero de cedula
        return cardManagerService.queryCardId(idNumber, idType);
    }

    /*@GetMapping(value = "${spring.application.services.rest.queryLocation}")
    public final Mono<Response> queryCardLocation(@PathVariable ("pointName") String pointName, @PathVariable (required = false) String city,
                                                  @PathVariable(required = false) String address) throws JsonProcessingException {
        //se envian los valores necesarios para la consulta de por numero de cedula
        return cardManagerService.queryLocation(pointName, city, address);
    }*/

    @GetMapping(value = "${spring.application.services.rest.queryLocation}")
    public final Page<ReclaimCard> queryCardLocation(@PathVariable ("pointName") String pointName)
                                                        throws JsonProcessingException {
        Pageable pageable = PageRequest.of(0, 5);
        //se envian los valores necesarios para la consulta de por numero de cedula
        return cardManagerService.queryLocation(pointName, pageable);
    }
}

