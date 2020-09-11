package co.moviired.support.endpoint.bancobogota.impl;

import co.moviired.base.domain.exception.ParsingException;
import co.moviired.support.endpoint.bancobogota.dto.consignment.in.*;
import co.moviired.support.endpoint.bancobogota.dto.consignment.out.GetBillAmountOutDTO;
import co.moviired.support.endpoint.bancobogota.dto.consignment.out.NotifyBillPaymentOutDTO;
import co.moviired.support.endpoint.bancobogota.dto.consignment.out.RevertBillPaymentOutDTO;
import co.moviired.support.endpoint.bancobogota.manager.IntegrationBankManager;
import co.moviired.support.endpoint.bancobogota.service.IntegrationBankWS;
import lombok.extern.slf4j.Slf4j;

import javax.jws.WebService;

@Slf4j
@WebService(
        endpointInterface = "co.moviired.support.endpoint.bancobogota.impl.IntegrationBankEndPoint"
)
public class IntegrationBankEndPoint implements IntegrationBankWS {

    private static final String CLASS_NAME = IntegrationBankEndPoint.class.getSimpleName();

    private final IntegrationBankManager manager;

    public IntegrationBankEndPoint(IntegrationBankManager manager) {
        this.manager = manager;
    }

    public GetBillAmountOutDTO getBillAmount(GetBillAmountInDTO getBillAmountInDTO) throws ParsingException {

        log.debug(CLASS_NAME + " - getBillAmount - " + getBillAmountInDTO);
        return manager.getBillAmount(getBillAmountInDTO);
    }

    public NotifyBillPaymentOutDTO notifyBillPayment(NotifyBillPaymentInDTO notifyBillPaymentInDTO) throws ParsingException {
        log.debug(CLASS_NAME, "notifyBillPayment", notifyBillPaymentInDTO);
        return manager.notifyBillPayment(notifyBillPaymentInDTO);
    }

    public RevertBillPaymentOutDTO revertBillPayment(RevertBillPaymentInDTO revertBillPaymentInDTO) throws ParsingException {
        log.debug(CLASS_NAME, "revertBillPayment", revertBillPaymentInDTO);
        return manager.revertBillPayment(revertBillPaymentInDTO);
    }

}

