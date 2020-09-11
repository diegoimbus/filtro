package co.movii.auth.server.audit;

import co.moviired.audit.repository.RabbitMqRepository;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;

@Service
public class AuditPushService extends co.moviired.audit.service.PushAuditService {
    public AuditPushService(@NotNull RabbitMqRepository rabbitMqRepository) {
        super(rabbitMqRepository);
    }
}

