package co.moviired.microservice.domain.servicesoap;

import co.moviired.base.domain.exception.ServiceException;
import co.moviired.microservice.client.soap.cargos.GenerarCargoRequestType;
import co.moviired.microservice.client.soap.cargos.GenerarCargoResponseType;
import co.moviired.microservice.client.soap.operacionesclean.ValidarFactura;
import co.moviired.microservice.client.soap.operacionesclean.ValidarFacturaResponseType;
import co.moviired.microservice.client.soap.seguridadbasecb.GETTICKET;

public interface BillRepository {

    String getTicket(GETTICKET requestTicket) throws ServiceException;

    ValidarFacturaResponseType getBillTransaction(ValidarFactura requestBill) throws ServiceException;

    GenerarCargoResponseType payBill(GenerarCargoRequestType generarCargoRequest) throws ServiceException;
}

