package co.moviired.support.domain.repository.redshift;

import co.moviired.support.domain.repository.account.IBarExemptionDaysRepository;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Data
@Component
public final class RepositoryStatementAccountFactory implements Serializable {
    private static final long serialVersionUID = 1905122041950251209L;
    private final IStatementAccountsRepository statementAccountsRepository;
    private final IGradeRepository gradeRepository;
    private final IBarExemptionDaysRepository barExemptionDaysRepository;

    public RepositoryStatementAccountFactory(IStatementAccountsRepository statementAccountsRepository,
                                             IGradeRepository pgradeRepository,
                                             IBarExemptionDaysRepository pbarExemptionDaysRepository) {
        this.statementAccountsRepository = statementAccountsRepository;
        this.gradeRepository = pgradeRepository;
        this.barExemptionDaysRepository = pbarExemptionDaysRepository;
    }
}

