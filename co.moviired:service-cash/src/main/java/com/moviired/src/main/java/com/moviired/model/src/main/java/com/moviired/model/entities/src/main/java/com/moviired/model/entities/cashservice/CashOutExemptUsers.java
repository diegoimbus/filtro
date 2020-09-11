package com.moviired.model.entities.cashservice;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "cash_out_exempt_users")
public class CashOutExemptUsers implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private Integer id;

    @Column(name = "user_network")
    private String userNetwork;

    @Column(name = "phone_number")
    private String phoneNumber;


}

