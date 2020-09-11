package co.moviired.supportp2pvalidatortransaction.model.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "frozen_balance")
public class FrozenBalance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", length = 15, nullable = false)
    private Integer id;

    @Column(name = "msisdn", length = 30, nullable = false)
    private String msisdn;

    @Column(name = "msisdn2", length = 30, nullable = false)
    private String msisdn2;

    @Column(name = "amount", length = 30, nullable = false)
    private Double amount;

    @Column(name = "txid", length = 200, nullable = false)
    private String txId;

    @Column(name = "ftxid", length = 200, nullable = false)
    private String ftxId;

    @Column(name = "name", length = 200, nullable = false)
    private String name;

    @Column(name = "transactionCode", length = 200)
    private String transactionCode;

    @Lob
    @Column(name = "reques_hmoney", nullable = false)
    private String requestHMoney;

    @Lob
    @Column(name = "response_hmoney", nullable = false)
    private String responseHMoney;

    @Column(name = "time_response_hmoney", nullable = false)
    private Long timeResponseHMoney;

    @Lob
    @Column(name = "request_uhmoney")
    private String requestUHMoney;

    @Lob
    @Column(name = "response_uhmoney")
    private String responseUHMoney;

    @Column(name = "time_response_uhmoney")
    private Long timeResponseUHMoney;

    @Column(name = "state", length = 100, nullable = false)
    private String state;

    @Column(name = "stateInitJob", length = 100)
    private String stateInitJob;

    @Lob
    @Column(name = "messageText1")
    private String messageText1;

    @Column(length = 100, nullable = false)
    private String txidHmoney;

    @Column(length = 100)
    private String txidUHmoney;

    @Column(length = 100)
    private String statusSms;

    @Column(length = 100)
    private String processType;

    @Column(length = 100)
    private String typeSms;

    @Lob
    @Column(name = "description")
    private String description;

    @Lob
    @Column(name = "message")
    private String message;

    @Column(length = 100)
    private Integer retries;

    @Column(length = 100)
    private String statusRetries;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "registration_date", nullable = false)
    private Date registrationDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_update")
    private Date dateUpdate;

}

