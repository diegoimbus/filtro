package co.moviired.microservice.provider;


import co.moviired.microservice.conf.SwitchProperties;
import co.moviired.microservice.domain.request.Input;
import co.moviired.microservice.domain.response.Response;
import co.moviired.microservice.exception.DataException;
import co.moviired.microservice.exception.ParseException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.packager.GenericPackager;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

public interface IParser extends Serializable {

    default ISOMsg parseRequest(@NotNull Input params, SwitchProperties config, GenericPackager packager) throws ParseException, DataException {
        throw new ParseException("Método no implementado");
    }

    default Response parseResponse(@NotNull ISOMsg isoMsg) throws ParseException, DataException {
        throw new ParseException("Método no implementado");
    }

    default Response parseResponse(@NotNull ISOMsg isoMsg, Input parameters) throws ParseException, DataException {
        throw new ParseException("Método no implementado");
    }

}

