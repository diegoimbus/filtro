package co.moviired.digitalcontent.business.domain.repository;

import co.moviired.digitalcontent.business.domain.entity.PinHistory;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.Optional;

@Repository
public interface IPinHistoryRepository extends CrudRepository<PinHistory, Integer>, Serializable {

    @Query("SELECT p FROM PinHistory p WHERE (p.authorizationCode = ?1 OR p.transferId = ?2) AND (p.sendMail = true OR p.sendMail = true)")
    Optional<PinHistory> findPin(String authorizationCode, String transferId);
}

