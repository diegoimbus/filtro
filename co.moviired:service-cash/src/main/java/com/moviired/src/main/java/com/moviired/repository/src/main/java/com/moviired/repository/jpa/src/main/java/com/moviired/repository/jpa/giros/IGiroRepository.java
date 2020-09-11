package com.moviired.repository.jpa.giros;

import com.moviired.model.entities.giros.Giro;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

@Repository
public interface IGiroRepository extends Serializable, CrudRepository<Giro, Integer> {

    @Query("SELECT estadoId FROM Giro g " +
            " WHERE g.envio = :transactionId")
    Integer findByEnvio(@Param("transactionId") String transactionId);

    Set<Giro> findByFechaRegistroGreaterThanAndOrigenAndEstadoId(Date dateFilter,
                                                                 String origen,
                                                                 Integer estadoId);
}
