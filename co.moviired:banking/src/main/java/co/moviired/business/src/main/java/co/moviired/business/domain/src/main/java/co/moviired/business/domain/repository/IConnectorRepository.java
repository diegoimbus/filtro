package co.moviired.business.domain.repository;
/*
 * Copyright @2019. Movii, SAS. Todos los derechos reservados.
 *
 * @author Bejarano, Cindy
 * @version 1, 2019
 * @since 1.0
 */

import co.moviired.business.domain.entity.Connector;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.io.Serializable;


@Repository
public interface IConnectorRepository extends CrudRepository<Connector, Integer>, Serializable {


}


