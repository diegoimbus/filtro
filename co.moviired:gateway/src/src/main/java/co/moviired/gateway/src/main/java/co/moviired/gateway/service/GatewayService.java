package co.moviired.gateway.service;
/*
 * Copyright @2019. Movii, SAS. Todos los derechos reservados.
 *
 * @author Bejarano, Cindy
 * @version 1, 2019
 * @since 1.0
 */

import co.moviired.gateway.conf.ProfilesConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;


@Slf4j
@Service
public class GatewayService {


    private final ProfilesConfig profilesConfig;

    public GatewayService(ProfilesConfig pprofilesConfig) {
        super();
        this.profilesConfig = pprofilesConfig;
    }

    public final Mono<Boolean> updateProfiles() {
        return Mono.just(true).flatMap(request -> {
            profilesConfig.loadProfiles();
            log.info("*********** UPDATE PROFILES - FINISHED ***********");
            return Mono.just(true);
        }).onErrorResume(e -> Mono.just(true));
    }
}

