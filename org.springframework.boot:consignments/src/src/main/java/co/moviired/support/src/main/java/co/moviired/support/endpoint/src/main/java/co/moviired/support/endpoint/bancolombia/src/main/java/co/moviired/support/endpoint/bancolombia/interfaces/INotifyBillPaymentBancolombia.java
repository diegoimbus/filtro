package co.moviired.support.endpoint.bancolombia.interfaces;

import co.moviired.support.endpoint.bancolombia.dto.DataConsignmentRequest;
import co.moviired.support.endpoint.bancolombia.dto.DataConsignmentResponse;
import co.moviired.support.endpoint.util.exceptions.BusinessException;

public interface INotifyBillPaymentBancolombia {
    DataConsignmentResponse notifyBillPayment(DataConsignmentRequest var1) throws BusinessException;
}

