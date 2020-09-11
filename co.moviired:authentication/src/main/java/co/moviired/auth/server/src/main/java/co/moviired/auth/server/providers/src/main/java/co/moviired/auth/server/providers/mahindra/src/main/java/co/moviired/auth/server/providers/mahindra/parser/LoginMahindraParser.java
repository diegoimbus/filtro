package co.moviired.auth.server.providers.mahindra.parser;
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
import co.moviired.auth.server.domain.dto.User;
import co.moviired.auth.server.exception.ParseException;
import co.moviired.auth.server.properties.MahindraProperties;
import co.moviired.auth.server.providers.IParser;
import co.moviired.auth.server.providers.IRequest;
import co.moviired.auth.server.providers.IResponse;
import co.moviired.auth.server.providers.mahindra.request.CommandLoginServiceRequest;
import co.moviired.auth.server.providers.mahindra.response.CommandResponse;
import co.moviired.auth.server.providers.mahindrafacade.client.IMahindraFacadeClient;
import co.moviired.base.domain.StatusCode;
import co.moviired.base.domain.enumeration.ErrorType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;


@Slf4j
@Service
public class LoginMahindraParser implements IParser {

    private static final long serialVersionUID = 8488946390611766156L;
    private final MahindraProperties mahindraProperties;
    private final StatusCodeConfig statusCodeConfig;

    private final XmlMapper xmlMapper;
    private final IMahindraFacadeClient mahindraFacadeClient;

    public LoginMahindraParser(StatusCodeConfig pstatusCodeConfig, MahindraProperties pmahindraProperties, IMahindraFacadeClient pmahindraFacadeClient) {
        super();
        this.mahindraProperties = pmahindraProperties;
        this.statusCodeConfig = pstatusCodeConfig;
        this.xmlMapper = new XmlMapper();
        this.xmlMapper.configure(SerializationFeature.INDENT_OUTPUT, false);
        this.mahindraFacadeClient = pmahindraFacadeClient;
    }

    @Override
    public final IRequest parseRequest(@NotNull Request data) throws ParseException {
        try {
            // Datos especificos de la transaccion
            CommandLoginServiceRequest loginService = new CommandLoginServiceRequest();

            // Usuario
            loginService.setMsisdn(data.getUserLogin());

            // PIN
            loginService.setMpin(data.getPin());

            // Datos prestablecidos
            loginService.setType(mahindraProperties.getNameAuthpinreq());
            loginService.setProvider(mahindraProperties.getProviderAuth());
            loginService.setOtpreq(mahindraProperties.getOtpReq());
            loginService.setIspincheckreq(mahindraProperties.getIsPinCheckReq());
            loginService.setSource(mahindraProperties.getSource());

            return loginService;
        } catch (Exception e) {
            throw new ParseException("Usuario y Clave incorrecto.", e);
        }
    }

    @Override
    public final Response parseResponse(@NotNull Request request, @NotNull IResponse pcommand) throws JsonProcessingException {

        // Datos especificos de la transaccion
        CommandResponse command = (CommandResponse) pcommand;

        // Armar el objeto respuesta
        Response response = new Response();
        StatusCode statusCode = statusCodeConfig.of(command.getTxnstatus());
        response.setErrorCode(command.getTxnstatus());

        if (StatusCode.Level.SUCCESS.equals(statusCode.getLevel())) {

            User user = new User();
            user.setFirstName(command.getFirstname());
            user.setLastName(command.getLastname());
            user.setGender(command.getGender());
            user.setEmail(command.getEmail());
            user.setIdno(command.getIdno());
            user.setIdtype(command.getIdtype());
            user.setCellphone(command.getMsisdn());
            user.setMsisdn(command.getMsisdn());
            user.setAgentCode(command.getAgentcode());
            user.setDob(command.getDob());
            user.setUserType(command.getUsertype());

            user.setUserId(command.getUserid());
            user.setGrade(command.getGrade());
            user.setTcp(command.getTcp());
            user.setWalletNumber(command.getWalletnumber());
            user.setLastLogin(command.getLastlogin());
            user.setExempted(command.getExempted());

            response.setUser(user);
            response.setErrorType("");
            response.setErrorMessage("OK");
            response.setErrorCode(StatusCode.Level.SUCCESS.value());

            //Insertar o actualizar usuario en MahindraFacade MongoDB
            this.mahindraFacadeClient.sendValidateUserMHFacade(this.xmlMapper.writeValueAsString(command), request).subscribe();

        } else {
            response.setErrorType(ErrorType.PROCESSING.name());
            response.setErrorMessage(statusCode.getMessage());
        }

        return response;
    }

}

