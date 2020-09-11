package co.moviired.microservice.provider;

import co.moviired.base.domain.exception.DataException;
import co.moviired.base.domain.exception.ParsingException;
import co.moviired.microservice.domain.enums.OperationType;
import co.moviired.microservice.domain.request.Input;
import co.moviired.microservice.domain.response.Response;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.packager.GenericPackager;

import javax.validation.constraints.NotNull;

public interface IParser {

    default ISOMsg parseRequest(@NotNull Input params, GenericPackager packager) throws ParsingException, DataException {
        throw new ParsingException();
    }

    default Response parseResponse(@NotNull ISOMsg isoMsg, Input parameters) throws ParsingException, DataException {
        throw new ParsingException();
    }

    default Response parseResponseQuery(@NotNull Input params, OperationType opType) throws ParsingException {
        throw new ParsingException();
    }

}

