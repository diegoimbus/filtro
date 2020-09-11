package com.moviired.repository.jpa.cashservice;

import com.moviired.model.entities.cashservice.CashOut;
import com.moviired.model.enums.CashOutStatus;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
@Transactional
public interface ICashOutRepository extends Serializable, CrudRepository<CashOut, Integer> {


    List<CashOut> findByStateCashOut(CashOutStatus state);

    Integer countByPhoneNumberCashOutAndStateCashOut(String phoneNumber, CashOutStatus state);


    List<CashOut> findByPhoneNumberCashOutAndStateCashOut(String phoneNumber, CashOutStatus state);

    List<CashOut> findByAgentCodeCashOutAndStateCashOutAndPhoneNumberCashOut(@Param("agentCode") String agentCode,
                                                                             @Param("state") CashOutStatus state,
                                                                             @Param("phoneNumber") String phoneNumber);

    @Query("from CashOut c " +
            "where (c.stateCashOut = com.moviired.model.enums.CashOutStatus.INITIALIZED and :now > c.expirationDateCashOut and c.takenCashOut = false and c.processedCashOut = false) " +
            "or (c.stateCashOut = com.moviired.model.enums.CashOutStatus.DECLINED and c.takenCashOut = false and c.processedCashOut = false) " +
            "or (c.stateCashOut = com.moviired.model.enums.CashOutStatus.REVERSED and c.takenCashOut = false and c.processedCashOut = false)" +
            "or (c.stateCashOut = com.moviired.model.enums.CashOutStatus.REVERSED_TRANSACTION_COST and c.takenCashOut = false and c.processedCashOut = false)")
    Set<CashOut> findExpired(@Param("now") Date now);

    @Modifying
    @Query("UPDATE CashOut SET takenCashOut = :taken WHERE id in (:cashOuts)")
    void updateAll(Set<Integer> cashOuts, boolean taken);

    @Query("from CashOut c where c.stateCashOut = com.moviired.model.enums.CashOutStatus.COMPLETED and c.processedCashOut = false and c.takenCashOut = false")
    Set<CashOut> findCompletedPending();

    CashOut findByOtpAndIdentificationNumber(String otp, String identificationNumber);

    CashOut findByOtpAndPhoneNumberCashOut(String otp, String phoneNumber);

    CashOut findByAgentCodeCashOutAndStateCashOutAndOtpAndPhoneNumberCashOut(String agentCode, CashOutStatus state, String otp, String phoneNumber);

    List<CashOut> findByCorrelationIdOrCorrelationIdCompleteAndAgentCodeCashOut(String correlationIdInitialized, String correlationIdComplete, String agentCode);

    Optional<CashOut> findByGiroIdAndStateCashOutAndTakenCashOutAndProcessedCashOut(Integer giro, CashOutStatus state, boolean taken, boolean processed);

}

