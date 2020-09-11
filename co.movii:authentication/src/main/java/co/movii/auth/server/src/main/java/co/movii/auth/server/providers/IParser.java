package co.movii.auth.server.providers;

import co.movii.auth.server.domain.dto.Request;
import co.movii.auth.server.domain.dto.Response;
import co.movii.auth.server.exception.AuthException;
import co.movii.auth.server.exception.ParseException;
import com.fasterxml.jackson.core.JsonProcessingException;

import javax.validation.constraints.NotNull;
import java.io.Serializable;


public interface IParser extends Serializable {

    default IRequest parseRequest(@NotNull Request data) throws AuthException {
        throw new ParseException("Método no implementado");
    }

    default Response parseResponse(@NotNull Request data, @NotNull IResponse command) throws AuthException, JsonProcessingException {
        throw new ParseException("Método no implementado");
    }


}

