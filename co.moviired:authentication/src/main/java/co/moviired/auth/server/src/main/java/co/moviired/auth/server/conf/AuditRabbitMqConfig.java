package co.moviired.auth.server.conf;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.validation.constraints.NotNull;

@Configuration
public class AuditRabbitMqConfig extends co.moviired.audit.config.RabbitMqConfig {

    public AuditRabbitMqConfig(@NotNull Environment env) {
        setExchangeName(env.getProperty("properties.audit.exchange_name"));
        setRoutingKey(env.getProperty("properties.audit.key_name"));
        setQueueName(env.getProperty("properties.audit.queue_name"));
    }
}

