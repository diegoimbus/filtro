package co.moviired.supportaudit.audit;

import co.moviired.audit.repository.RabbitMqRepository;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;

@Service
public class PushService extends co.moviired.audit.service.PushAuditService {
    public PushService(@NotNull RabbitMqRepository rabbitMqRepository) {
        super(rabbitMqRepository);
    }
}

