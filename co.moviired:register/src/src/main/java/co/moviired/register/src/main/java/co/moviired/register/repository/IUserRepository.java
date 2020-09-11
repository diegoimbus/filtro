package co.moviired.register.repository;

import co.moviired.register.domain.model.entity.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public interface IUserRepository extends Serializable, CrudRepository<User, Integer> {

    Optional<User> findFirstByPhoneNumberAndPhoneSerialNumberAndIsActiveAndStatusInAndProcessOrderByRegistrationDate(String phone, String phoneSerialNumber, Integer isActive, List<Integer> status, Integer process);

    Optional<List<User>> findByStatusAndIsActiveAndProcessIn(Integer status, Integer isActive, List<Integer> processList);

    Optional<User> findByPhoneNumberAndPhoneSerialNumberAndStatusInAndIsActiveAndProcess(String phone, String serial, List<Integer> status, Integer isActive, Integer process);

    @Query("SELECT u FROM User u WHERE u.isActive = ?1 AND u.registrationDate < ?2 AND u.process = ?3")
    List<User> getCountDefeatOldRegisters(int activeStatus, Date minDateActiveRegisters, Integer process);

    @Modifying
    @Query("UPDATE User u SET u.isActive = ?2 WHERE u.isActive = ?1 AND u.registrationDate < ?3 AND u.process = ?3")
    void defeatOldRegisters(int activeStatus, int defeatState, Date minDateActiveRegisters, Integer process);
}

