package co.moviired.support.domain.repository;
/*
 * Copyright @2019. Movii, SAS. Todos los derechos reservados.
 *
 * @author Bejarano, Cindy
 * @version 1, 2019
 * @since 1.0
 */

import co.moviired.support.domain.entity.Module;
import co.moviired.support.domain.entity.Operation;
import co.moviired.support.domain.entity.Profile;
import co.moviired.support.domain.enums.GeneralStatus;
import org.springframework.data.repository.CrudRepository;

import java.io.Serializable;
import java.util.List;

public interface IOperationRepository extends CrudRepository<Operation, Integer>, Serializable {

    List<Operation> findByStatus(GeneralStatus status);

    List<Operation> findByProfiles(Profile profile);

    List<Operation> findByModulesAndStatusIsNot(Module module,GeneralStatus status);

}


