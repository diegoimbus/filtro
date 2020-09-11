package co.moviired.transpiler.jpa.movii.repository;

import co.moviired.transpiler.jpa.movii.domain.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.Optional;

@Repository
public interface IUserRepository extends CrudRepository<User, Integer>, Serializable {

    Optional<User> findByGetraxUsername(String getraxUsername);

}

