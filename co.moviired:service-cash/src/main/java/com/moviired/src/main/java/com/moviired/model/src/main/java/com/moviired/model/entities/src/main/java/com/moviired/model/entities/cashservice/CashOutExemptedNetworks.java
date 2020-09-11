package com.moviired.model.entities.cashservice;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "cash_out_exempted_networks")
public class CashOutExemptedNetworks implements Serializable {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private Integer id;

    @Column(name = "origin_network")
    private String originNetwork;

    @Column(name = "target_network")
    private String targetNetwork;

}

