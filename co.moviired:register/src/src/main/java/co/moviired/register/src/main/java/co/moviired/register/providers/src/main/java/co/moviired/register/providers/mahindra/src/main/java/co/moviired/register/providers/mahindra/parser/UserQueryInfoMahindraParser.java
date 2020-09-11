package co.moviired.register.providers.mahindra.parser;

import co.moviired.base.domain.StatusCode;
import co.moviired.base.domain.enumeration.ErrorType;
import co.moviired.register.config.StatusCodeConfig;
import co.moviired.register.domain.dto.RegisterRequest;
import co.moviired.register.domain.dto.RegisterResponse;
import co.moviired.register.domain.dto.UserMoviiredDTO;
import co.moviired.register.properties.MahindraProperties;
import co.moviired.register.providers.IParser;
import co.moviired.register.providers.IRequest;
import co.moviired.register.providers.IResponse;
import co.moviired.register.providers.mahindra.request.CommandUserQueryInfoRequest;
import co.moviired.register.providers.mahindra.response.CommandUserQueryInfoResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;

@Slf4j
@Service
public class UserQueryInfoMahindraParser implements IParser {

    private final MahindraProperties mahindraProperties;
    private final StatusCodeConfig statusCodeConfig;

    public UserQueryInfoMahindraParser(MahindraProperties pMahindraProperties, StatusCodeConfig pStatusCodeConfig) {
        super();
        this.mahindraProperties = pMahindraProperties;
        this.statusCodeConfig = pStatusCodeConfig;
    }

    @Override
    public final IRequest parseRequest(@NotNull RegisterRequest data, String userType) {
        // Datos especificos de la transaccion
        CommandUserQueryInfoRequest userQueryInfoService = new CommandUserQueryInfoRequest();

        // Usuario
        userQueryInfoService.setMsisdn(data.getUserLogin());
        userQueryInfoService.setTrid(data.getCorrelationId());
        userQueryInfoService.setUsertype(userType);

        // Datos prestablecidos
        userQueryInfoService.setType(mahindraProperties.getUserQueryInfoType());
        userQueryInfoService.setProvider(mahindraProperties.getUserQueryInfoProvider());

        return userQueryInfoService;
    }

    @Override
    public final RegisterResponse parseResponse(@NotNull IResponse pcommand) {
        // Datos especificos de la transaccion
        CommandUserQueryInfoResponse command = (CommandUserQueryInfoResponse) pcommand;

        // Armar el objeto respuesta
        RegisterResponse response = new RegisterResponse();
        StatusCode statusCode = this.statusCodeConfig.of(command.getTxnstatus());

        response.setCode(command.getTxnstatus());

        if (StatusCode.Level.SUCCESS.equals(statusCode.getLevel())) {

            UserMoviiredDTO userMoviiredDTO = new UserMoviiredDTO();
            userMoviiredDTO.setMsisdn(command.getMsisdn());
            userMoviiredDTO.setCellphone(command.getMsisdn());
            userMoviiredDTO.setFirstName(command.getFname());
            userMoviiredDTO.setLastName(command.getLname());
            userMoviiredDTO.setGender(command.getGender());
            userMoviiredDTO.setStatus(command.getStatus());
            userMoviiredDTO.setDob(command.getDob());

            response.setUserMoviiredDTO(userMoviiredDTO);
            response.setType("");
            response.setMessage("OK");
            response.setCode(StatusCode.Level.SUCCESS.value());
        } else {
            response.setCode(command.getTxnstatus());
            response.setType(ErrorType.PROCESSING.name());
            response.setMessage(statusCode.getMessage());
        }
        return response;
    }
}

