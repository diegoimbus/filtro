package co.moviired.transpiler.jpa.getrax.repository;

import co.moviired.transpiler.jpa.getrax.domain.ProductGetrax;
import co.moviired.transpiler.jpa.movii.domain.enums.GeneralStatus;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.List;

@Repository
public interface IProductGetraxRepository extends CrudRepository<ProductGetrax, Integer>, Serializable {

    List<ProductGetrax> findProductGetraxesByIdAndStatus(Integer id, GeneralStatus status);

    List<ProductGetrax> findProductGetraxesByCodeAndStatus(String code, GeneralStatus status);

}

