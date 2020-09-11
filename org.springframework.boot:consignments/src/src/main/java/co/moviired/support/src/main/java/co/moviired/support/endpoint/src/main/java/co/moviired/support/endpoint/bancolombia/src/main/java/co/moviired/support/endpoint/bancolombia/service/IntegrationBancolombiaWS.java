package co.moviired.support.endpoint.bancolombia.service;

import co.moviired.base.domain.exception.ParsingException;
import co.moviired.support.endpoint.bancolombia.dto.*;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

@WebService()
public interface IntegrationBancolombiaWS {

    @WebMethod(operationName = "getBillAmount")
    DataQueryResponse getBillAmount(@WebParam(name = "DataQueryRequest") DataQueryRequest var1) throws ParsingException;

    @WebMethod(operationName = "notifyBillPayment")
    DataConsignmentResponse notifyBillPayment(@WebParam(name = "DataConsignmentRequest") DataConsignmentRequest var1) throws ParsingException;

    @WebMethod(operationName = "revertBillPayment")
    DataRevertResponse revertBillPayment(@WebParam(name = "DataRevertRequest") DataRevertRequest var1) throws ParsingException;

}
