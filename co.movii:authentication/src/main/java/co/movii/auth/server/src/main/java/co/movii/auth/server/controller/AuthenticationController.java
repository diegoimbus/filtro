package co.movii.auth.server.controller;

/*
 * Copyright @2019. Movii, SAS. Todos los derechos reservados.
 *
 * @author Rivas, Rodolfo
 * @version 1, 2019
 * @since 1.0
 */

import co.movii.auth.server.conf.StatusCodeConfig;
import co.movii.auth.server.domain.dto.Request;
import co.movii.auth.server.domain.dto.Response;
import co.movii.auth.server.domain.dto.User;
import co.movii.auth.server.domain.entity.AuthRegisterLogin;
import co.movii.auth.server.domain.enums.ClientType;
import co.movii.auth.server.domain.enums.OperationType;
import co.movii.auth.server.domain.repository.AuthRegisterLoginRepository;
import co.movii.auth.server.exception.MaxDevicesException;
import co.movii.auth.server.exception.ParseException;
import co.movii.auth.server.helper.UtilHelper;
import co.movii.auth.server.security.crypt.CryptoUtility;
import co.movii.auth.server.service.AuthenticationService;
import co.movii.auth.server.service.QueryUserInfoService;
import co.movii.auth.server.service.ServiceFactory;
import co.movii.auth.server.service.ValidationPasswordService;
import co.moviired.audit.service.PushAuditService;
import co.moviired.base.domain.StatusCode;
import co.moviired.base.domain.enumeration.ErrorType;
import co.moviired.base.domain.exception.CommunicationException;
import co.moviired.base.domain.exception.ServiceException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@RestController
public final class AuthenticationController {

    private static final String TRANSACTION_FAILED = "99";
    private static final String BAD_CREDENTIALS = "99033";
    private static final String ERROR_TYPE = "ERROR";
    private static final String INVALID_CLV = "VALIDATE_PASSWORD";
    private static final String ERROR_MAX_DEVICES = "ERROR_MAX_DEVICES";
    private static final String PETICION = "petición recibida ";

    private final StatusCodeConfig statusCodeConfig;
    private final CryptoUtility cryptoUtility;

    private final ObjectMapper objectMapper;
    private final AuthenticationService authenticationService;
    private final ValidationPasswordService validationPasswordService;
    private final AuthRegisterLoginRepository authRegisterLoginRepository;
    private final QueryUserInfoService queryUserInfoService;
    private final PushAuditService pushAuditService;

    public AuthenticationController(
            @NotNull AuthenticationService pauthenticationService,
            ServiceFactory serviceConfig,
            @NotNull StatusCodeConfig pstatusCodeConfig,
            @NotNull CryptoUtility pcryptoUtility,
            ObjectMapper pobjectMapper,
            @NotNull AuthRegisterLoginRepository pauthRegisterLoginRepository) {

        super();
        this.pushAuditService = serviceConfig.getPushAuditService();
        this.authenticationService = pauthenticationService;
        this.queryUserInfoService = serviceConfig.getQueryUserInfoService();
        this.validationPasswordService = serviceConfig.getValidationPasswordService();
        this.statusCodeConfig = pstatusCodeConfig;
        this.cryptoUtility = pcryptoUtility;
        this.objectMapper = pobjectMapper;
        this.authRegisterLoginRepository = pauthRegisterLoginRepository;
    }


    // PING
    @GetMapping(value = "${spring.application.services.rest.ping}")
    public Mono<String> ping() {
        return Mono.just("OK");
    }

