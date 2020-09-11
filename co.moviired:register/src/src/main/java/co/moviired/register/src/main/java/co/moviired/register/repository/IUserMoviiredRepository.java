package co.moviired.register.repository;

import co.moviired.register.domain.enums.register.Status;
import co.moviired.register.domain.model.entity.UserMoviired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public interface IUserMoviiredRepository extends Serializable, CrudRepository<UserMoviired, Integer> {

    Optional<UserMoviired> findByPhoneNumberAndStatus(String phoneNumber, Status status);

    List<UserMoviired> findAllByStatusAndTaken(Status status, boolean taken, Pageable pageable);

    @Modifying(flushAutomatically = true)
    @Query("update UserMoviired t set t.taken = :taken where t in :iterable")
    int saveAllAsTaken(@Param("taken") boolean taken, @Param("iterable") Iterable<UserMoviired> iterable);

}

