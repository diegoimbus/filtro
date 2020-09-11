package co.moviired.supportp2pvalidatortransaction.repository;

import co.moviired.supportp2pvalidatortransaction.model.entity.FrozenBalance;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface IFrozenBalanceRepository extends CrudRepository<FrozenBalance, Integer> {

    @Query("SELECT fb FROM FrozenBalance fb WHERE TIMESTAMPDIFF(HOUR,fb.registrationDate,now()) >= 24 AND fb.state NOT IN (?1,?2)")
    List<FrozenBalance> getTransactionsWithTimeOut(String stateSuccess, String stateCancel, Pageable pageable);
}

