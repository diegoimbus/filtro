package co.moviired.business.domain.jpa.movii.repository;

import co.moviired.business.domain.enums.CollectionType;
import co.moviired.business.domain.enums.Seller;
import co.moviired.business.domain.jpa.movii.entity.Biller;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.List;

@Repository
public interface IBillerRepository extends CrudRepository<Biller, Integer>, Serializable {

    Biller getByEanCode(String eanCode);

    Biller getByBillerCode(String billerCode);

    @Query("SELECT b FROM Biller b LEFT JOIN b.listSeller s WHERE b.categoryId = ?1 AND b.stateView = ?2 AND b.collectionType <> ?3 AND (s.seller IS NULL OR s.seller = ?4) ORDER BY b.name ASC")
    List<Biller> getAgreementsByCategory(Integer category, Boolean stateView, CollectionType type, Seller seller);

    @Query("SELECT b FROM Biller b LEFT JOIN b.listSeller s WHERE (UPPER(b.name) LIKE UPPER(?1) OR UPPER(b.productDescription) LIKE UPPER(?1)) AND b.stateView = ?2 AND b.collectionType <> ?3 AND (s.seller IS NULL OR s.seller = ?4) ORDER BY b.name ASC")
    List<Biller> getAgreementsByFilterText(String textFilter, Boolean stateView, CollectionType type, Seller seller);

}
