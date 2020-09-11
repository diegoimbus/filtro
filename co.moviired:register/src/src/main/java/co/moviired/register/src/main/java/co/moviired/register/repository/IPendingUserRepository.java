package co.moviired.register.repository;

import co.moviired.register.domain.model.entity.PendingUser;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public interface IPendingUserRepository extends Serializable, CrudRepository<PendingUser, Integer> {

    @Query(value = "SELECT u FROM PendingUser u where u.phoneNumber = ?1 and u.type = ?2 and u.status = ?3 " +
            "and (u.processType is null or u.processType = ?4) order by u.registrationDate desc")
    List<PendingUser> getRegistrationPendingUsers(String phoneNumber, String type, boolean status, PendingUser.ProcessType processType);

    @Query(value = "SELECT u FROM PendingUser u where u.documentType = ?1 and u.documentNumber = ?2 and u.status = ?3 " +
            "and u.processType = ?4 and u.altered = ?5 order by u.registrationDate desc")
    List<PendingUser> getSubsidyPendingRegistration(String documentType, long documentNumber, boolean status, PendingUser.ProcessType processType, boolean isAltered);

    @Query(value = "SELECT u FROM PendingUser u where u.documentType = ?1 and u.documentNumber = ?2 " +
            "and u.processType = ?3 and u.altered = ?4 order by u.registrationDate desc")
    List<PendingUser> getSubsidyPendingRegistrationWithOutStatus(String documentType, long documentNumber, PendingUser.ProcessType processType, boolean isAltered);

    @Query(value = "SELECT u FROM PendingUser u where u.documentType = ?1 and u.documentNumber = ?2 and u.status = ?3 " +
            "and u.processType = ?4 and u.subsidyCode = ?5 and u.altered = ?6 order by u.registrationDate desc")
    List<PendingUser> getSubsidyPendingRegistrationWithCode(String documentType, long documentNumber, boolean status, PendingUser.ProcessType processType, String code, boolean isAltered);

    Optional<PendingUser> findFirstByDocumentTypeAndDocumentNumberAndProcessType(String documentType, long documentNumber, PendingUser.ProcessType processType);

    @Query(value = "SELECT u FROM PendingUser u where u.status = ?1 and u.processType = ?2 and u.infoPersonIsComplete = ?3 and u.altered = ?4 " +
            "and (u.taken is null or u.taken < ?5) order by u.registrationDate desc")
    List<PendingUser> getSubsidyPendingForFillData(boolean status, PendingUser.ProcessType processType, boolean isCompletePersonInfo, boolean isAltered, String timeOut, Pageable pageable);
}

