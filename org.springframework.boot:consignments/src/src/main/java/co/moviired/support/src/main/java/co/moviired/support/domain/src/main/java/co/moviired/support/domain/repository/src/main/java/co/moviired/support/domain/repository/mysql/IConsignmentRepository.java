package co.moviired.support.domain.repository.mysql;

import co.moviired.support.domain.entity.mysql.Consignment;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Copyright @2019 MOVIIRED. Todos los derechos reservados.
 *
 * @author Rodolfo.rivas
 * @category Consinments
 */

public interface IConsignmentRepository extends CrudRepository<Consignment, Integer>, Serializable {

    List<Consignment> findByRegistryDateBetweenOrderByRegistryDateDesc(Date dateInit, Date dateEnd);

    List<Consignment> findByRegistryDateBetweenAndMsisdnOrderByRegistryDateDesc(Date dateInit, Date dateEnd, String msisdn);

    List<Consignment> findByCorrelationId(String correlationId);

    boolean existsByCorrelationId(String correlationId);

    boolean existsByPaymentReferenceAndBankId(String paymentReference, String bankId);

    boolean existsByPaymentReference(String paymentReference);

    boolean existsByPaymentReferenceAndStatus(String paymentReference, byte status);

    List<Consignment> findByStatus(byte status);

    boolean existsByCorrelationIdAndStatus(String correlationId, byte status);

    @Query("SELECT cons.voucher from Consignment cons WHERE cons.id = :id")
    byte[] findImagePathById(@Param("id") int id);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("UPDATE Consignment cons set cons.status = :status, cons.reason = :reason, cons.processorUser = :processorUser, cons.type = :type, cons.usernamePortalAuthorizer = :username, cons.authorizerDate = :authorizerDate WHERE cons.correlationId = :correlationId")
    int updateStatusAndReasonAndProccesorUserAndType(@Param("status") byte status,
                                                     @Param("reason") String reason,
                                                     @Param("correlationId") String correlationId,
                                                     @Param("processorUser") String processorUser,
                                                     @Param("type") byte type,
                                                     @Param("username") String username,
                                                     @Param("authorizerDate") Date authorizerDate);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("UPDATE Consignment cons set cons.status = :status WHERE cons.correlationId = :correlationId")
    int updateStatus(@Param("status") byte status,
                     @Param("correlationId") String correlationId);
}

