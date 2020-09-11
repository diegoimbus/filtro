package co.moviired.auth.server.domain.repository;
/*
 * Copyright @2020. Moviired, SAS. Todos los derechos reservados.
 *
 * @author Ronel Rivas
 * @version 1, 2019
 * @since 1.0
 */

import co.moviired.auth.server.domain.entity.AuthExtraValidations;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.io.Serializable;

@Repository
public interface AuthExtraValidationsRepository extends CrudRepository<AuthExtraValidations, Long>, Serializable {

}

