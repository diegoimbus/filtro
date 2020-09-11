package co.moviired.digitalcontent.business.domain.repository;


import co.moviired.digitalcontent.business.domain.entity.SubtypeOperator;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.Optional;

@Repository
public interface ISubtypeOperatorRepository extends CrudRepository<SubtypeOperator, Integer>, Serializable {

    Optional<SubtypeOperator> findBySubtype(String subtype);

}

