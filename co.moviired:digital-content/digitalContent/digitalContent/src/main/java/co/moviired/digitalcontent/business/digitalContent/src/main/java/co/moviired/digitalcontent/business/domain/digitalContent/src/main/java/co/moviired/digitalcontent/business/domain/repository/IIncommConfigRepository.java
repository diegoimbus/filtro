package co.moviired.digitalcontent.business.domain.repository;

import co.moviired.digitalcontent.business.domain.entity.IncommConfig;
import co.moviired.digitalcontent.business.domain.entity.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.Optional;

@Repository
public interface IIncommConfigRepository extends CrudRepository<IncommConfig, Integer>, Serializable {

    Optional<IncommConfig> findByUser(User user);
}

