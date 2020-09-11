package co.moviired.support.repository;

import co.moviired.support.domain.dto.enums.Status;
import co.moviired.support.domain.entity.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public interface IUserRepository extends Serializable, CrudRepository<User, Integer> {

    //Login
    Optional<User> findFirstByMsisdnAndMpin(String msisdn, String password);

    Optional<User> findFirstByMsisdn(String msisdn);

    List<User> findAllByStatus(Status status);

    List<User> findAllByUserTypeAndStatus(String userType, Status status);
}

