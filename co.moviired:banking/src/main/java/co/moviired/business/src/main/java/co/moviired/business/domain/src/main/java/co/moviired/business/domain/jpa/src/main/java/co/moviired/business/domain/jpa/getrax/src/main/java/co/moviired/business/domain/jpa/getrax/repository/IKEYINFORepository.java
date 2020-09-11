package co.moviired.business.domain.jpa.getrax.repository;

import co.moviired.business.domain.jpa.getrax.entity.KEYINFO;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.Map;

@Repository
public interface IKEYINFORepository extends CrudRepository<KEYINFO, Integer>, Serializable {

    @Query(value = "SELECT k.KEIN_HEADER, k.KEIN_CRYPTOGRAM ,k.KEIN_MAC FROM KEYINFO k  JOIN KEYTYPE kt  ON(k.KEIN_KETY_ID = kt.KETY_ID ) WHERE kt.KETY_NAME = :name", nativeQuery = true)
    Map<String, Object> findAllByKetyName(String name);

}

