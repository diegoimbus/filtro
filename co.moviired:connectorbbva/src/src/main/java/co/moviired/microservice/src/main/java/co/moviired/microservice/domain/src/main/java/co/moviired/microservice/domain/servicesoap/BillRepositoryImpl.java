package co.moviired.microservice.domain.servicesoap;

import co.moviired.base.domain.enumeration.ErrorType;
import co.moviired.base.domain.exception.ServiceException;
import co.moviired.microservice.client.soap.cargos.FaultCargos;
import co.moviired.microservice.client.soap.cargos.GenerarCargoRequestType;
import co.moviired.microservice.client.soap.cargos.GenerarCargoResponseType;
import co.moviired.microservice.client.soap.cargos.impl.CargosOperacionesService;
import co.moviired.microservice.client.soap.cargos.impl.OperacionesCargos;
import co.moviired.microservice.client.soap.operacionesclean.*;
import co.moviired.microservice.client.soap.operacionesclean.impl.OperacionesRecaudos;
import co.moviired.microservice.client.soap.operacionesclean.impl.RecaudosOperacionesService;
import co.moviired.microservice.client.soap.seguridadbasecb.GETTICKET;
import co.moviired.microservice.client.soap.seguridadbasecb.GETTICKETRESPONSE;
import co.moviired.microservice.client.soap.seguridadbasecb.impl.SeguridadBase;
import co.moviired.microservice.client.soap.seguridadbasecb.impl.SeguridadBase_Service;
import co.moviired.microservice.conf.BankProductsProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import javax.xml.ws.BindingProvider;


@Slf4j
@Repository
public class BillRepositoryImpl implements BillRepository {

    private final BankProductsProperties bankProducts;
    private static final String REQUEST_TIMEOUT = "com.sun.xml.internal.ws.request.timeout";
    private static final String CONNECT_TIMEOUT = "com.sun.xml.internal.ws.connect.timeout";

    public BillRepositoryImpl(BankProductsProperties bankProducts) {
        this.bankProducts = bankProducts;
    }

    @Override
    public String getTicket(GETTICKET requestTicket) throws ServiceException {

        log.info("************ Start WS ticket connector BBVA ************");
        String token = "";

        try {
            SeguridadBase_Service serviceTicket = new SeguridadBase_Service();
            SeguridadBase seguridadBase = serviceTicket.getSeguridadBaseSOAP();

            ((BindingProvider) seguridadBase).getRequestContext().put(REQUEST_TIMEOUT, Integer.parseInt(this.bankProducts.getRead()));
            ((BindingProvider) seguridadBase).getRequestContext().put(CONNECT_TIMEOUT, Integer.parseInt(this.bankProducts.getConnect()));

            GETTICKETRESPONSE responseTicket = seguridadBase.getTicket(requestTicket);
            token = responseTicket.getTicketID();

        } catch (Exception e) {
            log.error("ERROR EN TICKET Exception:[" + e + "]");
        }
        log.info("************ End WS ticket connector BBVA ************");

        if (token.equals("")) {
            throw new ServiceException(ErrorType.DATA, "99", "NO SE OBTUVO RESPUESTA DE TICKET");
        }

        return token;
    }

