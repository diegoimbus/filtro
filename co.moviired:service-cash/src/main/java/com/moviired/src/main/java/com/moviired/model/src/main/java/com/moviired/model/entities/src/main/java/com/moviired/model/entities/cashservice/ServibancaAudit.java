package com.moviired.model.entities.cashservice;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Table(name = "servibanca_audit")
public class ServibancaAudit implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private Integer id;

    @Column(name = "start_date")
    private Date startDate;

    @Column(name = "correlation_id")
    private String correlationId;

    @Column(name = "request")
    private String request;

    @Column(name = "response")
    private String response;

    @Column(name = "transaction_state")
    private String transactionState;

    @Column(name = "transaction_type")
    private String transactionType;

    @Column(name = "conciliation")
    private String conciliation;

    @Column(name = "description")
    private String description;


}

