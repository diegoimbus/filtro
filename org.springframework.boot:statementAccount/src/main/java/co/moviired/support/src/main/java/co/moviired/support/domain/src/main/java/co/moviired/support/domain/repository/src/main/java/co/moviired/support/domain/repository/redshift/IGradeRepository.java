package co.moviired.support.domain.repository.redshift;

import co.moviired.support.domain.entity.redshift.Grade;
import org.springframework.data.repository.CrudRepository;

import java.io.Serializable;

/**
 * Copyright @2019 MOVIIRED. Todos los derechos reservados.
 *
 * @author Rodolfo.rivas
 * @category Consinments
 */
@SuppressWarnings("unchecked")
public interface IGradeRepository extends CrudRepository<Grade, Integer>, Serializable {

    Grade findByGrado(String grade);
}

