package co.moviired.support.otp.service;

/*
 * Copyright @2019. MOVIIRED, SAS. Todos los derechos reservados.
 *
 * @author RIVAS, Ronel
 * @version 1, 2019-06-27
 * @since 1.0
 */

import co.moviired.support.otp.model.dto.GenerateRequest;
import co.moviired.support.otp.model.dto.Request;
import co.moviired.support.otp.model.dto.Response;
import co.moviired.support.otp.model.enums.NotifyChannel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;
import java.util.Random;

@RestController
@RequestMapping("${spring.application.root}")
public class OtpController {

    private final OtpService otpService;

    public OtpController(@NotNull OtpService otpService) {
        super();
        this.otpService = otpService;
    }

    @GetMapping(value = "${spring.application.services.ping}")
    public final Mono<ResponseEntity<Mono<String>>> ping() {
        return Mono.just(new ResponseEntity<>(otpService.ping(), HttpStatus.OK));
    }

    @Scheduled(fixedRateString = "${otp.expirationJobRate}")
    public void expire() {
        if (new Random().nextInt() % 2 == 0) {
            this.otpService.expireOtps();
        }
    }

    @PostMapping(value = "/generate/{component}/{origin}/{phoneNumber}")
    public final Mono<ResponseEntity<Mono<Response>>> generate(
            @PathVariable @NotNull String component,
            @PathVariable @NotNull String origin,
            @PathVariable @NotNull String phoneNumber,
            @RequestBody GenerateRequest request
    ) {
        return Mono.just(new ResponseEntity<>(otpService.generate(this.parseRequest(component, origin, phoneNumber, request, null, null)), HttpStatus.ACCEPTED));
    }

    @PutMapping(value = "/resend/{component}/{origin}/{phoneNumber}/{notifyChannel}")
    public final Mono<ResponseEntity<Mono<Response>>> resend(
            @PathVariable @NotNull String component,
            @PathVariable @NotNull String origin,
            @PathVariable @NotNull String phoneNumber,
            @PathVariable @NotNull NotifyChannel notifyChannel
    ) {
        return Mono.just(new ResponseEntity<>(otpService.resend(this.parseRequest(component, origin, phoneNumber, null, null, notifyChannel)), HttpStatus.ACCEPTED));
    }

    @PostMapping(value = "/validate/{component}/{origin}/{phoneNumber}/{otp}")
    public final Mono<ResponseEntity<Mono<Response>>> validate(
            @PathVariable @NotNull String component,
            @PathVariable @NotNull String origin,
            @PathVariable @NotNull String phoneNumber,
            @PathVariable @NotNull String otp
    ) {
        return Mono.just(new ResponseEntity<>(otpService.validate(this.parseRequest(component, origin, phoneNumber, null, otp, null)), HttpStatus.ACCEPTED));
    }

    // Generar el Mono<Request>
    private Mono<Request> parseRequest(
            @NotNull String component,
            @NotNull String origin,
            @NotNull String phoneNumber,
            GenerateRequest generateRequest,
            String otp,
            NotifyChannel notifyChannel
    ) {
        Request req;
        if (generateRequest != null) {
            req = Request.builder()
                    .component(component)
                    .origin(origin)
                    .phoneNumber(phoneNumber)
                    .otp(otp)
                    .notifyChannel(notifyChannel)
                    .email(generateRequest.getEmail())
                    .otpAlphanumeric(generateRequest.getOtpAlphanumeric())
                    .otpLength(generateRequest.getOtpLength())
                    .otpExpiration(generateRequest.getOtpExpiration())
                    .templateCode(generateRequest.getTemplateCode())
                    .variables(generateRequest.getVariables())
                    .sendSms(Boolean.TRUE.equals(generateRequest.getSendSms()))
                    .notifyChannel(generateRequest.getNotifyChannel())
                    .build();
        } else {
            req = Request.builder()
                    .component(component)
                    .origin(origin)
                    .phoneNumber(phoneNumber)
                    .otp(otp)
                    .notifyChannel(notifyChannel)
                    .build();
        }

        return Mono.just(req);
    }

}