    // OTP
    @PostMapping(value = "${spring.application.services.rest.generateOTP}")
    public  Mono<Response> generateOTPReset(@NotNull @RequestBody Request request) throws ParseException, CloneNotSupportedException, JsonProcessingException {

        request.setCorrelationId(this.authenticationService.asignarCorrelativo(request.getCorrelationId()));
        log.info("*********** GENERATE OTP ***********");

        // Validate datos de entrada
        Response responseValidate = authenticationService.validateInput(OperationType.GENERATE_OTP, request);
        if (!responseValidate.getErrorCode().equals("00")) {
            log.error(PETICION + responseValidate.getErrorMessage());
            this.authenticationService.requestInputMask(OperationType.LOGIN, request);
            return Mono.just(responseValidate);
        }

        // Obtener el último ingreso exitoso
        String userLogin = cryptoUtility.decryptAES(request.getUserLogin());
        AuthRegisterLogin login = this.authRegisterLoginRepository.findFirstByPhoneNumberOrderByLoginDateDesc(userLogin);
        if (login != null) {
            // Validar el máximo de cambios de dispositivos posibles
            try {
                User user = new User();
                user.setMsisdn(userLogin);
                user.setUserType(request.getSource());
                request.setUser(user);
                this.authenticationService.validateDeviceChange(login, request);
            } catch (MaxDevicesException mxe) {
                log.error("[{}] {}", request.getCorrelationId(), mxe.getMessage());
                // Devolver la respuesta de error por cambio de dispositivos
                StatusCode sc = statusCodeConfig.of(ERROR_MAX_DEVICES);
                Response response = new Response();
                response.setErrorType(ErrorType.DATA.name());
                response.setErrorCode(sc.getCode());
                response.setErrorMessage(sc.getMessage());
                return Mono.just(response);
            }
        }

        // Realizar la operación
        request.setUserLogin(userLogin);
        return authenticationService.process(Mono.just(request), OperationType.GENERATE_OTP, null).flatMap(this::logResponse);
    }

    @PostMapping(value = "${spring.application.services.rest.validateOTP}")
    public  Mono<Response> validateOTP(@NotNull @RequestBody Request request) throws ParseException, CloneNotSupportedException, JsonProcessingException {

        request.setCorrelationId(this.authenticationService.asignarCorrelativo(request.getCorrelationId()));
        log.info("*********** VALIDATE OTP ***********");

        // validate datos de entrada
        Response responseValidate = authenticationService.validateInput(OperationType.VALIDATE_OTP, request);
        if (!responseValidate.getErrorCode().equals("00")) {
            log.error(PETICION + responseValidate.getErrorMessage());
            this.authenticationService.requestInputMask(OperationType.LOGIN, request);
            return Mono.just(responseValidate);
        }
        request.setUserLogin(cryptoUtility.decryptAES(request.getUserLogin()));
        request.setOtp(cryptoUtility.decryptAES(request.getOtp()));
        request.setPin(cryptoUtility.decryptAES(request.getPin()));

        return authenticationService.validateOTP(Mono.just(request))
                .flatMap(response -> {
                    StatusCode statusCode = statusCodeConfig.of(response.getErrorCode());
                    if (StatusCode.Level.SUCCESS == statusCode.getLevel()) {
                        return authenticationService.additionalValidations(Mono.just(request), OperationType.VALIDATE_OTP);

                    }
                    response.setErrorCode(statusCode.getCode());
                    response.setErrorCode(statusCode.getMessage());
                    return Mono.just(response);

                }).flatMap(this::logResponse).onErrorResume(e -> {
                    log.error(e.getMessage());

                    StatusCode statusCode = statusCodeConfig.of(TRANSACTION_FAILED);
                    Response response = new Response();
                    response.setErrorType(ERROR_TYPE);
                    response.setErrorCode(statusCode.getCode());
                    response.setErrorMessage(statusCode.getMessage());
                    return Mono.just(response);
                });
    }

    @PostMapping(value = "${spring.application.services.rest.resend-otp}")
    public  Mono<Response> resendOTP(@NotNull @RequestBody Request request) throws ParseException, CloneNotSupportedException, JsonProcessingException {

        request.setCorrelationId(this.authenticationService.asignarCorrelativo(request.getCorrelationId()));
        log.info("*********** RESEND OTP ***********");

        // validate datos de entrada
        Response responseValidate = authenticationService.validateInput(OperationType.RESEND_OTP, request);
        if (!responseValidate.getErrorCode().equals("00")) {
            log.error(PETICION + responseValidate.getErrorMessage());
            this.authenticationService.requestInputMask(OperationType.RESEND_OTP, request);
            return Mono.just(responseValidate);
        }

        request.setUserLogin(cryptoUtility.decryptAES(request.getUserLogin()));

        return authenticationService.resendOTP(Mono.just(request)).flatMap(this::logResponse)
                .onErrorResume(e -> {
                    log.error(e.getMessage());

                    StatusCode statusCode = statusCodeConfig.of(TRANSACTION_FAILED);
                    Response response = new Response();
                    response.setErrorType(ERROR_TYPE);
                    response.setErrorCode(statusCode.getCode());
                    response.setErrorMessage(statusCode.getMessage());
                    return Mono.just(response);
                });
    }

