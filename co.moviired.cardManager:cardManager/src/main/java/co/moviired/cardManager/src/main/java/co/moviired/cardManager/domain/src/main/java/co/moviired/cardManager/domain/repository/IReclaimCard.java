package co.moviired.cardManager.domain.repository;


import co.moviired.cardManager.domain.entity.ReclaimCard;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

@Repository
public interface IReclaimCard extends CrudRepository<ReclaimCard, Integer>, Serializable {

    List<ReclaimCard> findByDocumentNumberAndDocumentTypeOrderByRequestDateDesc(String documentNumber, String documentType);

    //Page<ReclaimCard> findByPointNameOrderByRequestDateDesc(String pointName, Pageable pageable);

    //List<ReclaimCard> findByPointNameAndCityOrderByRequestDateDesc(String documentNumber, String documentType);

    //List<ReclaimCard> findByPointNameAndCityAndAddressOrderByRequestDateDesc(String documentNumber, String documentType);

}

