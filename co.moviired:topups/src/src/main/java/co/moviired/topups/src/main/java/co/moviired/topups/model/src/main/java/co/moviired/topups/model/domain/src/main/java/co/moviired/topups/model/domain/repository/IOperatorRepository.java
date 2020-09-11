package co.moviired.topups.model.domain.repository;

import co.moviired.topups.model.domain.Operator;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.io.Serializable;
import java.util.List;

public interface IOperatorRepository extends PagingAndSortingRepository<Operator, Integer>, Serializable {

    List<Operator> findAllByStatusIn(List<Integer> status, Pageable pageable);

    List<Operator> findAllByStatusView(int statusView, Pageable pageable);


    List<Operator> findAllByOperatorIdAndStatusIn(int operatorId, List<Integer> status, Pageable pageable);

    List<Operator> findAllByTypeAndStatusIn(int type, List<Integer> status, Pageable pageable);

    List<Operator> findAllByTypeAndStatusViewAndStatusIn(int type, int statusView,  List<Integer> status, Pageable pageable);

    Operator findByEanCodeAndProductCode(String eanCode, String productCode);

    Operator findByEanCode(String eanCode);

    Operator findByProductCodeAndType(String productCode, int type);
}