    // PASSWORD
    @PostMapping(value = "${spring.application.services.rest.resetPassword}")
    public  Mono<Response> resetPassword(@NotNull @RequestBody Request request) throws ParseException, CloneNotSupportedException, JsonProcessingException {
        request.setCorrelationId(this.authenticationService.asignarCorrelativo(request.getCorrelationId()));
        log.info("*********** RESET PASSWORD ***********");

        // validate datos de entrada
        Response responseValidate = authenticationService.validateInput(OperationType.RESET_PASSWORD, request);
        if (!responseValidate.getErrorCode().equals("00")) {
            log.error(PETICION + responseValidate.getErrorMessage());
            this.authenticationService.requestInputMask(OperationType.LOGIN, request);
            return Mono.just(responseValidate);
        }

        request.setUserLogin(cryptoUtility.decryptAES(request.getUserLogin()));
        request.setOtp(cryptoUtility.decryptAES(request.getOtp()));
        request.setNewpin(cryptoUtility.decryptAES(request.getNewpin()));
        request.setConfirmnewpin(cryptoUtility.decryptAES(request.getConfirmnewpin()));
        return authenticationService
                .process(Mono.just(request), OperationType.RESET_PASSWORD, null)
                .flatMap(response -> {
                    StatusCode statusCode = statusCodeConfig.of(response.getErrorCode());

                    // Aplicar validaciones adicionales si el reset es exitoso
                    if (StatusCode.Level.SUCCESS == statusCode.getLevel()) {
                        // AUDITORIA
                        this.pushAuditService.pushAudit(
                                this.pushAuditService.generarAudit(
                                        request.getUserLogin(),
                                        "RESETPASSWORD",
                                        request.getCorrelationId(),
                                        "Se ha reseteado la contraseña.",
                                        null)
                        );
                        // Guardar el dispositivo
                        ExecutorService taskThread = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
                        taskThread.submit(() -> this.authenticationService.saveRegister(request.getUserLogin(), request.getImei(), ClientType.resolve(request.getSource())));
                        request.setPin(request.getNewpin());
                        return authenticationService.additionalValidations(Mono.just(request), OperationType.RESET_PASSWORD);
                    }
                    authenticationService.saveRegisterChangePassword(request.getUserLogin(), response.getErrorCode());

                    response.setErrorCode(statusCode.getCode());
                    response.setErrorMessage(statusCode.getMessage());
                    return Mono.just(response);
                }).flatMap(this::logResponse).onErrorResume(e -> errorResumen(e, request));
    }

    @PostMapping(value = "${spring.application.services.rest.changePassword}")
    public  Mono<Response> changePassword(@NotNull @RequestBody Request request) throws ParseException, CloneNotSupportedException, JsonProcessingException {
        request.setCorrelationId(this.authenticationService.asignarCorrelativo(request.getCorrelationId()));
        log.info("*********** CHANGE PASSWORD ***********");
        this.authenticationService.requestInputMask(OperationType.CHANGE_PASSWORD, request);

        // validate datos de entrada
        Response responseValidate = authenticationService.validateInput(OperationType.CHANGE_PASSWORD, request);
        if (!responseValidate.getErrorCode().equals("00")) {
            log.error("petición  recibida " + responseValidate.getErrorMessage());
            return Mono.just(responseValidate);
        }

        request.setUserLogin(cryptoUtility.decryptAES(request.getUserLogin()));
        request.setPin(cryptoUtility.decryptAES(request.getPin()));
        request.setNewpin(cryptoUtility.decryptAES(request.getNewpin()));
        request.setConfirmnewpin(cryptoUtility.decryptAES(request.getConfirmnewpin()));
        AtomicReference<Response> respValAddAtomicReference = new AtomicReference<>();

        return authenticationService.additionalValidations(Mono.just(request), OperationType.CHANGE_PASSWORD)
                .flatMap(respUser -> {
                    if (respUser.getErrorCode().equals(StatusCode.Level.SUCCESS.value())) {
                        respValAddAtomicReference.set(respUser);
                        request.setUser(new User());
                        if (request.getIdno() != null) {
                            request.getUser().setIdno(request.getIdno());
                        }
                        if (request.getDob() != null) {
                            request.getUser().setDob(request.getDob() + " 00:00:00.0");
                        }

                        if (respUser.getUser() != null) {
                            request.setUser(respUser.getUser());
                        }
                        return authenticationService.process(Mono.just(request), OperationType.CHANGE_PASSWORD, null);
                    }
                    return Mono.just(respUser);
                }).flatMap(resp -> {
                            if (resp.getErrorCode().equals(StatusCode.Level.SUCCESS.value())) {
                                // AUDITORIA
                                this.pushAuditService.pushAudit(
                                        this.pushAuditService.generarAudit(
                                                request.getUserLogin(),
                                                "CHANGEPASSWORD",
                                                request.getCorrelationId(),
                                                "Se ha cambiado la contraseña.",
                                                null)
                                );
                                resp.setUser(respValAddAtomicReference.get().getUser());
                            }
                            authenticationService.saveRegisterChangePassword(request.getUserLogin(), resp.getErrorCode());
                            return Mono.just(resp);
                        }
                ).onErrorResume(e -> errorResumen(e, request));

    }

