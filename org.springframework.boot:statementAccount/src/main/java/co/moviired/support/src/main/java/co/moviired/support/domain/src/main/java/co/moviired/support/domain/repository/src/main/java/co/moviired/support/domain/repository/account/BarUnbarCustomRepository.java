package co.moviired.support.domain.repository.account;

import co.moviired.support.domain.dto.UnbarBarHistoryDTO;
import co.moviired.support.domain.entity.account.UnbarBarHistory;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


@Service
public final class BarUnbarCustomRepository {
    private static final int NUMBER_23 = 23;
    private static final int NUMBER_59 = 59;
    private static final int NUMBER_999 = 999;

    @PersistenceContext
    private EntityManager entityManager;

    public List<UnbarBarHistory> searchHistory(@NotNull UnbarBarHistoryDTO history) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<UnbarBarHistory> query = cb.createQuery(UnbarBarHistory.class);
        Root<UnbarBarHistory> barUnbar = query.from(UnbarBarHistory.class);

        // Fields resultants
        List<Selection<UnbarBarHistory>> fields = new ArrayList<>();
        fields.add(barUnbar.get("id"));
        fields.add(barUnbar.get("msisdn"));
        fields.add(barUnbar.get("date"));
        fields.add(barUnbar.get("balance"));
        fields.add(barUnbar.get("quota"));
        fields.add(barUnbar.get("minimumValuePay"));
        fields.add(barUnbar.get("fullPayment"));
        fields.add(barUnbar.get("monthlyCommission"));
        fields.add(barUnbar.get("dailyCommission"));
        fields.add(barUnbar.get("type"));

        // Condiciones
        List<Predicate> conditions = new ArrayList<>();
        generateEqualsConditions(cb, barUnbar, conditions, history);
        generateRangeConditions(cb, barUnbar, conditions, history);

        // Orden
        List<Order> orders = new ArrayList<>();
        orders.add(cb.desc(barUnbar.get("date")));

        // Consulta
        query.select(barUnbar)
                .multiselect(fields.toArray(new Selection[0]))
                .where(cb.and(conditions.toArray(new Predicate[0])));

        // Devolver los resultados
        return entityManager
                .createQuery(query)
                .getResultList();
    }

    private void generateEqualsConditions(@NotNull CriteriaBuilder cb,
                                          @NotNull Root<UnbarBarHistory> barUnbar,
                                          @NotNull List<Predicate> conditions,
                                          @NotNull UnbarBarHistoryDTO cpe) {
        // Equals conditions
        if (cpe.getMsisdn() != null) {
            conditions.add(cb.equal(barUnbar.get("msisdn"), cpe.getMsisdn()));
        }
        // Equals conditions
        if (cpe.getType() != null) {
            conditions.add(cb.equal(barUnbar.get("type"), cpe.getType()));
        }
    }

    private void generateRangeConditions(@NotNull CriteriaBuilder cb,
                                         @NotNull Root<UnbarBarHistory> barUnbar,
                                         @NotNull List<Predicate> conditions,
                                         @NotNull UnbarBarHistoryDTO request) {
        // Range conditions
        Calendar start = Calendar.getInstance();
        start.setTime(request.getStartDate());
        start.add(Calendar.HOUR_OF_DAY, 24);
        start.set(Calendar.HOUR_OF_DAY, 0);
        start.set(Calendar.MINUTE, 0);
        start.set(Calendar.SECOND, 0);
        start.set(Calendar.MILLISECOND, 0);
        Calendar end = Calendar.getInstance();
        end.setTime(request.getEndDate());
        end.add(Calendar.HOUR_OF_DAY, 24);
        end.set(Calendar.HOUR_OF_DAY, NUMBER_23);
        end.set(Calendar.MINUTE, NUMBER_59);
        end.set(Calendar.SECOND, NUMBER_59);
        end.set(Calendar.MILLISECOND, NUMBER_999);

        conditions.add(cb.between(barUnbar.get("date"), start.getTime(), end.getTime()));
    }

}

