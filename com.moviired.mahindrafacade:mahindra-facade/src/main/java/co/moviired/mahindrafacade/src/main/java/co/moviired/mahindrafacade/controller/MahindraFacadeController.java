package co.moviired.mahindrafacade.controller;


import co.moviired.mahindrafacade.service.MahindraFacadeService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Slf4j
@RestController()
@AllArgsConstructor
@RequestMapping("${spring.application.services.rest.uri}")
public class MahindraFacadeController {

    private final MahindraFacadeService mahindraFacadeService;

    @PostMapping(value = "",
            consumes = MediaType.APPLICATION_XML_VALUE, produces = {MediaType.APPLICATION_XML_VALUE})
    public final Mono<String> process(@RequestBody Mono<String> request) {
        return mahindraFacadeService.processMHFacade(request);
    }

    @PostMapping(value = "${spring.application.services.rest.sendUser}")
    public final Mono<String> sendUser(@RequestBody Mono<String> request) {

        return mahindraFacadeService.validateUserService(request);

    }

}

