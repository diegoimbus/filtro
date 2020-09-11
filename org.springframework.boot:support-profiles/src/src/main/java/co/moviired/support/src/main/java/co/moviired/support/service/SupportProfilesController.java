package co.moviired.support.service;
/*
 * Copyright @2019. Movii, SAS. Todos los derechos reservados.
 *
 * @author Bejarano, Cindy
 * @version 1, 2019
 * @since 1.0
 */

import co.moviired.support.domain.dto.Request;
import co.moviired.support.domain.dto.Response;
import co.moviired.support.domain.enums.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;

import org.springframework.http.server.reactive.ServerHttpRequest;

@Slf4j
//@CrossOrigin
@RestController
@RequestMapping("${server.servlet.context-path}")
public class SupportProfilesController {

    private final SupportProfilesService supportProfilesService;
    private static final String CORRELATION = "correlationId";
    private static final String AUTHORIZATION = "Authorization";
    public SupportProfilesController(@NotNull SupportProfilesService psupportProfilesService) {
        super();
        this.supportProfilesService = psupportProfilesService;
    }

    @GetMapping(value = "${spring.application.services.rest.ping}")
    public final Mono<String> ping() {
        return Mono.just("I'm Alive!");
    }

    @GetMapping(value = "${spring.application.services.rest.profile.selectDeleteProfile}")
    public final Mono<Response> findProfileById(@NotNull @PathVariable String idProfile,
                                                @RequestHeader(value = CORRELATION, required = false)  String correlationId,
                                                ServerHttpRequest server) {

         return supportProfilesService.serviceProfileRD(Mono.just(idProfile), correlationId, OperationType.SELECT,null);
    }

    @GetMapping(value = "${spring.application.services.rest.profile.profileByName}")
    public final Mono<Response> findProfileByName(@NotNull @PathVariable String name,
                                                  @RequestHeader(value = CORRELATION, required = false) String correlationId) {
        return supportProfilesService.serviceProfileRName(Mono.just(name), correlationId);
    }
    @GetMapping(value = "${spring.application.services.rest.profile.profilesAll}")
    public final Mono<Response> profilesAll(@RequestHeader(value = CORRELATION, required = false) String correlationId) {
        return supportProfilesService.getProfileAll(correlationId);
    }

    @GetMapping(value = "${spring.application.services.rest.profile.findAuthorities}")
    public final Mono<Response> findAuthorities(@NotNull @PathVariable String name,
                                                @RequestHeader(value = AUTHORIZATION, required = false)  String authorization,
                                                  @RequestHeader(value = CORRELATION, required = false) String correlationId) {
        return supportProfilesService.findAuthorities(Mono.just(name), correlationId);
    }

    @DeleteMapping(value = "${spring.application.services.rest.profile.selectDeleteProfile}")
    public final Mono<Response> deleteProfileById(@NotNull @PathVariable String idProfile,
                                                  @RequestHeader(value = AUTHORIZATION, required = false)  String authorization,
                                                  @RequestHeader(value = CORRELATION, required = false)  String correlationId,
                                                  ServerHttpRequest server) {
        return supportProfilesService.serviceProfileRD(Mono.just(idProfile), correlationId, OperationType.DELETE,authorization);
    }

    @PostMapping(value = "${spring.application.services.rest.profile.insertUpdateProfile}")
    public final Mono<Response> createProfile(@NotNull @RequestBody Mono<Request> request,
                                              @RequestHeader(value = AUTHORIZATION, required = false)  String authorization,
                                              @RequestHeader(value = CORRELATION, required = false)  String correlationId) {
        return supportProfilesService.serviceProfileCU(request, correlationId, OperationType.INSERT,authorization);
    }

    @PutMapping(value = "${spring.application.services.rest.profile.insertUpdateProfile}")
    public final Mono<Response> updateProfile(@NotNull @RequestBody Mono<Request> request,
                                              @RequestHeader(value = AUTHORIZATION, required = false)  String authorization,
                                              @RequestHeader(value = CORRELATION, required = false)  String correlationId) {
        return supportProfilesService.serviceProfileCU(request, correlationId, OperationType.UPDATE,authorization);
    }

    @GetMapping(value = "${spring.application.services.rest.profile.profilesByStatus}")
    public final Mono<Response> findProfilesByStatus(@NotNull @PathVariable Boolean status,
                                                     @RequestHeader(value = CORRELATION, required = false) String correlationId) {
        return supportProfilesService.serviceFindProfileByStatus(Mono.just(status), correlationId);
    }

    @GetMapping(value = "${spring.application.services.rest.operation.selectOperation}")
    public final Mono<Response> findOperationById(@NotNull @PathVariable String idOperation,
                                                  @RequestHeader(value = CORRELATION, required = false)  String correlationId) {
        return supportProfilesService.serviceOperationR(Mono.just(idOperation), correlationId, OperationType.SELECT);
    }

    @GetMapping(value = "${spring.application.services.rest.operation.operationsByStatus}")
    public final Mono<Response> findOperationsByStatus(@NotNull @PathVariable Boolean status,
                                                       @RequestHeader(value = CORRELATION, required = false)  String correlationId) {
        return supportProfilesService.serviceFindOperationByStatus(Mono.just(status), correlationId);
    }

    @GetMapping(value = "${spring.application.services.rest.module.getModuleAll}")
    public final Mono<Response> getModuleAll(@RequestHeader(value = CORRELATION, required = false)  String correlationId) {
        return supportProfilesService.getModuleAll( correlationId);
    }


}

