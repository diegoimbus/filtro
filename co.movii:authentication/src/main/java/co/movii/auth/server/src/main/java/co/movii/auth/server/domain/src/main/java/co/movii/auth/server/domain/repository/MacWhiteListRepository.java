package co.movii.auth.server.domain.repository;

/*
 * Copyright @2019. Movii, SAS. Todos los derechos reservados.
 *
 * @author Ronel Rivas
 * @version 1, 2019
 * @since 1.0
 */

import co.movii.auth.server.domain.entity.MacWhiteList;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

@Repository
public interface MacWhiteListRepository extends CrudRepository<MacWhiteList, Long>, Serializable {

    Optional<MacWhiteList> findByPhoneNumberAndMac(String phoneNumber, String mac);

    List<MacWhiteList> findByPhoneNumberAndMacIn(String phoneNumber, List<String> mac);

}
