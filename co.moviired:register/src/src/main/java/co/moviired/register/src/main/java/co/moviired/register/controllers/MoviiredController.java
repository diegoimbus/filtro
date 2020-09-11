package co.moviired.register.controllers;

import co.moviired.base.domain.exception.ParsingException;
import co.moviired.base.helper.CryptoHelper;
import co.moviired.base.util.Security;
import co.moviired.register.domain.dto.*;
import co.moviired.register.domain.enums.register.OperationType;
import co.moviired.register.exceptions.ParseException;
import co.moviired.register.service.MoviiService;
import co.moviired.register.service.MoviiredService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;

import static co.moviired.register.helper.ConstantsHelper.*;

@RestController
@Slf4j
@RequestMapping(PROJECT_PATH)
public final class MoviiredController {

    private final MoviiredService moviiredService;
    private final MoviiService moviiService;
    private final CryptoHelper cryptoHelper;

    public MoviiredController(@NotNull MoviiredService moviiredServiceI,
                              @NotNull MoviiService moviiServiceI,
                              @NotNull @Qualifier(CRYPTO_HELPER) CryptoHelper cryptoHelperI) {
        super();
        this.moviiredService = moviiredServiceI;
        this.cryptoHelper = cryptoHelperI;
        this.moviiService = moviiServiceI;
    }

    @PostMapping(value = "${spring.application.services.moviired.verifyusermh}")
    public Mono<RegisterResponse> verifyUserMahindra(@NotNull @RequestBody RegisterRequest request) throws ParseException, ParsingException, JsonProcessingException {

        log.info("*********** VERIFY USER STATUS - STARTED ***********");

        //Validate input parameters
        RegisterResponse responseValidate = moviiredService.validateInput(OperationType.USER_QUERY_INFO, request);
        if (!responseValidate.getCode().equals("00")) {
            log.error("Verify MH User request received." + responseValidate.getMessage());
            log.info(Security.printIgnore(request.toString(), "pin", "mpin", "otp"));
            log.info("*********** VERIFY USER STATUS - FINISHED ***********");
            return Mono.just(responseValidate);
        }
        request.setUserLogin(cryptoHelper.decoder(request.getUserLogin()));
        return this.moviiredService.validateMHUser(request, OperationType.USER_QUERY_INFO);
    }

    @PostMapping(value = "${spring.application.services.moviired.otp.generate}")
    public Mono<RegisterResponse> otpGenerate(@NotNull @RequestBody Mono<RegisterRequest> request) {
        return this.moviiredService.otpGenerate(request);
    }

    @PostMapping(value = "${spring.application.services.moviired.otp.resend}")
    public Mono<RegisterResponse> otpResend(@NotNull @RequestBody Mono<RegisterRequest> request) {
        return this.moviiredService.otpResend(request);
    }

    @PostMapping(value = "${spring.application.services.moviired.otp.validate}")
    public Mono<RegisterResponse> otpValidate(@NotNull @RequestBody Mono<RegisterRequest> request) {
        return this.moviiredService.otpValidate(request);
    }

    @PostMapping(value = "${spring.application.services.moviired.getUserInfo}")
    public Mono<RegisterResponse> getUserInfo(@NotNull @RequestBody Mono<RegisterRequest> request) {
        return this.moviiredService.getUserInfo(request, true, false);
    }

    @PostMapping(value = "${spring.application.services.moviired.registry}")
    public Mono<RegisterResponse> registry(@NotNull @RequestBody Mono<RegisterRequest> request) {
        return this.moviiredService.registry(request);
    }

    @PostMapping(value = CREATE_PENDING_USER_ROUTE)
    public Mono<RegisterDTO> createPendingRegistration(@NotNull @RequestBody RegisterDTO request) {
        return this.moviiredService.createPendingUser(request.getPhoneNumber(), SUBSCRIBER, request.getReferralCode());
    }

    @PostMapping(value = INACTIVE_PENDING_USER_ROUTE)
    public Mono<RegisterDTO> inactivePendingRegistration(@RequestHeader(value = AUTHORIZATION_HEADER) String authorizationHeader, @PathVariable String origin) {
        return this.moviiredService.inactivePendingUser(moviiService, authorizationHeader, origin);
    }

    @GetMapping(value = VALIDATE_PENDING_USER_ROUTE)
    public Mono<RegisterDTO> validatePending(@PathVariable String origin, @PathVariable(PHONE_NUMBER) String phoneNumber) {
        return this.moviiredService.validatePendingUser(origin, phoneNumber);
    }

    @PostMapping(value = UPDATE_USER_PENDING_UPDATE_ROUTE)
    public Mono<UserPendingUpdateResponse> updateUserPendingUpdate(@NotNull @RequestBody Mono<UserPendingUpdateRequest> request) {
        return this.moviiredService.updateUserPendingUpdate(request);
    }

    @GetMapping(value = VALIDATE_USER_PENDING_UPDATE_ROUTE)
    public Mono<UserPendingUpdateResponse> validatePending(@PathVariable(PHONE_NUMBER) String phoneNumber) {
        return this.moviiredService.validateUserPendingUpdate(phoneNumber);
    }
}

