package co.moviired.support.audit;

import co.moviired.support.domain.repository.RabbitMqRepositoryLocal;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;

@Service
public class PushService extends co.moviired.audit.service.PushAuditService {
    public PushService(@NotNull RabbitMqRepositoryLocal rabbitMqRepository) {
        super(rabbitMqRepository);
    }
}

