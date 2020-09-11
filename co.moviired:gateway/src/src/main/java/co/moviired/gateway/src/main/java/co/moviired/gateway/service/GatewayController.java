package co.moviired.gateway.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;

@RestController
@Slf4j
@RequestMapping("${spring.application.name}")
public class GatewayController {

    private final GatewayService moviiredService;

    public GatewayController(@NotNull GatewayService pmoviiredService) {
        super();
        this.moviiredService = pmoviiredService;
    }

    @GetMapping(value = "${services.updateProfiles}")
    public final Mono<Boolean> updateProfiles() {
        log.info("*********** UPDATE PROFILES - STARTED ***********");
        //Validate input parameters
        return this.moviiredService.updateProfiles();
    }

}

