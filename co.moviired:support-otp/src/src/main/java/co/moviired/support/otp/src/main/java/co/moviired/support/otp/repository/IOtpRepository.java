package co.moviired.support.otp.repository;

import co.moviired.support.otp.model.entity.Otp;
import co.moviired.support.otp.model.enums.OtpState;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Date;
import java.util.Optional;

@Repository
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public interface IOtpRepository extends CrudRepository<Otp, Integer>, Serializable {

    Optional<Otp> findTopByComponentAndPhoneNumberAndStateOrderByCreationDateDesc(String component, String phoneNumber, OtpState state);

    @Query("SELECT COUNT(o.id) FROM Otp o WHERE o.component = :component AND o.phoneNumber = :phoneNumber AND o.creationDate BETWEEN :startDate AND :endDate")
    int countByComponentAndPhoneNumberAndDate(String component, String phoneNumber, Date startDate, Date endDate);

    @Modifying(flushAutomatically = true)
    @Query("UPDATE Otp o SET o.state = 'EXPIRED', o.modificationDate = :expirationDate WHERE o.state = 'PENDING' AND o.expirationDate < :expirationDate")
    int expiredOtps(@Param("expirationDate") Date expirationDate);


}