    @Override
    public ValidarFacturaResponseType getBillTransaction(ValidarFactura requestBill) throws ServiceException {

        log.info("************ Start WS validarFactura connector BBVA ************");
        RecaudosOperacionesService recaudosOperacionesService;
        OperacionesRecaudos operacionesRecaudos;
        ValidarFacturaResponseType facturaResponseType = null;
        ServiceException serviceException = null;

        try {
            recaudosOperacionesService = new RecaudosOperacionesService();
            operacionesRecaudos = this.bankProducts.isProduccion() ? recaudosOperacionesService.getBbvaRecaudosOperacionesSoap12() : recaudosOperacionesService.getBbvaRecaudosOperacionesSoap();

            ((BindingProvider) operacionesRecaudos).getRequestContext().put(REQUEST_TIMEOUT, Integer.parseInt(this.bankProducts.getRead()));
            ((BindingProvider) operacionesRecaudos).getRequestContext().put(CONNECT_TIMEOUT, Integer.parseInt(this.bankProducts.getConnect()));

            facturaResponseType = operacionesRecaudos.validarFactura(requestBill);

        } catch (FaultCanales e1) {
            log.error("ERROR EN RECAUDOS FaultCanales:[" + e1.getFaultInfo().getErrores().get(0).getLongMessage() + "]");
            serviceException = new ServiceException(ErrorType.DATA, e1.getFaultInfo().getErrores().get(0).getErrorCode(), e1.getFaultInfo().getErrores().get(0).getLongMessage());
        } catch (FaultRecaudos e1) {
            log.error("ERROR EN RECAUDOS FaultRecaudos:[" + e1.getFaultInfo().getErrores().get(0).getLongMessage() + "]");
            serviceException = new ServiceException(ErrorType.DATA, e1.getFaultInfo().getErrores().get(0).getErrorCode(), e1.getFaultInfo().getErrores().get(0).getLongMessage());
        } catch (FaultSystem e1) {
            log.error("ERROR EN RECAUDOS FaultSystem:[" + e1.getFaultInfo().getErrores().get(0).getLongMessage() + "]");
            serviceException = new ServiceException(ErrorType.DATA, e1.getFaultInfo().getErrores().get(0).getErrorCode(), e1.getFaultInfo().getErrores().get(0).getLongMessage());
        } catch (Exception e) {
            log.error("ERROR EN RECAUDOS Exception:[" + e + "]");
            serviceException = new ServiceException(ErrorType.DATA, "99", e.getMessage());
        }

        if(facturaResponseType==null && serviceException!=null){
            throw serviceException;
        }

        log.info("************ End WS validarFactura connector BBVA ************");

        return facturaResponseType;
    }

    @Override
    public GenerarCargoResponseType payBill(GenerarCargoRequestType generarCargoRequest) throws ServiceException {

        log.info("************ Start WS generarCargo connector BBVA ************");
        CargosOperacionesService cargosOperacionesService;
        OperacionesCargos operacionesCargos;
        GenerarCargoResponseType cargoResponseType = null;
        ServiceException serviceException = null;

        try {
            cargosOperacionesService = new CargosOperacionesService();
            operacionesCargos = this.bankProducts.isProduccion() ? cargosOperacionesService.getBbvaCargosSOAP12() : cargosOperacionesService.getBbvaCargosSOAP();

            ((BindingProvider) operacionesCargos).getRequestContext().put(REQUEST_TIMEOUT, Integer.parseInt(this.bankProducts.getRead()));
            ((BindingProvider) operacionesCargos).getRequestContext().put(CONNECT_TIMEOUT, Integer.parseInt(this.bankProducts.getConnect()));

            cargoResponseType = operacionesCargos.generarCargo(generarCargoRequest);

        } catch (co.moviired.microservice.client.soap.cargos.FaultCanales e1) {
            log.error("ERROR EN CARGOS FaultCanales:[" + e1.getFaultInfo().getCanalesFault().getErrores().get(0).getLongMessage() + "]");
            serviceException = new ServiceException(ErrorType.DATA, e1.getFaultInfo().getCanalesFault().getErrores().get(0).getErrorCode(), e1.getFaultInfo().getCanalesFault().getErrores().get(0).getLongMessage());
        } catch (FaultCargos e1) {
            log.error("ERROR EN CARGOS FaultCargos:[" + e1.getFaultInfo().getCargoFault().getErrores().getLongMessage() + "]");
            serviceException = new ServiceException(ErrorType.DATA, e1.getFaultInfo().getCargoFault().getErrores().getErrorCode(), e1.getFaultInfo().getCargoFault().getErrores().getLongMessage());
        } catch (co.moviired.microservice.client.soap.cargos.FaultSystem e1) {
            log.error("ERROR EN CARGOS FaultSystem:[" + e1.getFaultInfo().getSystemFault().getErrores().get(0).getLongMessage() + "]");
            serviceException = new ServiceException(ErrorType.DATA, e1.getFaultInfo().getSystemFault().getErrores().get(0).getErrorCode(), e1.getFaultInfo().getSystemFault().getErrores().get(0).getLongMessage());
        } catch (Exception e) {
            log.error("ERROR EN CARGOS Exception:[" + e + "]");
            serviceException = new ServiceException(ErrorType.DATA, "99", e.getMessage());
        }

        if(cargoResponseType==null && serviceException!=null){
            throw serviceException;
        }

        log.info("************ End WS generarCargo connector BBVA ************");

        return cargoResponseType;
    }

}

