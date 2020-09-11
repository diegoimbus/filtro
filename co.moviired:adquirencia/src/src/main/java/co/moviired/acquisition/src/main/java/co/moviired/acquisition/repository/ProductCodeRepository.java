package co.moviired.acquisition.repository;

import co.moviired.acquisition.model.LotIdentifier;
import co.moviired.acquisition.model.entity.Product;
import co.moviired.acquisition.model.entity.ProductCode;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductCodeRepository extends Serializable, CrudRepository<ProductCode, Integer> {

    Optional<ProductCode> findFirstByPinHashAndProduct(String pin, Product product);

    Optional<ProductCode> findFirstByCardCodeAndProduct(String cardCode, Product product);

    List<ProductCode> findByLotIdentifierIn(List<String> lotIdentifier);

    @Query(value = "SELECT pc.lot_identifier as lotIdentifier, pc.creation_date as creationDate, p.identifier as productIdentifier, " +
            "count(*) as codesCount FROM `product_code` pc INNER JOIN product p on pc.product_id = p.id " +
            "group by pc.lot_identifier, pc.creation_date, pc.product_id order by pc.creation_date desc",
            nativeQuery = true)
    List<LotIdentifier> getLotsIdentifiers();
}

