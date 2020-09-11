package co.moviired.support.endpoint.bancolombia.interfaces;

import co.moviired.support.endpoint.bancolombia.dto.DataRevertRequest;
import co.moviired.support.endpoint.bancolombia.dto.DataRevertResponse;
import co.moviired.support.endpoint.util.exceptions.BusinessException;
import co.moviired.support.endpoint.util.exceptions.InvokeException;

public interface IRevertBillPaymentBancolombia {
    DataRevertResponse revert(DataRevertRequest var1) throws BusinessException, InvokeException;
}

