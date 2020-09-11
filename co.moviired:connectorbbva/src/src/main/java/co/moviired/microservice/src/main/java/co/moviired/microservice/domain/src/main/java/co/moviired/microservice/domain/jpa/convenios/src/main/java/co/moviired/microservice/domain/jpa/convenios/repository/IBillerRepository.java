package co.moviired.microservice.domain.jpa.convenios.repository;


import co.moviired.microservice.domain.jpa.convenios.entity.Biller;
import org.springframework.data.repository.CrudRepository;

import java.io.Serializable;

public interface IBillerRepository extends CrudRepository<Biller, Integer>, Serializable {

    Biller getByThirdPartyCode(String thirdPartyCode);

}

