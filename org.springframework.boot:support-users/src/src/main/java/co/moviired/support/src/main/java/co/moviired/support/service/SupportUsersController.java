package co.moviired.support.service;
/*
 * Copyright @2019. Movii, SAS. Todos los derechos reservados.
 *
 * @author Bejarano, Cindy
 * @version 1, 2019
 * @since 1.0
 */

import co.moviired.base.domain.exception.DataException;
import co.moviired.support.domain.dto.Request;
import co.moviired.support.domain.dto.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;

@Slf4j
@RestController
@RequestMapping(value = "${server.servlet.context-path}")
public class SupportUsersController {

    private final SupportUsersService supportUsersService;
    private static final String CORRELATION_ID = "correlationId";
    private static final String MSISDN = "msisdn";
    private static final String AUTHORIZATION = "Authorization";

    public SupportUsersController(@NotNull SupportUsersService psupportUsersService) {
        super();
        this.supportUsersService = psupportUsersService;
    }

    @PostMapping(value = "${spring.application.services.rest.login}")
    public final Response serviceClient(@RequestHeader(value = SupportUsersController.CORRELATION_ID, required = false) String correlationId,
            @NotNull @RequestBody Request request) {
        return supportUsersService.login(correlationId, request);
    }

    @PostMapping(value = "${spring.application.services.rest.updateUser}")
    public final Response serviceUpdateUser(
            @RequestHeader(value = SupportUsersController.AUTHORIZATION, required = false)  String authorization,
            @RequestHeader(value = SupportUsersController.CORRELATION_ID, required = false) String correlationId,
            @NotNull @RequestBody Request request) {
        return supportUsersService.updateUser(correlationId, request,authorization);
    }

    @PostMapping(value = "${spring.application.services.rest.resetPassword}")
    public final Mono<Response> serviceResetPassword(
            @RequestHeader(value = SupportUsersController.CORRELATION_ID, required = false) String correlationId,
            @NotNull @RequestBody Mono<Request> request) {
        return supportUsersService.resetPassword(correlationId, request);
    }

    @PostMapping(value = "${spring.application.services.rest.createUser}")
    public final Response serviceCreateUser(
            @RequestHeader(value = SupportUsersController.AUTHORIZATION, required = false)  String authorization,
            @RequestHeader(value = SupportUsersController.CORRELATION_ID, required = false) String correlationId,
            @NotNull @RequestBody Request request) {
        return supportUsersService.createUser(correlationId, request,authorization);
    }

    @PostMapping(value = "${spring.application.services.rest.deleteUser}")
    public final Response serviceDeleteUser(
            @RequestHeader(value = SupportUsersController.AUTHORIZATION, required = false)  String authorization,
            @RequestHeader(value = SupportUsersController.CORRELATION_ID, required = false) String correlationId,
            @NotNull @RequestBody Request request) {
        return supportUsersService.deleteUser(correlationId, request,authorization);
    }

    @PostMapping(value = "${spring.application.services.rest.generateOTP}")
    public final Mono<Response> generateOTP(
            @RequestHeader(value = SupportUsersController.CORRELATION_ID, required = false) String correlationId,
            @NotNull @RequestBody Mono<Request> request) throws DataException {
        return supportUsersService.generateOTP(correlationId, request);
    }

    @GetMapping(value = "${spring.application.services.rest.findAllUsers}")
    public final Response findAllUsers(
            @RequestHeader(value = SupportUsersController.CORRELATION_ID, required = false) String correlationId) {
        return supportUsersService.findAllUsers(correlationId);
    }

    @GetMapping(value = "${spring.application.services.rest.findAllUsersTmp}")
    public final Response findAllUsersTmp(
            @RequestHeader(value = SupportUsersController.CORRELATION_ID, required = false) String correlationId) {
        return supportUsersService.findAllUsersTmp(correlationId);
    }

    @PostMapping(value = "${spring.application.services.rest.findUser}")
    public final Response findUser(
            @RequestHeader(value = SupportUsersController.CORRELATION_ID, required = false) String correlationId,
            @NotNull @RequestBody Request request) {
        return supportUsersService.findUser(correlationId, request.getMsisdn());
    }

    @GetMapping(value = "${spring.application.services.rest.findUser}")
    public final Response findUserGet(
            @RequestHeader(value = SupportUsersController.CORRELATION_ID, required = false) String correlationId,
            @RequestParam(value = SupportUsersController.MSISDN, required = false) String msisdn) {
        return supportUsersService.findUser(correlationId, msisdn);
    }

    @GetMapping(value = "${spring.application.services.rest.findUserTmp}")
    public final Response findUserTmpGet(
            @RequestHeader(value = SupportUsersController.CORRELATION_ID, required = false) String correlationId,
            @RequestParam(value = SupportUsersController.MSISDN, required = false) String msisdn) {
        return supportUsersService.findUserTmp(correlationId, msisdn);
    }

    @PostMapping(value = "${spring.application.services.rest.changePassword}")
    public final Response changePassword(
            @RequestHeader(value = SupportUsersController.AUTHORIZATION, required = false)  String authorization,
            @RequestHeader(value = SupportUsersController.CORRELATION_ID, required = false) String correlationId,
            @NotNull @RequestBody Request request) {
        return supportUsersService.changePassword(correlationId, request);
    }

    @PostMapping(value = "${spring.application.services.rest.changePasswordMahindra}")
    public final Response changePasswordMahindra(
            @RequestHeader(value = SupportUsersController.AUTHORIZATION, required = false)  String authorization,
            @RequestHeader(value = SupportUsersController.CORRELATION_ID, required = false) String correlationId,
            @NotNull @RequestBody Request request) {
        return supportUsersService.setPasswordMahindra(correlationId, request,authorization);
    }

    @PostMapping(value = "${spring.application.services.rest.activateUser}")
    public final Response activateUser(
            @RequestHeader(value = SupportUsersController.AUTHORIZATION, required = false)  String authorization,
            @RequestHeader(value = SupportUsersController.CORRELATION_ID, required = false) String correlationId,
            @NotNull @RequestBody Request request) {
        return supportUsersService.activateUser(correlationId, request, authorization);
    }



    @GetMapping(value = "${spring.application.services.rest.approvedUserTmp}")
    public final Response approvedUserTmp(
            @RequestHeader(value = SupportUsersController.AUTHORIZATION, required = false)  String authorization,
            @RequestHeader(value = SupportUsersController.CORRELATION_ID, required = false) String correlationId,
            @RequestParam(value = SupportUsersController.MSISDN, required = false) String msisdn) {
        return supportUsersService.approvedUserTmp(correlationId, msisdn, authorization);
    }

    @GetMapping(value = "${spring.application.services.rest.rejectedUserTmp}")
    public final Response rejectedUserTmp(
            @RequestHeader(value = SupportUsersController.AUTHORIZATION, required = false)  String authorization,
            @RequestHeader(value = SupportUsersController.CORRELATION_ID, required = false) String correlationId,
            @RequestParam(value = SupportUsersController.MSISDN, required = false) String msisdn) {
        return supportUsersService.rejectedUserTmp(correlationId, msisdn, authorization);
    }

}

