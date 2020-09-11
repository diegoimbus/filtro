package co.moviired.support.endpoint.bancobogota.service;

import co.moviired.base.domain.exception.ParsingException;
import co.moviired.support.endpoint.bancobogota.dto.consignment.in.*;
import co.moviired.support.endpoint.bancobogota.dto.consignment.out.GetBillAmountOutDTO;
import co.moviired.support.endpoint.bancobogota.dto.consignment.out.NotifyBillPaymentOutDTO;
import co.moviired.support.endpoint.bancobogota.dto.consignment.out.RevertBillPaymentOutDTO;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

@WebService()
public interface IntegrationBankWS {

    @WebMethod(operationName = "getBillAmount")
    GetBillAmountOutDTO getBillAmount(@WebParam(name = "GetBillAmountInDTO") GetBillAmountInDTO var1) throws ParsingException;


    @WebMethod(operationName = "notifyBillPayment")
    NotifyBillPaymentOutDTO notifyBillPayment(@WebParam(name = "NotifyBillPaymentInDTO") NotifyBillPaymentInDTO var1) throws ParsingException;

    @WebMethod(operationName = "revertBillPayment")
    RevertBillPaymentOutDTO revertBillPayment(@WebParam(name = "RevertBillPaymentInDTO") RevertBillPaymentInDTO var1) throws ParsingException;

}

