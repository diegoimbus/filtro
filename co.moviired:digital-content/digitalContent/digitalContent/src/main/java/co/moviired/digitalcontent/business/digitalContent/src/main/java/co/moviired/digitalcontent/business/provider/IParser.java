package co.moviired.digitalcontent.business.provider;

import co.moviired.base.domain.exception.ParsingException;
import co.moviired.digitalcontent.business.domain.dto.request.DigitalContentRequest;
import co.moviired.digitalcontent.business.domain.dto.response.DigitalContentResponse;
import co.moviired.digitalcontent.business.provider.integrator.response.ResponseIntegrator;
import co.moviired.digitalcontent.business.provider.mahindra.response.CommandLoginServiceResponse;
import co.moviired.digitalcontent.business.provider.mahindra.response.CommandResponse;
import com.fasterxml.jackson.core.JsonProcessingException;

import javax.validation.constraints.NotNull;
import java.io.Serializable;


public interface IParser extends Serializable {

    String NO_IMPLEMENTADO = "Método no implementado";

    default IRequest parseRequest(@NotNull DigitalContentRequest data) throws ParsingException, JsonProcessingException {
        throw new ParsingException("99", NO_IMPLEMENTADO);
    }

    default IRequest parseRequest(@NotNull DigitalContentRequest data, CommandLoginServiceResponse mhResponseAutenticacion) throws ParsingException {
        throw new ParsingException("99", NO_IMPLEMENTADO);
    }

    default IRequest parseRequest(@NotNull DigitalContentRequest data, CommandResponse mhResponse) throws ParsingException {
        throw new ParsingException("99", NO_IMPLEMENTADO);
    }

    default IRequest parseRequest(@NotNull DigitalContentRequest data, DigitalContentResponse response) throws ParsingException {
        throw new ParsingException("99", NO_IMPLEMENTADO);
    }

    default DigitalContentResponse parseResponse(@NotNull DigitalContentRequest data, @NotNull IResponse command) throws ParsingException {
        throw new ParsingException("99", NO_IMPLEMENTADO);
    }

    default DigitalContentResponse parseResponse(@NotNull ResponseIntegrator commandResponse, DigitalContentRequest request) throws ParsingException {
        throw new ParsingException("99", NO_IMPLEMENTADO);
    }

}

