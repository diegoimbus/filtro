package co.moviired.register.repository;

import co.moviired.register.domain.model.entity.UserPendingUpdate;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;

@Repository
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public interface IUserPendingUpdateRepository extends Serializable, CrudRepository<UserPendingUpdate, Integer> {

    UserPendingUpdate findByIdno(String idno);

    UserPendingUpdate findByPhoneNumber(String phoneNumber);

}

