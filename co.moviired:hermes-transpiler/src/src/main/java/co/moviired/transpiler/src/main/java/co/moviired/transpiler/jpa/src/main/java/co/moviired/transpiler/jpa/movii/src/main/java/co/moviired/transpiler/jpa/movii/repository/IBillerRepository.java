package co.moviired.transpiler.jpa.movii.repository;

import co.moviired.transpiler.jpa.movii.domain.Biller;
import org.springframework.data.repository.CrudRepository;

import java.io.Serializable;

public interface IBillerRepository extends CrudRepository<Biller, Integer>, Serializable {

    Biller getByEanCode(String eanCode);

    Biller getByBillerCode(String billerCode);

}

