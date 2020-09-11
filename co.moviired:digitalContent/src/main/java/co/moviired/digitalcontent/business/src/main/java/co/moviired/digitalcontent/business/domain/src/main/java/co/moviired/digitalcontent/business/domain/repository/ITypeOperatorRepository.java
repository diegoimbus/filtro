package co.moviired.digitalcontent.business.domain.repository;


import co.moviired.digitalcontent.business.domain.entity.TypeOperator;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.io.Serializable;

@Repository
public interface ITypeOperatorRepository extends CrudRepository<TypeOperator, Integer>, Serializable {

}

