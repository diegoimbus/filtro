package co.moviired.acquisition.repository;

import co.moviired.acquisition.model.entity.ProductCode;
import co.moviired.acquisition.model.entity.Transaction;
import co.moviired.acquisition.model.enums.TransactionState;
import co.moviired.acquisition.model.enums.TransactionType;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends Serializable, CrudRepository<Transaction, Integer> {

    // find pending transactions
    List<Transaction> findByProductCodeAndStateAndIdNot(ProductCode productCode, TransactionState transactionState, String idCurrentTransaction);

    Optional<Transaction> findFirstByProductCodeAndStateAndTransactionTypeNotInOrderByDateTransactionDesc(ProductCode productCode, TransactionState state, List<TransactionType> noTypes);
}

