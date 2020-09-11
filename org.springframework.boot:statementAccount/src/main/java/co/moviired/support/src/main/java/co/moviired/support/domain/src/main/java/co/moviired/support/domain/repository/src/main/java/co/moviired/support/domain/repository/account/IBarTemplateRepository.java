package co.moviired.support.domain.repository.account;

import co.moviired.support.domain.entity.account.BarTemplate;
import org.springframework.data.repository.CrudRepository;

/**
 * Copyright @2019 MOVIIRED. Todos los derechos reservados.
 *
 * @author Rodolfo.rivas
 * @category Consinments
 */

public interface IBarTemplateRepository extends CrudRepository<BarTemplate, Integer> {

    BarTemplate findByBarType(String barType);
}

