package co.moviired.business.domain.jpa.movii.repository;

import co.moviired.business.domain.enums.Seller;
import co.moviired.business.domain.jpa.movii.entity.BillerCategory;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IBillerCategoriesRepository extends CrudRepository<BillerCategory, Integer> {

    @Query("SELECT bc FROM BillerCategory bc LEFT JOIN bc.listSellerCategory sc WHERE sc.seller IS NULL OR sc.seller = ?1 ORDER BY bc.name ASC")
    List<BillerCategory> getAllCategories(Seller seller);

}

