package co.moviired.transpiler.integration;

import co.moviired.transpiler.exception.ParseException;
import co.moviired.transpiler.jpa.movii.domain.dto.hermes.IHermesRequest;
import co.moviired.transpiler.jpa.movii.domain.dto.hermes.IHermesResponse;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

public interface IHermesParser extends Serializable {

    default IHermesRequest parseRequest(@NotNull String request) throws ParseException {
        throw new ParseException("Método no implementado");
    }

    default String parseResponse(@NotNull IHermesResponse hermesResponse) throws ParseException {
        throw new ParseException("Método no implementado");
    }
}

