package co.moviired.supportaudit.domain.repository;
/**
 * Copyright @2019. Movii, SAS. Todos los derechos reservados.
 *
 * @author Suarez, Juan
 * @version 1, 2019
 * @since 1.0
 */

import co.moviired.audit.config.RabbitMqConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotNull;


@Slf4j
@Repository
public class RRabbitMqRepository extends co.moviired.audit.repository.RabbitMqRepository {

    public RRabbitMqRepository(@NotNull RabbitTemplate rabbitTemplate, @NotNull RabbitMqConfig rabbitMqConfig) {
        super(rabbitTemplate, rabbitMqConfig);
    }
}

