package com.moviired.model.entities.cashservice;

import com.moviired.model.enums.CashOutStatus;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

import static com.moviired.helper.Constant.*;

@Data
@Entity
@Table(name = "cash_out")
public class CashOut implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", length = FIFTEEN, nullable = false, unique = true)
    private Integer id;

    @Column(name = "correlation_id_initialized", length = THIRTY, nullable = false, unique = true)
    private String correlationId;

    @Column(name = "correlation_id_complete", length = THIRTY, nullable = false, unique = true)
    private String correlationIdComplete;

    @Column(name = "txn_id", length = THIRTY)
    private String txnId;

    @Column(name = "txn_hold", length = THIRTY)
    private String txnHold;

    @Column(name = "state", length = THIRTY, nullable = false)
    @Enumerated(EnumType.STRING)
    private CashOutStatus stateCashOut = CashOutStatus.INITIALIZED;

    @Column(name = "processed")
    private boolean processedCashOut = Boolean.FALSE;

    @Column(name = "phone_number", length = THIRTY, nullable = false)
    private String phoneNumberCashOut;

    @Column(name = "agent_code", length = TEN, nullable = false)
    private String agentCodeCashOut;

    @Column(name = "amount", nullable = false)
    private Integer amountCashOut;

    @Column(name = "transaction_cost", nullable = false)
    private Integer transactionCost;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "creation_date", nullable = false)
    private Date creationDateCashOut = new Date();

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "expiration_date")
    private Date expirationDateCashOut;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "issuer_date")
    private Date issuerDateCashOut;

    @Column(name = "issuer_cred")
    private String issuerCredCashOut;

    @Column(name = "target_app", length = HUNDRED, nullable = false)
    private String targetAppCashOut;

    @Column(name = "giro_id")
    private Integer giroId;

    @Column(name = "taken")
    private boolean takenCashOut = Boolean.FALSE;

    @Column(name = "signature")
    private String signatureCashOut = "";

    @Column(name = "otp")
    private String otp;

    @Column(name = "identification_number", length = TWENTY)
    private String identificationNumber;

    @Column(name = "transaction_cost_process", nullable = false)
    private boolean transactionCostProcess = Boolean.TRUE;

}

