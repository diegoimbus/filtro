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
    public final Mono<Response> registryCardRequest(@NotNull @RequestBody Mono<RequestFormatCard> requestFormatCard,
                                                    @RequestHeader(value = "Authorization") @NotNull String userpass) {
        //Se envia mono con los datos a guardar en el registro
        return cardManagerService.registryCarRequest(requestFormatCard, userpass);
    }

    @GetMapping(value = "${spring.application.services.rest.queryCard}")
    public final Mono<Response> queryCardId(@PathVariable ("idType") String idType, @PathVariable ("idNumber") String idNumber) throws JsonProcessingException {
        //se envian los valores necesarios para la consulta de por numero de cedula
        return cardManagerService.queryCardId(idNumber, idType);
    }

    @GetMapping(value = "${spring.application.services.rest.queryLocation}")
    public final Mono<Response> queryCardLocation(@PathVariable ("page") Integer page, @PathVariable ("pageSize") Integer pageSize,
                                                  @RequestBody Mono<RequestFormatCard> requestFormatCard) throws JsonProcessingException {
        //Se crea objeto de tipo pageable que feine el tamaño de las paginas y la pagina que se debe mostrar
        Pageable pageable = PageRequest.of(page, pageSize);

        return cardManagerService.queryLocation(pageable, requestFormatCard);
    }

    @GetMapping(value = "${spring.application.services.rest.queryReport}")
    public final Mono<Response> queryReport() throws JsonProcessingException {
        return cardManagerService.queryReport();
    }

    @PutMapping(value = "${spring.application.services.rest.updateCardDelivery}")
    public final Mono<Response> updateCardDelivered(@NotNull @RequestBody Mono<RequestFormatCard> requestFormatCard,
                                                    @RequestHeader(value = "Authorization") @NotNull String userpass) throws JsonProcessingException {
        return  cardManagerService.updateDeliveredCard(requestFormatCard, userpass);
    }

}

