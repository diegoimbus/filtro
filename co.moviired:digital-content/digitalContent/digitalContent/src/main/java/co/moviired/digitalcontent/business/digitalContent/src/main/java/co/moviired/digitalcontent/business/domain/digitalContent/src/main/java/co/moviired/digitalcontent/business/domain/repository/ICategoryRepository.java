package co.moviired.digitalcontent.business.domain.repository;


import co.moviired.digitalcontent.business.domain.entity.Category;
import co.moviired.digitalcontent.business.domain.enums.GeneralStatus;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

@Repository
public interface ICategoryRepository extends CrudRepository<Category, Integer>, Serializable {

    Category getCategoryById(Integer id);

    List<Category> findAllByStatus(GeneralStatus status);

    Optional<Category> findByIdAndStatus(Integer id, GeneralStatus status);

}

