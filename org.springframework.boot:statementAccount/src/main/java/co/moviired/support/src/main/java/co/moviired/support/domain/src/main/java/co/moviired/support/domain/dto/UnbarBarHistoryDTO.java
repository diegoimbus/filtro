package co.moviired.support.domain.dto;

import co.moviired.support.domain.entity.account.UnbarBarHistory;
import co.moviired.support.domain.enums.HistoryType;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.util.Date;

/**
 * Copyright @2019 MOVIIRED. Todos los derechos reservados.
 *
 * @author Rodolfo.rivas
 * @version 1.0.7
 * @category consignment
 */

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class UnbarBarHistoryDTO implements Serializable {

    private static final long serialVersionUID = 527701445404960468L;

    private Integer id;

    private String msisdn;

    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    private Double balance;

    private Double quota;

    private Double minimumValuePay;

    private Double fullPayment;

    private Double monthlyCommission;

    private Double dailyCommission;

    private HistoryType type;

    @Temporal(TemporalType.TIMESTAMP)
    private Date startDate;

    @Temporal(TemporalType.TIMESTAMP)
    private Date endDate;

    public UnbarBarHistory toEntity() {
        UnbarBarHistory unbarBarHistory = new UnbarBarHistory();
        unbarBarHistory.setId(this.id);
        unbarBarHistory.setMsisdn(this.msisdn);
        unbarBarHistory.setDate(this.date);
        unbarBarHistory.setBalance(this.balance);
        unbarBarHistory.setQuota(this.quota);
        unbarBarHistory.setMinimumValuePay(this.minimumValuePay);
        unbarBarHistory.setFullPayment(this.fullPayment);
        unbarBarHistory.setMonthlyCommission(this.monthlyCommission);
        unbarBarHistory.setDailyCommission(this.dailyCommission);
        unbarBarHistory.setType(this.type);
        return unbarBarHistory;
    }


}

