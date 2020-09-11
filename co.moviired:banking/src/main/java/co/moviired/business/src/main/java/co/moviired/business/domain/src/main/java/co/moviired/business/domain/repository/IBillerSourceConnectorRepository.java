package co.moviired.business.domain.repository;
/*
 * Copyright @2019. Movii, SAS. Todos los derechos reservados.
 *
 * @author Bejarano, Cindy
 * @version 1, 2019
 * @since 1.0
 */

import co.moviired.business.domain.entity.BillerSourceConnector;
import co.moviired.business.domain.entity.Connector;
import co.moviired.business.domain.entity.Source;
import co.moviired.business.domain.jpa.movii.entity.Biller;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.Optional;

@Repository
public interface IBillerSourceConnectorRepository extends CrudRepository<BillerSourceConnector, Integer>, Serializable {

    BillerSourceConnector findFirstByBillerAndAndSourceAndConnector(Biller biller, Source source, Connector connector);

    Optional<BillerSourceConnector> findFirstByBillerAndAndSource(Biller biller, Source source);

}
