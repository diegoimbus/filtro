package co.moviired.supportp2pvalidatortransaction.service;

import co.moviired.supportp2pvalidatortransaction.common.config.GlobalProperties;
import co.moviired.supportp2pvalidatortransaction.common.config.StatusCodeConfig;
import co.moviired.supportp2pvalidatortransaction.common.provider.schedulersupport.SchedulerSupportConnector;
import co.moviired.supportp2pvalidatortransaction.common.service.IPrimaryController;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;

import static co.moviired.supportp2pvalidatortransaction.common.util.Constants.*;
import static co.moviired.supportp2pvalidatortransaction.util.Constants.RATE_VALIDATE_TRANSACTIONS_P2P;
import static co.moviired.supportp2pvalidatortransaction.util.Constants.SCHEDULER_VALIDATE_TRANSACTIONS_P2P;

@Controller
@RequestMapping(PROJECT_PATH)
public class SupportP2pTransactionsController extends IPrimaryController<SupportP2pTransactionsService> {

    public SupportP2pTransactionsController(@NotNull SupportP2pTransactionsService supportP2pTransactionsService,
                                            @NotNull GlobalProperties globalProperties,
                                            @NotNull StatusCodeConfig statusCodeConfig,
                                            @NotNull @Qualifier(SCHEDULER_HELPER_API) SchedulerSupportConnector schedulerSupportConnector) {
        super(supportP2pTransactionsService, globalProperties, statusCodeConfig, schedulerSupportConnector);
    }

    @Scheduled(fixedRateString = RATE_VALIDATE_TRANSACTIONS_P2P, initialDelay = INITIAL_SCHEDULED_DELAY)
    public void validateP2pTransactions() {
        assignCorrelative(null)
                .flatMap(request -> startScheduler(SCHEDULER_VALIDATE_TRANSACTIONS_P2P, request, baseBaseService.validateP2pTransactions(request.getCorrelative())))
                .block();
    }
}

