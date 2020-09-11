package co.moviired.supportaudit.domain.repository;


import co.moviired.supportaudit.domain.entity.Audit;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

import java.io.Serializable;
import java.util.Date;

public interface IAuditRepository extends ReactiveMongoRepository<Audit, String>, Serializable {

    Flux<Audit> findByOperationAndUsernameContainsAndDateBetween(String operation, String username, Date startDate, Date endDate);

    Flux<Audit> findByUsernameContainsAndDateBetween(String username, Date startDate, Date endDate);

    Flux<Audit> findByOperationContainsAndDateBetween(String operation, Date startDate, Date endDate);

    Flux<Audit> findByDateBetween(Date startDate, Date endDate);


}

