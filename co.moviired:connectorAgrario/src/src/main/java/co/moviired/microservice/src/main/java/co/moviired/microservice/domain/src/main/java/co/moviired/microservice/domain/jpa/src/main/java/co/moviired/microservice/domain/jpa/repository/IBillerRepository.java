package co.moviired.microservice.domain.jpa.repository;

import co.moviired.microservice.domain.jpa.entity.Biller;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.io.Serializable;

@Repository
public interface IBillerRepository extends CrudRepository<Biller, Integer>, Serializable {

    Biller getByEanCode(String eanCode);

}
