package co.moviired.microservice.domain.provider;

import co.moviired.base.domain.exception.DataException;
import co.moviired.base.domain.exception.ParsingException;
import co.moviired.base.domain.exception.ServiceException;
import co.moviired.microservice.client.soap.cargos.GenerarCargo;
import co.moviired.microservice.client.soap.operacionesclean.ValidarFactura;
import co.moviired.microservice.client.soap.seguridadbasecb.GETTICKET;
import co.moviired.microservice.domain.enums.OperationType;
import co.moviired.microservice.domain.request.Input;
import co.moviired.microservice.domain.response.Response;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

public interface IParser extends Serializable {

    default GETTICKET parseRequestTicket(@NotNull Input params) throws ParsingException, DataException {
        throw new ParsingException();
    }

    default ValidarFactura parseRequestQuery(@NotNull Input params, OperationType operationType) throws ParsingException, DataException {
        throw new ParsingException();
    }

    default GenerarCargo parseRequestPay(@NotNull Input params) throws ParsingException {
        throw new ParsingException();
    }

    default Response parseResponse(@NotNull Input params, Object object, @NotNull OperationType operationType) throws ServiceException {
        throw new ParsingException();
    }

}

