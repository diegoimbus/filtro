package com.moviired.repository.jpa.cashservice;

import com.moviired.model.entities.cashservice.CashIn;
import org.springframework.data.repository.CrudRepository;

import java.io.Serializable;
import java.util.List;

public interface ICashInRepository extends Serializable, CrudRepository<CashIn, Integer> {

    List<CashIn> findByCorrelationIdAndAgentCode(String correlationId, String agentCode);
}

