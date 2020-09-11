package co.moviired.support.domain.repository.account;

import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public final class RepositoryBarFactory {
    private static final long serialVersionUID = 1905122041950251207L;
    private final IBarUnbarConfigRepository barUnbarConfigRepository;
    private final IBarUnbarHistoryRepository barUnbarHistoryRepository;
    private final BarUnbarCustomRepository barUnbarHistoryCustomRepository;
    private final IBarTemplateRepository barTemplateRepository;
    private final IBarExemptionDaysRepository barExemptionDaysRepository;


    public RepositoryBarFactory(IBarUnbarConfigRepository pbarUnbarConfigRepository,
                                IBarUnbarHistoryRepository pbarUnbarHistoryRepository,
                                BarUnbarCustomRepository pbarUnbarHistoryCustomRepository,
                                IBarTemplateRepository pbarTemplateRepository,
                                IBarExemptionDaysRepository pbarExemptionDaysRepository) {
        this.barUnbarConfigRepository = pbarUnbarConfigRepository;
        this.barUnbarHistoryRepository = pbarUnbarHistoryRepository;
        this.barUnbarHistoryCustomRepository = pbarUnbarHistoryCustomRepository;
        this.barTemplateRepository = pbarTemplateRepository;
        this.barExemptionDaysRepository = pbarExemptionDaysRepository;
    }
}

