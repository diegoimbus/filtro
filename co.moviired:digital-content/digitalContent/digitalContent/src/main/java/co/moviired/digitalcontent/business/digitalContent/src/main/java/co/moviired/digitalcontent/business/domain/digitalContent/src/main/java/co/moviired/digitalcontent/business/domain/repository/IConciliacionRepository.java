package co.moviired.digitalcontent.business.domain.repository;


import co.moviired.digitalcontent.business.domain.entity.Conciliacion;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.io.Serializable;

@Repository
public interface IConciliacionRepository extends CrudRepository<Conciliacion, Integer>, Serializable {

    Conciliacion getCategoryById(Integer id);

}

