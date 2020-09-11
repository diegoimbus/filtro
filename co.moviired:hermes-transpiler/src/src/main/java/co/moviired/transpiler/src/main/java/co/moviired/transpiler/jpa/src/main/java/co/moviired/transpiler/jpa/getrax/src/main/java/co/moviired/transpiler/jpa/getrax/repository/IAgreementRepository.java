package co.moviired.transpiler.jpa.getrax.repository;

import co.moviired.transpiler.jpa.getrax.domain.Agreement;
import co.moviired.transpiler.jpa.movii.domain.enums.GeneralStatus;
import org.springframework.data.repository.CrudRepository;

import java.io.Serializable;
import java.util.List;

public interface IAgreementRepository extends CrudRepository<Agreement, Integer>, Serializable {

    List<Agreement> findAgreementsByIdAndStatus(Integer id, GeneralStatus status);

    List<Agreement> findAgreementsByCodeAndStatus(String code, GeneralStatus status);

}

