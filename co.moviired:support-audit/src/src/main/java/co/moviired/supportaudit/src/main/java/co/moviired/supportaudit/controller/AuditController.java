package co.moviired.supportaudit.controller;
/**
 * Copyright @2019. Movii, SAS. Todos los derechos reservados.
 *
 * @author Suarez, Juan
 * @version 1, 2019
 * @since 1.0
 */

import co.moviired.audit.domain.dto.AuditDto;
import co.moviired.audit.service.PushAuditService;
import co.moviired.supportaudit.domain.response.Response;
import co.moviired.supportaudit.service.AuditService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;

@RestController
@Slf4j
@RequestMapping("${server.servlet.context-path}")
public final class AuditController {

    private final PushAuditService publishService;
    private final AuditService auditService;

    public AuditController(@NotNull PushAuditService publishService1, AuditService pauditService) {
        super();
        this.publishService = publishService1;
        this.auditService = pauditService;
    }

    @GetMapping("/")
    public String ping() {
        return "I´m alived";
    }

    @PostMapping(value = "${spring.application.services.rest.pushAudit}")
    public Boolean pushAudit(@RequestBody AuditDto request,
                             @RequestHeader(name = "correlationId") String correlationId) {
        return publishService.pushAudit(request);
    }

    @GetMapping(value = "${spring.application.services.rest.audit}")
    public Mono<Response> getAllAudit() {

        return auditService.getAllAudit();
    }

    @PostMapping(value = "${spring.application.services.rest.audit}")
    public Mono<Response> getAudit(@RequestBody AuditDto request,
                                   @RequestHeader(name = "correlationId") String correlationId) {

        return auditService.getAudit(request);
    }
}

