package co.moviired.moneytransfer.domain.repository.redshift;


import co.moviired.moneytransfer.domain.entity.redshift.Header;
import org.springframework.data.repository.CrudRepository;

import java.io.Serializable;

public interface IHeaderRepository extends CrudRepository<Header, String>, Serializable {

}

