package co.moviired.support.repository;

import co.moviired.support.domain.entity.User;
import co.moviired.support.domain.entity.UserTmp;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Optional;

@Repository
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public interface IUserTmpRepository extends Serializable, CrudRepository<UserTmp, Integer> {

    //Login
    Optional<UserTmp> findByUser(User user);

    Optional<UserTmp> findFirstByMsisdn(String msisdn);

}

