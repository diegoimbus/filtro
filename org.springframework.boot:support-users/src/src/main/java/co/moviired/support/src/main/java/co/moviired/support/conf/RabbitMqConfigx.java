package co.moviired.support.conf;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.validation.constraints.NotNull;

@Configuration
public class RabbitMqConfigx extends co.moviired.audit.config.RabbitMqConfig {

    public RabbitMqConfigx(@NotNull Environment env) {
        setExchangeName(env.getProperty("properties.audit.exchange_name"));
        setRoutingKey(env.getProperty("properties.audit.key_name"));
        setQueueName(env.getProperty("properties.audit.queue_name"));
    }
}

