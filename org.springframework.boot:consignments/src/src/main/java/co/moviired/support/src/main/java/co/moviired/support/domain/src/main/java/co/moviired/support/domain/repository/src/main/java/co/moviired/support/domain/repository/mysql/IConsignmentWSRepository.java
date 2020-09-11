package co.moviired.support.domain.repository.mysql;

import co.moviired.support.domain.entity.mysql.ConsignmentWS;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.List;

@Repository
public interface IConsignmentWSRepository extends CrudRepository<ConsignmentWS, Integer>, Serializable {

    @Query("SELECT cons from ConsignmentWS cons WHERE concat(cons.accountNumber, " +
            "cons.accountantDate, " +
            "cons.bankNitOne, " +
            "cons.transactionChannel, " +
            "cons.eanCode, " +
            "cons.billTotalAmount, " +
            "cons.billNumber, " +
            "cons.uuid, " +
            "cons.statusCode) = :concatenated ")
    List<ConsignmentWS> findByUnique(@Param("concatenated") String concatenated
    );

    @Query("SELECT cons from ConsignmentWS cons WHERE concat(cons.billNumber, " +
            "cons.accountantDate, " +
            "cons.bankNitOne, " +
            "cons.uuid, " +
            "cons.billTotalAmount, " +
            "cons.billingCompanyAgreementOne, " +
            "cons.referenceFieldTwo, " +
            "cons.referenceFieldThree, " +
            "cons.referenceFieldFour, " +
            "cons.referenceFieldFive, " +
            "cons.statusCode) = :concatenated ")
    List<ConsignmentWS> findByUniqueBancolombia(@Param("concatenated") String concatenated
    );

    @Query("SELECT cons from ConsignmentWS cons WHERE cons.id = :id")
    ConsignmentWS findId(@Param("id") Integer id);

    @Query("SELECT cons from ConsignmentWS cons WHERE concat(cons.accountNumber, " +
            "cons.accountantDate, " +
            "cons.bankNitOne, " +
            "cons.transactionChannel, " +
            "cons.billTotalAmount, " +
            "cons.billNumber, " +
            "cons.uuid, " +
            "cons.statusCode) = :concatenated ")
    ConsignmentWS findByRevert(@Param("concatenated") String concatenated
    );

    @Query("SELECT cons from ConsignmentWS cons WHERE concat(cons.billNumber, " +
            "cons.accountantDate, " +
            "cons.bankNitOne, " +
            "cons.uuid, " +
            "cons.billTotalAmount, " +
            "cons.billingCompanyAgreementOne, " +
            "cons.referenceFieldTwo, " +
            "cons.referenceFieldThree, " +
            "cons.referenceFieldFour, " +
            "cons.referenceFieldFive, " +
            "cons.statusCode) = :concatenated ")
    ConsignmentWS findByRevertBancolombia(@Param("concatenated") String concatenated);

}

