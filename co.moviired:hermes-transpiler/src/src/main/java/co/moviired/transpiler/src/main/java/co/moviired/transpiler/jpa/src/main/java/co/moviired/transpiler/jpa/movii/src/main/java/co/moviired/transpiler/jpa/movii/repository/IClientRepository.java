package co.moviired.transpiler.jpa.movii.repository;

import co.moviired.transpiler.jpa.movii.domain.Client;
import org.springframework.data.repository.CrudRepository;

import java.io.Serializable;

public interface IClientRepository extends CrudRepository<Client, Integer>, Serializable {

}

