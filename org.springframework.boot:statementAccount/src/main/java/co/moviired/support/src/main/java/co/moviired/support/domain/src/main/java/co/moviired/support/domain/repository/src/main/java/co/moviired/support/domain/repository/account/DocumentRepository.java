package co.moviired.support.domain.repository.account;

import co.moviired.support.domain.entity.account.Document;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.Optional;

@Repository
public interface DocumentRepository extends Serializable, CrudRepository<Document, Integer> {

    Optional<Document> findByToken(String token);
}

