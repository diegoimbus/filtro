package co.moviired.support.domain.entity.account;

import co.moviired.support.domain.enums.HistoryType;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Copyright @2019 MOVIIRED. Todos los derechos reservados.
 *
 * @author Rodolfo.rivas
 * @version 1.0.7
 * @category consignment
 */

@Entity
@Data
@Table(name = "bar_unbar_history")
@Builder
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UnbarBarHistory implements Serializable {

    private static final int LENGTH_15 = 15;
    private static final int LENGTH_10 = 10;
    private static final long serialVersionUID = 527701445404960468L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", length = LENGTH_15, nullable = false)
    private Integer id;

    @Column(name = "msisdn", length = LENGTH_10, nullable = false)
    private String msisdn;

    @Column(name = "date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    @Column(name = "balance")
    private Double balance;

    @Column(name = "quota")
    private Double quota;

    @Column(name = "minimum_value_pay")
    private Double minimumValuePay;

    @Column(name = "full_payment")
    private Double fullPayment;

    @Column(name = "monthly_commission")
    private Double monthlyCommission;

    @Column(name = "daily_commission")
    private Double dailyCommission;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "type")
    private HistoryType type;


    public UnbarBarHistory(int pid,
                           String pmsisdn,
                           Date pdate,
                           Double pbalance,
                           Double pquota,
                           Double pminimumValuePay,
                           Double pfullPayment,
                           Double pmonthlyCommission,
                           Double pdailyCommission,
                           HistoryType ptype) {
        this.id = pid;
        this.msisdn = pmsisdn;
        this.date = pdate;
        this.balance = pbalance;
        this.quota = pquota;
        this.minimumValuePay = pminimumValuePay;
        this.fullPayment = pfullPayment;
        this.monthlyCommission = pmonthlyCommission;
        this.dailyCommission = pdailyCommission;
        this.type = ptype;
    }


}

