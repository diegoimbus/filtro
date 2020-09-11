package co.moviired.microservice.provider;

import co.moviired.base.domain.exception.ParsingException;
import co.moviired.base.domain.exception.ServiceException;
import co.moviired.microservice.domain.request.Input;
import co.moviired.microservice.domain.response.Response;

public interface IParser {

    default Object parseRequest(Input parameters) throws ServiceException {
        throw new ParsingException();
    }

    default Response parseResponse(Object response, Input parameters) throws ServiceException {
        throw new ParsingException();
    }

}

