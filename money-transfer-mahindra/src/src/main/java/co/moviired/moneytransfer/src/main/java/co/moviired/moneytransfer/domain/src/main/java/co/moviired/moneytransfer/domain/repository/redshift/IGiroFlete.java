package co.moviired.moneytransfer.domain.repository.redshift;


import co.moviired.moneytransfer.domain.entity.redshift.GiroFlete;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.io.Serializable;

public interface IGiroFlete extends CrudRepository<GiroFlete, Integer>, Serializable {

    @Query(value = "SELECT * FROM giro_financiero_giro_flete where ?1 between desde AND hasta ", nativeQuery = true)
    GiroFlete findByTarifaBetween(Integer amount);

}

