package co.moviired.support.domain.repository;
/**
 * Copyright @2019. Movii, SAS. Todos los derechos reservados.
 *
 * @author Suarez, Juan
 * @version 1, 2019
 * @since 1.0
 */

import co.moviired.support.conf.RabbitMqConf;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotNull;


@Slf4j
@Repository
public class RabbitMqRepo extends co.moviired.audit.repository.RabbitMqRepository {

    public RabbitMqRepo(@NotNull RabbitTemplate rabbitTemplate, @NotNull RabbitMqConf rabbitMqConf) {
        super(rabbitTemplate, rabbitMqConf);
    }
}

