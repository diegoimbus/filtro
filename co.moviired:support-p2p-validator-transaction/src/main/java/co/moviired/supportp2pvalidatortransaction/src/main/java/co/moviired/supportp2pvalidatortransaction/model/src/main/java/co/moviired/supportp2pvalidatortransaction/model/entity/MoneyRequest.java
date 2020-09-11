package co.moviired.supportp2pvalidatortransaction.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "money_request")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MoneyRequest {

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

    @Column(name = "txid", length = 200)
    private String txId;

    @Column(name = "name", length = 200)
    private String name;

    @Column(name = "txidConfirmation", length = 200)
    private String txIdConfirmation;

    @Column(name = "transactionCode", length = 200, nullable = false)
    private String transactionCode;

    @Column(name = "transactionCodeConfirm", length = 200)
    private String transactionCodeConfirm;

    @Column(name = "stateInitJob", length = 100)
    private String stateInitJob;

    @Lob
    @Column(name = "message")
    private String message;

    @Lob
    @Column(name = "messageText1")
    private String messageText1;

    @Lob
    @Column(name = "messageText2")
    private String messageText2;

    @Lob
    @Column(name = "request_money")
    private String requestMoney;

    @Lob
    @Column(name = "response_money")
    private String responseMoney;

    @Lob
    @Column(name = "request_money_conformation")
    private String requestMoneyConfirmation;

    @Lob
    @Column(name = "response_money_confirmation")
    private String responseMoneyConfirmation;

    @Column(name = "state", length = 100, nullable = false)
    private String state;

    @Column(length = 100)
    private String statusSms;

    @Column(length = 100)
    private String statusSms2;

    @Column(length = 100)
    private String processType;

    @Column(length = 100)
    private String typeSms;

    @Column(length = 100)
    private Integer retries;

    @Column(length = 100)
    private String statusRetries;

    @Lob
    @Column(name = "description")
    private String description;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "registration_date", nullable = false)
    private Date registrationDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_update")
    private Date dateUpdate;

}

