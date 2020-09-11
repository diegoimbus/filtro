package co.moviired.cardManager.domain.repository;

import co.moviired.cardManager.domain.entity.ReclaimCard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IReclaimCardList extends PagingAndSortingRepository<ReclaimCard, Integer> {

    Page<ReclaimCard> findByPointNameOrderByRequestDateDesc(String pointName, Pageable pageable);

    Page<ReclaimCard> findByPointNameAndCityOrderByRequestDateDesc(String pointName, String city, Pageable pageable);

    Page<ReclaimCard> findByPointNameAndCityAndAddressOrderByRequestDateDesc(String pointName, String city, String address, Pageable pageable);

}

