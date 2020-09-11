package co.moviired.support.domain.repository.account;

import co.moviired.support.domain.entity.account.BarExemptionDays;
import org.springframework.data.repository.CrudRepository;

import java.io.Serializable;
import java.util.Calendar;

/**
 * Copyright @2019 MOVIIRED. Todos los derechos reservados.
 *
 * @author Rodolfo.rivas
 * @category Consinments
 */

public interface IBarExemptionDaysRepository extends CrudRepository<BarExemptionDays, Integer>, Serializable {

    BarExemptionDays findByDay(Calendar day);
}

