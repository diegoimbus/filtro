package co.moviired.microservice.domain.jpa.repository;

/*
 * Copyright @2020. Movii, SAS. Todos los derechos reservados.
 *
 * @author Oscar Lopez
 * @since 1.1.1
 */

import co.moviired.microservice.domain.jpa.entity.Conciliacion;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.io.Serializable;

@Repository
public interface IConciliacionRepository extends CrudRepository<Conciliacion, Integer>, Serializable {

}

