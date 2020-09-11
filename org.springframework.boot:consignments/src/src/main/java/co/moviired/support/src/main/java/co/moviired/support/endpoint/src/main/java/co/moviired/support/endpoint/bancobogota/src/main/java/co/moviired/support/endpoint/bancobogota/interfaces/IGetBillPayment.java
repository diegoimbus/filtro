package co.moviired.support.endpoint.bancobogota.interfaces;

import co.moviired.support.endpoint.bancobogota.dto.consignment.in.GetBillAmountInDTO;
import co.moviired.support.endpoint.bancobogota.dto.consignment.out.GetBillAmountOutDTO;
import co.moviired.support.endpoint.util.exceptions.BusinessException;

public interface IGetBillPayment {

    GetBillAmountOutDTO getBillAmount(GetBillAmountInDTO var1) throws BusinessException;

}

