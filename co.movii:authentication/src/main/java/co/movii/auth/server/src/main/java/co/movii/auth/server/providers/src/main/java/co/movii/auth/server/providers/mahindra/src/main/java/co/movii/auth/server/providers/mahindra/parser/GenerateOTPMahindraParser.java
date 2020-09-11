package co.movii.auth.server.providers.mahindra.parser;
/*
 * Copyright @2019. Movii, SAS. Todos los derechos reservados.
 *
 * @author Rivas, Rodolfo
 * @version 1, 2019
 * @since 1.0
 */

import co.movii.auth.server.conf.StatusCodeConfig;
import co.movii.auth.server.domain.dto.Request;
import co.movii.auth.server.domain.dto.Response;
import co.movii.auth.server.helper.UtilHelper;
import co.movii.auth.server.properties.MahindraProperties;
import co.movii.auth.server.properties.SupportOTPProperties;
import co.movii.auth.server.providers.IParser;
import co.movii.auth.server.providers.IRequest;
import co.movii.auth.server.providers.IResponse;
import co.movii.auth.server.providers.mahindra.request.CommandGenerateOTPRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;


@Slf4j
@Service
public class GenerateOTPMahindraParser implements IParser {

    private static final long serialVersionUID = 8488946390611766156L;
    private final MahindraProperties mahindraProperties;
    private final SupportOTPProperties supportOTPProperties;
    private final StatusCodeConfig statusCodeConfig;
    private final UtilHelper utilHelper;

    public GenerateOTPMahindraParser(MahindraProperties pmahindraProperties,
                                     SupportOTPProperties psupportOTPProperties,
                                     StatusCodeConfig pstatusCodeConfig,
                                     UtilHelper putilHelper) {
        super();
        this.mahindraProperties = pmahindraProperties;
        this.supportOTPProperties = psupportOTPProperties;
        this.statusCodeConfig = pstatusCodeConfig;
        this.utilHelper = putilHelper;
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
        // SOURCES IS MOVII
        if (supportOTPProperties.getNotifySubscriber().equalsIgnoreCase("EMAIL")) {
            command.setLanguage1(mahindraProperties.getGenerateOTPLanguageEMAIL());
        }

        command.setSrvreqtype(mahindraProperties.getGenerateOTPSrvReqType());
        command.setUsertype(data.getSource());
        return command;
    }


    @Override
    public final Response parseResponse(@NotNull Request request, @NotNull IResponse pcommand) {
        return utilHelper.parseResponse(pcommand, statusCodeConfig);
    }

}

