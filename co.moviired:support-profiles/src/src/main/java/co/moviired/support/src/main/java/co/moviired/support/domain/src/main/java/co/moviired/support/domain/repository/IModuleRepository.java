package co.moviired.support.domain.repository;
/*
 * Copyright @2019. Movii, SAS. Todos los derechos reservados.
 *
 * @author Bejarano, Cindy
 * @version 1, 2019
 * @since 1.0
 */

import co.moviired.support.domain.entity.Module;
import org.springframework.data.repository.CrudRepository;

import java.io.Serializable;

public interface IModuleRepository extends CrudRepository<Module, Integer>, Serializable {

}