    @GetMapping(value = "${spring.application.services.rest.validatePasswordFormat}")
    public  Response validatePasswordFormat(@NotNull @RequestParam String ppassword,
                                                 @NotNull @RequestParam String pdocumentNumber,
                                                 @NotNull @RequestParam String pcellphone,
                                                 @NotNull @RequestParam String dob) throws UnsupportedEncodingException {
        log.info("*********** VALIDATE PASSWORD FORMAT ***********");

        String password = cryptoUtility.decryptAES(URLDecoder.decode(ppassword, StandardCharsets.UTF_8.name()));
        String documentNumber = cryptoUtility.decryptAES(URLDecoder.decode(pdocumentNumber, StandardCharsets.UTF_8.name()));
        String cellphone = cryptoUtility.decryptAES(URLDecoder.decode(pcellphone, StandardCharsets.UTF_8.name()));

        StatusCode statusCode = statusCodeConfig.of(INVALID_CLV);
        Response response = new Response();
        response.setErrorType(ERROR_TYPE);
        response.setErrorCode(statusCode.getCode());
        response.setErrorMessage(statusCode.getMessage());

        if (validationPasswordService.isValidPasswordFormat(password, documentNumber, cellphone, dob, UtilHelper.DATE_FORMAT_DOB_QUERY_USER_INFO_MAHINDRA)) {
            statusCode = statusCodeConfig.of("OK");
            response.setErrorType(null);
            response.setErrorCode(statusCode.getCode());
            response.setErrorMessage(statusCode.getMessage());
        }

        return response;
    }


    @GetMapping(value = "/queryUserInfo/{userLogin}/{userType}")
    public  Mono<Response> queryUserInfo(@NotNull @PathVariable String userLogin,
                                              @NotNull @PathVariable String userType,
                                              @RequestHeader String correlationId) {

        Request request = new Request();
        request.setUserLogin(userLogin);
        request.setSource(userType);
        request.setCorrelationId(correlationId);
        return Mono.just(queryUserInfoService.queryUserInfo(Mono.just(request), request.getUserLogin()));
    }


