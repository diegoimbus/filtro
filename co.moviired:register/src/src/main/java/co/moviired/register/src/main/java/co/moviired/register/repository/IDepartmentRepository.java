package co.moviired.register.repository;

import co.moviired.register.domain.dto.QueryDepartmentDTO;
import co.moviired.register.domain.model.entity.Department;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.List;

@Repository
public interface IDepartmentRepository extends Serializable, CrudRepository<Department, Integer> {

    @Query(value = "SELECT d.id, d.name, m.id munId, m.name munName, m.dane_code daneCode "
            + "FROM department d INNER JOIN municipality m on d.id = m.department_id", nativeQuery = true)
    List<QueryDepartmentDTO> findDepartments();

}

