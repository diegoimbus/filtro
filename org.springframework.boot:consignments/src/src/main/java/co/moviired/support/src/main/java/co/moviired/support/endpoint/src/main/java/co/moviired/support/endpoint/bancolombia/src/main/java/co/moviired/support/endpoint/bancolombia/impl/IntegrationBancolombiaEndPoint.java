package co.moviired.support.endpoint.bancolombia.impl;

import co.moviired.base.domain.exception.ParsingException;
import co.moviired.support.endpoint.bancolombia.dto.*;
import co.moviired.support.endpoint.bancolombia.manager.IntegrationBancolombiaManager;
import co.moviired.support.endpoint.bancolombia.service.IntegrationBancolombiaWS;
import lombok.extern.slf4j.Slf4j;

import javax.jws.WebService;

@Slf4j
@WebService(
        endpointInterface = "co.moviired.support.endpoint.bancolombia.impl.IntegrationBancolombiaEndPoint"
)
public class IntegrationBancolombiaEndPoint implements IntegrationBancolombiaWS {

    private static final String CLASS_NAME = IntegrationBancolombiaEndPoint.class.getSimpleName();
    private final IntegrationBancolombiaManager manager;

    public IntegrationBancolombiaEndPoint(IntegrationBancolombiaManager manager) {
        this.manager = manager;
    }

    public DataQueryResponse getBillAmount(DataQueryRequest dataQueryRequest) throws ParsingException {
        log.debug(CLASS_NAME + " - getBillAmount - " + dataQueryRequest);
        return manager.getBillAmount(dataQueryRequest);
    }

    public DataConsignmentResponse notifyBillPayment(DataConsignmentRequest dataConsignmentRequest) throws ParsingException {
        log.debug(CLASS_NAME, "notifyBillPayment", dataConsignmentRequest);
        return manager.notifyBillPayment(dataConsignmentRequest);
    }

    public DataRevertResponse revertBillPayment(DataRevertRequest dataRevertRequest) throws ParsingException {
        log.debug(CLASS_NAME, "revertBillPayment", dataRevertRequest);
        return manager.revertBillPayment(dataRevertRequest);
    }

}

