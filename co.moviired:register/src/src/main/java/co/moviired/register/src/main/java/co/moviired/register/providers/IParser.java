package co.moviired.register.providers;

import co.moviired.register.domain.dto.RegisterRequest;
import co.moviired.register.domain.dto.RegisterResponse;
import co.moviired.register.exceptions.ParseException;

import javax.validation.constraints.NotNull;
import java.io.Serializable;


public interface IParser extends Serializable {

    default IRequest parseRequest(@NotNull RegisterRequest data, String userType) throws ParseException {
        throw new ParseException("Método no implementado");
    }

    default IRequest parseRequest(@NotNull RegisterRequest data) throws ParseException {
        throw new ParseException("Método no implementado");
    }

    default RegisterResponse parseResponse(@NotNull IResponse command) throws ParseException {
        throw new ParseException("Método no implementado");
    }


}

