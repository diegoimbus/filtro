package co.moviired.auth.server.providers.supportuser.parser;
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
import co.moviired.auth.server.helper.UtilHelper;
import co.moviired.auth.server.providers.IParser;
import co.moviired.auth.server.providers.IRequest;
import co.moviired.auth.server.providers.IResponse;
import co.moviired.auth.server.providers.supportuser.request.ResetPasswordRequest;
import co.moviired.auth.server.providers.supportuser.response.ResetPasswordResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;


@Slf4j
@Service
public class ResetPasswordParser implements IParser {

    private static final long serialVersionUID = 8488946390611766156L;

    private final StatusCodeConfig statusCodeConfig;
    private final UtilHelper utilHelper;
    public ResetPasswordParser(StatusCodeConfig pstatusCodeConfig, UtilHelper putilHelper) {
        super();
        this.statusCodeConfig = pstatusCodeConfig;
        this.utilHelper = putilHelper;
    }

    @Override
    public final IRequest parseRequest(@NotNull Request data) {

        ResetPasswordRequest command = new ResetPasswordRequest();

        command.setMsisdn(data.getUserLogin());
        command.setNewmpin(data.getNewpin());
        command.setConfirmmpin(data.getConfirmnewpin());
        command.setOtp(data.getOtp());
        command.setSource(data.getSource());

        return command;
    }


    @Override
    public final Response parseResponse(@NotNull Request request, @NotNull IResponse pcommand) {
        // Transformar al command específico
        ResetPasswordResponse command = (ResetPasswordResponse) pcommand;
        return utilHelper.parseResponse(command.getErrorCode(), statusCodeConfig);
    }

}

