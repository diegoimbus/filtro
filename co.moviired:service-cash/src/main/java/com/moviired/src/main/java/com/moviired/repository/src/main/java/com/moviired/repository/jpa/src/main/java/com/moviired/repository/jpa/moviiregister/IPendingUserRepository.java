package com.moviired.repository.jpa.moviiregister;

import com.moviired.model.entities.moviiregister.PendingUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.Optional;

@Repository
public interface IPendingUserRepository extends Serializable, CrudRepository<PendingUser, Integer> {

    Optional<PendingUser> findByDocumentNumberAndProcessTypeAndStatusAndAltered(String documentNumber, PendingUser.ProcessType processType, boolean status, boolean altered);
}

