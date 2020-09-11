package co.moviired.transpiler.jpa.movii.repository;

import co.moviired.transpiler.jpa.movii.domain.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.List;

@Repository
public interface IProductRepository extends CrudRepository<Product, Integer>, Serializable {

    @Query("SELECT DISTINCT p FROM Product p WHERE p.eanCode LIKE CONCAT(:eanCode, '%')")
    List<Product> findByEan(@Param("eanCode") String eanCode, Pageable pageable);

    Product getProductById(Integer id);

}
