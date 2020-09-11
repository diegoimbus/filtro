package co.moviired.business.provider;

import co.moviired.base.domain.exception.DataException;
import co.moviired.base.domain.exception.ParsingException;
import co.moviired.base.domain.exception.ProcessingException;
import co.moviired.business.domain.dto.banking.request.RequestFormatBanking;
import co.moviired.business.domain.dto.banking.response.Response;
import com.fasterxml.jackson.core.JsonProcessingException;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

public interface IParser extends Serializable {

    default IRequest parseRequest(@NotNull RequestFormatBanking bankingRequest) throws ParsingException, JsonProcessingException, ProcessingException, DataException {
        throw new ParsingException("99", "Método no implementado");
    }

    default IRequest parseRequest(@NotNull RequestFormatBanking bankingRequest, Response response) throws ParsingException {
        throw new ParsingException("99", "Método no implementado");
    }

    default Response parseResponse(@NotNull RequestFormatBanking bankingRequest, @NotNull IResponse command) throws ParsingException {
        throw new ParsingException("99", "Método no implementado");
    }

}

