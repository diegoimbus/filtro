package co.moviired.support.domain.repository.redshift;

import co.moviired.support.domain.entity.redshift.ExtractData;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.io.Serializable;
import java.util.List;

public interface IExtractDataRepository extends CrudRepository<ExtractData, String>, Serializable {

    @Query(value = "SELECT new ExtractData(e.year, e.month) FROM ExtractData e " +
            "WHERE e.phoneNumber = ?1 GROUP BY e.month, e.year order by e.year DESC, e.month DESC")
    List<ExtractData> getAvailableExtracts(String phoneNumber);

    @Query(value = "SELECT new ExtractData(e.year, e.month) FROM ExtractData e " +
            "WHERE e.phoneNumber = ?1 AND e.year = ?2 AND e.month = ?3 GROUP BY e.month, e.year order by e.year DESC, e.month DESC")
    List<ExtractData> validateAvailableExtract(String phoneNumber, Integer year, Integer month);

    @Query(value = "SELECT new ExtractData(e.phoneNumber, e.productName, e.count, e.total, e.initialBalance, e.endBalance, e.year, e.month, e.storeName, e.userName, e.documentNumber) " +
            "FROM ExtractData e WHERE e.phoneNumber = ?1 AND e.year = ?2 AND e.month = ?3")
    List<ExtractData> getExtractData(String phoneNumber, Integer year, Integer month);
}

