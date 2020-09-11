package co.moviired.support.endpoint.bancobogota.interfaces;

import co.moviired.support.endpoint.bancobogota.dto.consignment.in.NotifyBillPaymentInDTO;
import co.moviired.support.endpoint.bancobogota.dto.consignment.out.NotifyBillPaymentOutDTO;
import co.moviired.support.endpoint.util.exceptions.BusinessException;

public interface INotifyBillPayment {

    NotifyBillPaymentOutDTO notifyBillPayment(NotifyBillPaymentInDTO var1) throws BusinessException;

}

