package co.moviired.microservice.provider;

/*
 * Copyright @2019. Movii, SAS. Todos los derechos reservados.
 *
 * @author Cindy Bejarano, Oscar Lopez
 * @since 1.0.0
 */

import co.moviired.base.domain.exception.DataException;
import co.moviired.base.domain.exception.ParsingException;
import co.moviired.microservice.domain.request.Input;
import co.moviired.microservice.domain.response.Response;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.packager.GenericPackager;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

public interface IParser extends Serializable {

    default ISOMsg parseRequest(@NotNull Input params, GenericPackager packager) throws ParsingException, DataException {
        throw new ParsingException();
    }

    default Response parseResponse(@NotNull ISOMsg isoMsg) throws ParsingException {
        throw new ParsingException();
    }

    default Response parseResponse(@NotNull ISOMsg isoMsg, Input parameters) throws ParsingException, DataException {
        throw new ParsingException();
    }

}

