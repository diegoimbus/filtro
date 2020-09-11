package co.movii.auth.server.providers.mahindra.parser;

import co.movii.auth.server.conf.StatusCodeConfig;
import co.movii.auth.server.domain.dto.Request;
import co.movii.auth.server.domain.dto.Response;
import co.movii.auth.server.domain.dto.User;
import co.movii.auth.server.properties.MahindraProperties;
import co.movii.auth.server.providers.IParser;
import co.movii.auth.server.providers.IRequest;
import co.movii.auth.server.providers.IResponse;
import co.movii.auth.server.providers.mahindra.request.CommandUserQueryInfoRequest;
import co.movii.auth.server.providers.mahindra.response.CommandResponse;
import co.moviired.base.domain.StatusCode;
import co.moviired.base.domain.enumeration.ErrorType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;

@Slf4j
@Service
public class UserQueryInfoMahindraParser implements IParser {

    private final MahindraProperties mahindraProperties;
    private final StatusCodeConfig statusCodeConfig;

    public UserQueryInfoMahindraParser(MahindraProperties pmahindraProperties, StatusCodeConfig pstatusCodeConfig) {
        super();
        this.mahindraProperties = pmahindraProperties;
        this.statusCodeConfig = pstatusCodeConfig;
    }

    @Override
    public final IRequest parseRequest(@NotNull Request data) {
        // Datos especificos de la transaccion
        CommandUserQueryInfoRequest userQueryInfoService = new CommandUserQueryInfoRequest();

        // Usuario
        userQueryInfoService.setMsisdn(data.getUserLogin());
        userQueryInfoService.setTrid(data.getCorrelationId());
        userQueryInfoService.setUsertype(data.getSource());

        // Datos prestablecidos
        userQueryInfoService.setType(mahindraProperties.getUserQueryInfoType());
        userQueryInfoService.setProvider(mahindraProperties.getUserQueryInfoProvider());

        return userQueryInfoService;
    }

    @Override
    public final Response parseResponse(@NotNull Request request, @NotNull IResponse pcommand) {
        // Datos especificos de la transaccion
        CommandResponse command = (CommandResponse) pcommand;

        // Armar el objeto respuesta
        Response response = new Response();
        StatusCode statusCode = statusCodeConfig.of(command.getTxnstatus());

        response.setErrorCode(command.getTxnstatus());

        if (StatusCode.Level.SUCCESS.equals(statusCode.getLevel())) {

            User user = new User();
            user.setMsisdn(command.getMsisdn());
            user.setCellphone(command.getMsisdn());
            user.setFirstName(command.getFirstname());
            user.setLastName(command.getLastname());
            user.setGender(command.getGender());
            user.setStatus(command.getStatus());
            user.setDob(command.getDob());
            user.setExempted(command.getExempted());

            response.setUser(user);
            response.setErrorType("");
            response.setErrorMessage("OK");
            response.setErrorCode(StatusCode.Level.SUCCESS.value());
        } else {
            response.setErrorType(ErrorType.PROCESSING.name());
            response.setErrorMessage(statusCode.getMessage());
        }
        return response;
    }
}

