package co.moviired.supportaudit.service;

/**
 * Copyright @2019. Movii, SAS. Todos los derechos reservados.
 *
 * @author Suarez, Juan
 * @version 1, 2019
 * @since 1.0
 */

import co.moviired.audit.domain.dto.AuditDto;
import co.moviired.supportaudit.domain.entity.Audit;
import co.moviired.supportaudit.domain.repository.IAuditRepository;
import co.moviired.supportaudit.helper.UtilHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;


@Slf4j
@Service
public class ReceiverService {

    private final IAuditRepository auditRepository;

    public ReceiverService(IAuditRepository pauditRepository) {

        this.auditRepository = pauditRepository;
    }

    /**
     * Recibimos los mensajes encolados en el servidor de RabbitMQ
     *
     * @param wrequest : cuerpo del sms de tipo String.
     * @code queue_sms :  nombre de la cola en RabbitMQ donde habitan los sms.
     */
    @RabbitListener(queues = "${properties.audit.queue_name}")
    public void consumeQueue(String wrequest) {
        log.debug("lleego mensaje a la cola");
        AuditDto data = UtilHelper.parseJsonToObject(wrequest);
        if (data != null) {
            this.auditRepository.insert(mapAuditDtoToAudit(data)).subscribe();
        }

    }

    // Transformar un Entity a su DTO
    private static Audit mapAuditDtoToAudit(@NotNull AuditDto p) {
        return Audit.builder()
                .id(p.getId())
                .correlationId(p.getCorrelationId())
                .username(p.getUsername())
                .operation(p.getOperation())
                .operationText(p.getOperationText())
                .operationDetail(p.getOperationDetail())
                .date(p.getDate())
                .build();
    }
}

