package co.moviired.digitalcontent.business.domain.repository;


import co.moviired.digitalcontent.business.domain.entity.Category;
import co.moviired.digitalcontent.business.domain.entity.Product;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.List;

@Repository
public interface IProductRepository extends CrudRepository<Product, Integer>, Serializable {

    Product getProductById(Integer id);

    List<Product> getProductByCategory(Category category);

    List<Product> getProductByCategoryAndTsubType(Category category, Integer tsubType);

    Product findTopByEanCodeStartingWith(String eanCode);

}

