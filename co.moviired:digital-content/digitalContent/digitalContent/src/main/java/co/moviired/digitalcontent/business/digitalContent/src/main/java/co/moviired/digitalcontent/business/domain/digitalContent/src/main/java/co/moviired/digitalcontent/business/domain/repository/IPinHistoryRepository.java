package co.moviired.digitalcontent.business.domain.repository;

import co.moviired.digitalcontent.business.domain.entity.PinHistory;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.io.Serializable;

@Repository
public interface IPinHistoryRepository extends CrudRepository<PinHistory, Integer>, Serializable {
}

