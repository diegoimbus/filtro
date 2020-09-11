package co.moviired.digitalcontent.business.domain.repository;

import co.moviired.digitalcontent.business.domain.entity.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.Optional;

@Repository
public interface IUserRepository extends CrudRepository<User, Integer>, Serializable {

    Optional<User> findFirstByGetraxUsernameAndGetraxPassword(String getraxUsername, String getraxPassword);

    Optional<User> findFirstByMahindraUsernameAndMahindraPassword(String mahindraUsername, String mahindraPassword);

    @Query(value = "SELECT u FROM User u " +
            "WHERE (u.getraxUsername = :username AND u.getraxPassword = :password) " +
            "OR (u.mahindraUsername = :username AND u.mahindraPassword = :password)")
    Optional<User> findFirstByUsernameAndPassword(String username, String password);

}

