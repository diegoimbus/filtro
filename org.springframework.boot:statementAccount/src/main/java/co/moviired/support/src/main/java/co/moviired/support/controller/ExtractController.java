package co.moviired.support.controller;

import co.moviired.support.domain.dto.ExtractDTO;
import co.moviired.support.properties.CertificatesProperties;
import co.moviired.support.service.ExtractService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;

import static co.moviired.support.util.ConstantsHelper.AUTHORIZATION_HEADER;

@Controller
@RequestMapping("/")
public class ExtractController {

    private final ExtractService extractService;
    private final CertificatesProperties certificatesProperties;

    public ExtractController(
            @NotNull ExtractService pextractService,
            @NotNull CertificatesProperties pcertificatesProperties) {
        this.extractService = pextractService;
        this.certificatesProperties = pcertificatesProperties;
    }

    @ResponseBody
    @GetMapping(value = "${spring.application.services.rest.getAvailableExtracts}")
    public final Mono<ResponseEntity<Mono<ExtractDTO>>> getAvailableExtracts(
            @RequestHeader(value = AUTHORIZATION_HEADER) String authorizationHeader) {
        return Mono.just(extractService.getAvailableExtracts(authorizationHeader))
                .flatMap(response -> Mono.just(new ResponseEntity<>(response, HttpStatus.OK)));
    }

    @ResponseBody
    @PostMapping(value = "${spring.application.services.rest.requestExtract}")
    public final Mono<ResponseEntity<Mono<ExtractDTO>>> requestExtract(@RequestHeader(value = AUTHORIZATION_HEADER) String authorizationHeader, @RequestBody ExtractDTO request) {
        return Mono.just(new ResponseEntity<>(extractService.requestExtract(authorizationHeader, request), HttpStatus.OK));
    }

    @GetMapping(value = "${spring.application.services.rest.getDocument}")
    public final Object getPdf(@PathVariable String token) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            return new ResponseEntity<>(extractService.getPdf(token), headers, HttpStatus.OK);
        } catch (Exception e) {
            return certificatesProperties.getNotFoundView();
        }
    }
}