    // LOGIN
    @PostMapping(value = "${spring.application.services.rest.login}")
    public  Mono<Response> login(@NotNull @RequestBody Request request) throws ParseException, CloneNotSupportedException, JsonProcessingException {

        request.setCorrelationId(this.authenticationService.asignarCorrelativo(request.getCorrelationId()));
        log.info("*********** LOGIN ***********");

        // validate datos de entrada

        Response responseValidate = authenticationService.validateInput(OperationType.LOGIN, request);
        if (!responseValidate.getErrorCode().equals("00")) {
            log.error("petición de login recibida " + responseValidate.getErrorMessage());
            this.authenticationService.requestInputMask(OperationType.LOGIN, request);
            return Mono.just(responseValidate);
        }

        request.setUserLogin(cryptoUtility.decryptAES(request.getUserLogin()));
        request.setPin(cryptoUtility.decryptAES(request.getPin()));
        return authenticationService
                .process(Mono.just(request), OperationType.LOGIN, null)
                .flatMap(response -> {
                    authenticationService.asignarCorrelativo(request.getCorrelationId());
                    StatusCode statusCode = statusCodeConfig.of(response.getErrorCode());

                    // Aplicar validaciones adicionales si el login es exitoso
                    if (StatusCode.Level.SUCCESS == statusCode.getLevel()) {
                        return successAddValidations(response, request);
                    }
                    return errorGenerate(statusCode, response, request);
                }).onErrorResume(e -> {
                    log.error(e.getMessage());

                    if (e instanceof CommunicationException) {
                        Response response = new Response();
                        response.setErrorType(ERROR_TYPE);
                        response.setErrorCode(((CommunicationException) e).getCode());
                        response.setErrorMessage(e.getMessage());
                        return Mono.just(response);
                    }
                    if (e instanceof ServiceException) {
                        Response response = new Response();
                        response.setErrorType(ErrorType.DATA.name());
                        response.setErrorCode(((ServiceException) e).getCode());
                        response.setErrorMessage(e.getMessage());
                        return Mono.just(response);
                    }

                    StatusCode statusCode = statusCodeConfig.of(BAD_CREDENTIALS);
                    Response response = new Response();
                    response.setErrorType(ERROR_TYPE);
                    response.setErrorCode(statusCode.getCode());
                    response.setErrorMessage(statusCode.getMessage());
                    return Mono.just(response);
                });
    }


    private Mono<Response> errorGenerate(StatusCode statusCode, Response response, Request request) {
        ExecutorService taskThread = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        // Notificar error de usuario bloqueado MH
        if (statusCode.getExtCode().equals("002100") || statusCode.getExtCode().equals("001992")) {
            taskThread.submit(() -> authenticationService.notifyBlockUser(request));
        }

        if (statusCode.getExtCode().equals("003590")) {
            response.setErrorType(null);
            response.setErrorMessage(statusCode.getMessage());
            response.setErrorCode(statusCode.getCode());
            return Mono.just(response);
        }

        if (response.getErrorCode().equals("408")) {
            return Mono.error(new CommunicationException(response.getErrorCode(), response.getErrorMessage()));
        }

        return Mono.error(new ServiceException(ErrorType.DATA, response.getErrorCode(), response.getErrorMessage()));
    }


    private Mono<Response> successAddValidations(Response response, Request request) {
        if ((request.getChannel().equals("GATEWAY")) &&
                (request.getSource().equals("CHANNEL")) &&
                (request.getImei().equals("IMEI-GATEWAY"))) {
            response.getUser().setProfile(null);
            return Mono.just(response);
        } else {
            //VERIFICAR SI EL USUARIO PERTENECE AL ORIGEN DONDE SE INVOCA
            if ((!response.getUser().getUserType().equals(request.getSource())) && (
                    request.getSource().equals(ClientType.SUBSCRIBER.name()) &&
                            (response.getUser().getUserType().equals("CHANNEL"))
            )) {
                log.error("Intento de logeo de un usuario de origen no permitido");
                Response responseAux = new Response();
                responseAux.setErrorType(ERROR_TYPE);
                responseAux.setErrorCode("01");
                responseAux.setErrorMessage("Cuenta no existe o clave inválida.");
                return Mono.just(responseAux);
            }

            request.setUser(response.getUser());

            // Aplicar validaciones adicionales
            return authenticationService.additionalValidations(Mono.just(request), OperationType.LOGIN);
        }
    }

    @PostMapping(value = "/encrypt")
    public  Response encrypt(@RequestHeader(value = "Token") String token, @RequestBody Request request) {
        return this.authenticationService.encrypt(request, token);
    }

    @PostMapping(value = "/dencrypt")
    public  Response dencrypt(@RequestHeader(value = "Token") String token, @RequestBody Request request) {
        return this.authenticationService.decrypt(request, token);
    }



    private Mono<Response> errorResumen(Throwable e, Request request){
        log.error(e.getMessage());

        StatusCode statusCode = statusCodeConfig.of(TRANSACTION_FAILED);
        Response response = new Response();
        response.setErrorType(ERROR_TYPE);
        response.setErrorCode(statusCode.getCode());
        response.setErrorMessage(statusCode.getMessage());
        authenticationService.saveRegisterChangePassword(request.getUserLogin(), response.getErrorCode());
        return Mono.just(response);
    }

    private Mono<Response> logResponse(Response response){
        try {
            log.info(this.objectMapper.writer().writeValueAsString(response));
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }
        return Mono.just(response);
    }
}

