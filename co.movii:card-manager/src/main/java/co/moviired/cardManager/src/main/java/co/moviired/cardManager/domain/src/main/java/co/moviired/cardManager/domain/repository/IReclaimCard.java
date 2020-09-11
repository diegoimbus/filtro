package co.moviired.cardManager.domain.repository;


import co.moviired.cardManager.domain.entity.ReclaimCard;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.io.Serializable;

import java.util.Date;
import java.util.List;

@Repository
public interface IReclaimCard extends CrudRepository<ReclaimCard, Integer>, Serializable {

    List<ReclaimCard> findByDocumentNumberAndDocumentTypeOrderByRequestDateDesc(String documentNumber, String documentType);

    @Query("SELECT x FROM reclaim_card x ORDER BY x.deliveryDate DESC")
    List<ReclaimCard> listAll();

    List<ReclaimCard> findByDocumentNumberAndDocumentTypeAndPhoneNumberOrderByRequestDateDesc(String documentNumber, String documentType, String phoneNumber);

    /*@Modifying
    @Query("update ReclaimCard u set u.cardDelivered = ?1, set u.deliveryDate = ?2 where u.id = ?3")
    void updateCardDelivery(Boolean cardDelivered, Date deliveryDate, Integer id);*/

}

