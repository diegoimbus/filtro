package co.moviired.digitalcontent.business.domain.repository;

import co.moviired.digitalcontent.business.domain.entity.HomologationIncomm;
import org.springframework.data.repository.CrudRepository;

import java.io.Serializable;

public interface IHomologationIncommRepository extends CrudRepository<HomologationIncomm, Integer>, Serializable {

    HomologationIncomm findByNetwork(String network);
}

