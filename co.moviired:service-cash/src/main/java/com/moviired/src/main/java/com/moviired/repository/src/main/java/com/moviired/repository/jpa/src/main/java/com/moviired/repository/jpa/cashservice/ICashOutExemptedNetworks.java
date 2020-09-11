package com.moviired.repository.jpa.cashservice;

import com.moviired.model.entities.cashservice.CashOutExemptedNetworks;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.Optional;

@Repository
public interface ICashOutExemptedNetworks extends Serializable, CrudRepository<CashOutExemptedNetworks, Integer> {

    Optional<CashOutExemptedNetworks> findByOriginNetworkAndTargetNetwork(String originNetwork,String targetNetwork);
}

