package co.moviired.digitalcontent.business.domain.repository;

import co.moviired.digitalcontent.business.domain.entity.Client;
import org.springframework.data.repository.CrudRepository;

import java.io.Serializable;

public interface IClientRepository extends CrudRepository<Client, Integer>, Serializable {
}

