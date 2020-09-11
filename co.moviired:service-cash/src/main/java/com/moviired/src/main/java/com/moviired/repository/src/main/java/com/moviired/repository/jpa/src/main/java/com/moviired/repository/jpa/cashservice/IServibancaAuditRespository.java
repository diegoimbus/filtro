package com.moviired.repository.jpa.cashservice;

import com.moviired.model.entities.cashservice.ServibancaAudit;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.io.Serializable;

@Repository
public interface IServibancaAuditRespository extends Serializable, CrudRepository<ServibancaAudit, Integer> {
}

