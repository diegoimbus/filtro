package co.moviired.auth.server.domain.repository;

/*
 * Copyright @2020. Moviired, SAS. Todos los derechos reservados.
 *
 * @author Ronel Rivas
 * @version 1, 2019
 * @since 1.0
 */

import co.moviired.auth.server.domain.entity.MacBlackList;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

@Repository
public interface MacBlackListRepository extends CrudRepository<MacBlackList, Long>, Serializable {

    Optional<MacBlackList> findByMac(String mac);

    List<MacBlackList> findByMacIn(List<String> mac);

}

