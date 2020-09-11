package co.moviired.supportaudit.service;
/**
 * Copyright @2019. Movii, SAS. Todos los derechos reservados.
 *
 * @author Suarez, Juan
 * @version 1, 2019
 * @since 1.0
 */

import co.moviired.audit.domain.dto.AuditDto;
import co.moviired.base.domain.enumeration.ErrorType;
import co.moviired.base.domain.exception.ServiceException;
import co.moviired.supportaudit.domain.entity.Audit;
import co.moviired.supportaudit.domain.repository.IAuditRepository;
import co.moviired.supportaudit.domain.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


@Service
@Slf4j
public final class AuditService implements Serializable {

    private static final int NUM_00 = 00;
    private static final int NUM_59 = 59;
    private static final int NUM_23 = 23;
    private static final long serialVersionUID = 618849983555874596L;
    private final IAuditRepository auditRepository;

    public AuditService(IAuditRepository pauditRepository) {
        this.auditRepository = pauditRepository;
    }

    private Mono<Response> apply(List<Audit> list) {
        List<AuditDto> audits = new ArrayList<>();
        for (Audit u : list) {
            audits.add(mapAuditToAuditDTO(u));
        }

        Response response = new Response();
        response.setCode("00");
        response.setMessage("Exito");
        response.setAudits(audits);
        // Establecer la respuesta correcta
        return Mono.just(response);
    }

    public Mono<Response> getAllAudit() {

        return this.auditRepository.findAll()
                .switchIfEmpty(Mono.error(new ServiceException(ErrorType.PROCESSING, "01", "No items found")))
                .collectList()
                .flatMap(list ->
                        // Establecer la respuesta correcta
                        apply(list))
                .onErrorResume(e -> {
                    log.error(e.getMessage());
                    // Establecer el código y mensaje de error específico
                    Response response = new Response();
                    response.setCode("99");
                    response.setMessage(e.getMessage());
                    return Mono.just(response);
                });

    }

    public Mono<Response> getAudit(AuditDto audit) {

        return this.getCustomAudit(audit)
                .switchIfEmpty(Mono.error(new ServiceException(ErrorType.PROCESSING, "01", "No items found")))
                .collectList()
                .flatMap(list ->
                        // Establecer la respuesta correcta
                        apply(list))
                .onErrorResume(e -> {
                    log.error(e.getMessage());
                    // Establecer el código y mensaje de error específico
                    Response response = new Response();
                    response.setCode("99");
                    response.setMessage(e.getMessage());
                    return Mono.just(response);
                });

    }

    private Flux<Audit> getCustomAudit(AuditDto audit) {

        Calendar startDate = AuditService.dateToCalendar(audit.getStartDate());
        Calendar endDate = AuditService.dateToCalendar(audit.getEndDate());

        startDate.set(Calendar.HOUR_OF_DAY, AuditService.NUM_00);
        startDate.set(Calendar.MINUTE, AuditService.NUM_00);
        startDate.set(Calendar.SECOND, AuditService.NUM_00);
        audit.setStartDate(startDate.getTime());

        endDate.set(Calendar.HOUR_OF_DAY, AuditService.NUM_23);
        endDate.set(Calendar.MINUTE, AuditService.NUM_59);
        endDate.set(Calendar.SECOND, AuditService.NUM_59);
        audit.setEndDate(endDate.getTime());


        if (audit.getOperation() != null && audit.getUsername() != null && !audit.getUsername().isEmpty()) {
            return this.auditRepository.findByOperationAndUsernameContainsAndDateBetween(audit.getOperation(), audit.getUsername(), audit.getStartDate(), audit.getEndDate());
        }
        if (audit.getOperation() != null && (audit.getUsername() == null || audit.getUsername().isEmpty())) {
            return this.auditRepository.findByOperationContainsAndDateBetween(audit.getOperation(), audit.getStartDate(), audit.getEndDate());
        }
        if ((audit.getOperation() == null) && audit.getUsername() != null && !audit.getUsername().isEmpty()) {
            return this.auditRepository.findByUsernameContainsAndDateBetween(audit.getUsername(), audit.getStartDate(), audit.getEndDate());
        }

        return this.auditRepository.findByDateBetween(audit.getStartDate(), audit.getEndDate());
    }


    // Transformar un Entity a su DTO
    private static AuditDto mapAuditToAuditDTO(@NotNull Audit p) {
        return AuditDto.builder()
                .id(p.getId())
                .correlationId(p.getCorrelationId())
                .username(p.getUsername())
                .operation(p.getOperation())
                .operationText(p.getOperationText())
                .operationDetail(p.getOperationDetail())
                .date(p.getDate())
                .build();
    }

    public static Calendar dateToCalendar(Date date) {
        Calendar cal = null;
        cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }
}

