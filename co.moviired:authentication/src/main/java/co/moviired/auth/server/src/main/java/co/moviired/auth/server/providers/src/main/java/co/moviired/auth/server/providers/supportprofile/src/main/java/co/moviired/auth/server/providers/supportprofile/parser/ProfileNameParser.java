package co.moviired.auth.server.providers.supportprofile.parser;
/*
 * Copyright @2020. Moviired, SAS. Todos los derechos reservados.
 *
 * @author Rivas, Rodolfo
 * @version 1, 2019
 * @since 1.0
 */

import co.moviired.auth.server.conf.StatusCodeConfig;
import co.moviired.auth.server.domain.dto.Request;
import co.moviired.auth.server.domain.dto.Response;
import co.moviired.auth.server.providers.IParser;
import co.moviired.auth.server.providers.IRequest;
import co.moviired.auth.server.providers.IResponse;
import co.moviired.auth.server.providers.supportprofile.request.ProfileNameRequest;
import co.moviired.auth.server.providers.supportprofile.response.ProfileNameResponse;
import co.moviired.base.domain.StatusCode;
import co.moviired.base.domain.enumeration.ErrorType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;


@Slf4j
@Service
public class ProfileNameParser implements IParser {

    private static final long serialVersionUID = 8488946390611766156L;

    private final StatusCodeConfig statusCodeConfig;

    public ProfileNameParser(StatusCodeConfig pstatusCodeConfig) {
        super();
        this.statusCodeConfig = pstatusCodeConfig;
    }

    @Override
    public final IRequest parseRequest(@NotNull Request data) {
        ProfileNameRequest command = new ProfileNameRequest();
        // Datos especificos de la transaccion
        command.setName(data.getName());
        // Datos prestablecidos
        return command;
    }


    @Override
    public final Response parseResponse(@NotNull Request request, @NotNull IResponse pcommand) {
        // Transformar al command específico
        ProfileNameResponse command = (ProfileNameResponse) pcommand;

        // Armar el objeto respuesta
        Response response = new Response();
        StatusCode statusCode = statusCodeConfig.of(command.getErrorCode());

        response.setErrorCode(command.getErrorCode());

        if (StatusCode.Level.SUCCESS.equals(statusCode.getLevel())) {
            response.setErrorType("");
            response.setErrorMessage("OK");
            response.setProfile(command.getProfile());
            response.setErrorCode(StatusCode.Level.SUCCESS.value());
        } else {
            response.setErrorType(ErrorType.PROCESSING.name());
            response.setErrorMessage(statusCode.getMessage());
        }

        return response;
    }

}

