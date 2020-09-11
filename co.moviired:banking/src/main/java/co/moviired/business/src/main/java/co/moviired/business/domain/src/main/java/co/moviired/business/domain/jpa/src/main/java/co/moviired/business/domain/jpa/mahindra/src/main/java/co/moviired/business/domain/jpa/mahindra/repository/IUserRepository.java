package co.moviired.business.domain.jpa.mahindra.repository;

import co.moviired.business.domain.jpa.mahindra.entity.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.io.Serializable;

@Repository
public interface IUserRepository extends CrudRepository<User, Integer>, Serializable {

    User findByAgentCodeAndStatus(String agentCode, String status);

}
