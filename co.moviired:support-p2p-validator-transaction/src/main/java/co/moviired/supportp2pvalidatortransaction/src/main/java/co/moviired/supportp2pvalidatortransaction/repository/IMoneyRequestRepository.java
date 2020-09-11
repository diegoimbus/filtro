package co.moviired.supportp2pvalidatortransaction.repository;

import co.moviired.supportp2pvalidatortransaction.model.entity.MoneyRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface IMoneyRequestRepository extends CrudRepository<MoneyRequest, Integer> {

    @Query("SELECT mr FROM MoneyRequest mr WHERE TIMESTAMPDIFF(HOUR,mr.registrationDate, now()) >= 24 AND mr.state NOT IN (?1,?2)")
    List<MoneyRequest> getFailsMoneyRequest(String state1, String status3, Pageable pageable);

    @Query("SELECT mr FROM MoneyRequest mr WHERE mr.state NOT IN (?1,?2, ?3) AND mr.statusRetries IS NULL " +
            "AND TIMESTAMPDIFF(MINUTE,mr.registrationDate,now()) > 5 AND TIMESTAMPDIFF(HOUR,mr.registrationDate, now()) < 24" +
            "AND mr.stateInitJob = 'Fail'")
    List<MoneyRequest> getFailsMoneyRequest(String state1, String state2, String status3, Pageable pageable);
}




