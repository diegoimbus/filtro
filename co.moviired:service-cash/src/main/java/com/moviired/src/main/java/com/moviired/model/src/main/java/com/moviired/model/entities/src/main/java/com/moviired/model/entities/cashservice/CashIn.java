package com.moviired.model.entities.cashservice;

import com.moviired.model.enums.CashOutStatus;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

import static com.moviired.helper.Constant.*;

@Data
@Entity
@Table(name = "cash_in")
public class CashIn {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", length = FIFTEEN, nullable = false)
    private Integer id;

    @Column(name = "correlation_id", length = THIRTY, nullable = false)
    private String correlationId;

    @Column(name = "txn_id", length = THIRTY)
    private String txnId;

    @Column(name = "txn_hold", length = THIRTY)
    private String txnHold;

    @Column(name = "state", length = THIRTY, nullable = false)
    @Enumerated(EnumType.STRING)
    private CashOutStatus state = CashOutStatus.INITIALIZED;

    @Column(name = "processed")
    private boolean processed = Boolean.FALSE;

    @Column(name = "phone_number", length = THIRTY, nullable = false)
    private String phoneNumber;

    @Column(name = "agent_code", length = TEN, nullable = false)
    private String agentCode;

    @Column(name = "amount", nullable = false)
    private Integer amount;

    @Column(name = "transaction_cost", nullable = false)
    private Integer transactionCost;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "creation_date", nullable = false)
    private Date creationDate = new Date();

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "expiration_date")
    private Date expirationDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "issuer_date")
    private Date issuerDate;

    @Column(name = "issuer_cred")
    private String issuerCred;

    @Column(name = "target_app", length = HUNDRED, nullable = false)
    private String targetApp;

    @Column(name = "taken")
    private boolean taken = Boolean.FALSE;

    @Column(name = "signature")
    private String signature = "";
}

