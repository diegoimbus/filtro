package co.moviired.acquisition.repository;

import co.moviired.acquisition.model.entity.Product;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.Optional;

@Repository
public interface ProductRepository extends Serializable, CrudRepository<Product, Integer> {

    Optional<Product> findByIdentifier(String identifier);

    Optional<Product> findByProductCode(String productCode);
}

