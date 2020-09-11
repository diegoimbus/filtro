package co.moviired.mahindrafacade.domain.repository.mahindrafacade;

import co.moviired.mahindrafacade.domain.entity.mahindrafacade.User;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

import java.io.Serializable;

public interface IUserRepository extends ReactiveMongoRepository<User, String>, Serializable {

    Mono<User> findByMsisdn(String msisdn);

}

