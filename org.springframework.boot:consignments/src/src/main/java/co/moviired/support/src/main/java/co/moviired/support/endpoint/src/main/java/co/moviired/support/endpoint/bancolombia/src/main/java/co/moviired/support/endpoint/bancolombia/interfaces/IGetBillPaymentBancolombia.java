package co.moviired.support.endpoint.bancolombia.interfaces;

import co.moviired.support.endpoint.bancolombia.dto.DataQueryRequest;
import co.moviired.support.endpoint.bancolombia.dto.DataQueryResponse;
import co.moviired.support.endpoint.util.exceptions.BusinessException;

public interface IGetBillPaymentBancolombia {
    DataQueryResponse getBillAmount(DataQueryRequest var1) throws BusinessException;
}

