package co.moviired.digitalcontent.business.provider.parser;

import co.moviired.base.domain.exception.ParsingException;
import co.moviired.digitalcontent.business.domain.dto.request.DigitalContentRequest;
import co.moviired.digitalcontent.business.domain.dto.response.DigitalContentResponse;
import co.moviired.digitalcontent.business.helper.Constants;
import co.moviired.digitalcontent.business.provider.IRequest;
import co.moviired.digitalcontent.business.provider.IResponse;
import co.moviired.digitalcontent.business.provider.mahindra.response.CommandResponse;

import javax.validation.constraints.NotNull;
import java.io.Serializable;


public interface IParser extends Serializable {

    default IRequest parseRequest(@NotNull DigitalContentRequest data) throws ParsingException {
        throw new ParsingException(Constants.ERR_PARSE_CODE, Constants.ERR_PARSE_MESSAGE);
    }

    default IRequest parseRequest(@NotNull DigitalContentRequest data, CommandResponse mhResponseAutenticacion) throws ParsingException {
        throw new ParsingException(Constants.ERR_PARSE_CODE, Constants.ERR_PARSE_MESSAGE);
    }

    default DigitalContentResponse parseResponse(@NotNull DigitalContentRequest data, @NotNull IResponse command) throws ParsingException {
        throw new ParsingException(Constants.ERR_PARSE_CODE, Constants.ERR_PARSE_MESSAGE);
    }

}

