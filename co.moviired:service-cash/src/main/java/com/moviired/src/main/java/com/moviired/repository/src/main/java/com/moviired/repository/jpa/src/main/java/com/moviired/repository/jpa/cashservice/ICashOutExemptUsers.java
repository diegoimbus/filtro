package com.moviired.repository.jpa.cashservice;

import com.moviired.model.entities.cashservice.CashOutExemptUsers;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.Optional;

@Repository
public interface ICashOutExemptUsers extends Serializable, CrudRepository<CashOutExemptUsers, Integer> {

    Optional<CashOutExemptUsers> findByPhoneNumber(String phoneNumber);
}

