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
import co.moviired.auth.server.properties.MahindraProperties;
import co.moviired.auth.server.properties.SupportOTPProperties;
import co.moviired.auth.server.providers.IParser;
import co.moviired.auth.server.providers.IRequest;
import co.moviired.auth.server.providers.IResponse;
import co.moviired.auth.server.providers.mahindra.request.CommandGenerateOTPRequest;
import co.moviired.auth.server.providers.mahindra.response.CommandResponse;
import co.moviired.base.domain.StatusCode;
import co.moviired.base.domain.enumeration.ErrorType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;


@Slf4j
@Service
public class GenerateOTPMahindraParser implements IParser {

    private static final long serialVersionUID = 8488946390611766156L;
    private final MahindraProperties mahindraProperties;
    private final SupportOTPProperties supportOTPProperties;
    private final StatusCodeConfig statusCodeConfig;
    @Value("${providers.moviired}")
    private String moviired;

    public GenerateOTPMahindraParser(MahindraProperties pmahindraProperties,
                                     SupportOTPProperties psupportOTPProperties,
                                     StatusCodeConfig pstatusCodeConfig) {
        super();
        this.mahindraProperties = pmahindraProperties;
        this.supportOTPProperties = psupportOTPProperties;
        this.statusCodeConfig = pstatusCodeConfig;
    }

    @Override
    public final IRequest parseRequest(@NotNull Request data) {

        CommandGenerateOTPRequest command = new CommandGenerateOTPRequest();

        // Usuario
        command.setMsisdn(data.getUserLogin());

        // Datos prestablecidos
        command.setType(mahindraProperties.getGenerateOTPType());
        command.setProvider(mahindraProperties.getGenerateOTPProvider());

        command.setLanguage1(mahindraProperties.getGenerateOTPLanguageSMS());
        if (supportOTPProperties.getNotifyChannel().equalsIgnoreCase("EMAIL")) {
            command.setLanguage1(mahindraProperties.getGenerateOTPLanguageEMAIL());
        }

        command.setSrvreqtype(mahindraProperties.getGenerateOTPSrvReqType());
        command.setUsertype(data.getSource());
        return command;
    }


    @Override
    public final Response parseResponse(@NotNull Request request, @NotNull IResponse pcommand) {
        // Transformar al command específico
        CommandResponse command = (CommandResponse) pcommand;

        // Armar el objeto respuesta
        Response response = new Response();
        StatusCode statusCode = statusCodeConfig.of(command.getTxnstatus());

        response.setErrorCode(command.getTxnstatus());

        if (StatusCode.Level.SUCCESS.equals(statusCode.getLevel())) {
            response.setErrorType("");
            response.setErrorMessage(command.getMessage());
            response.setErrorCode(StatusCode.Level.SUCCESS.value());
        } else {
            response.setErrorType(ErrorType.PROCESSING.name());
            response.setErrorMessage(statusCode.getMessage());
        }

        return response;
    }

}

