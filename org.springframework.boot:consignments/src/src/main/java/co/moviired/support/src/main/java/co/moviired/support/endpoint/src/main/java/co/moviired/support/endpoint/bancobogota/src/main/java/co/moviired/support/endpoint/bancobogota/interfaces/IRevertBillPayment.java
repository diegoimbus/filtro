package co.moviired.support.endpoint.bancobogota.interfaces;

import co.moviired.support.endpoint.bancobogota.dto.consignment.in.RevertBillPaymentInDTO;
import co.moviired.support.endpoint.bancobogota.dto.consignment.out.RevertBillPaymentOutDTO;
import co.moviired.support.endpoint.util.exceptions.BusinessException;
import co.moviired.support.endpoint.util.exceptions.InvokeException;

public interface IRevertBillPayment {

    RevertBillPaymentOutDTO revert(RevertBillPaymentInDTO var1) throws BusinessException, InvokeException;

}

