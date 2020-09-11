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
import co.moviired.auth.server.exception.PasswordFormatInvalidException;
import co.moviired.auth.server.helper.UtilHelper;
import co.moviired.auth.server.properties.ExtraValidationsProperties;
import co.moviired.auth.server.properties.MahindraProperties;
import co.moviired.auth.server.providers.IParser;
import co.moviired.auth.server.providers.IRequest;
import co.moviired.auth.server.providers.IResponse;
import co.moviired.auth.server.providers.mahindra.request.CommandChangePasswordRequest;
import co.moviired.auth.server.service.ValidationPasswordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;


@Slf4j
@Service
public class ChangePasswordMahindraParser implements IParser {

    private static final long serialVersionUID = 8488946390611766156L;


    private final MahindraProperties mahindraProperties;
    private final UtilHelper utilHelper;
    private final ValidationPasswordService validationPasswordService;
    private final ExtraValidationsProperties extraValidations;
    private final StatusCodeConfig statusCodeConfig;

    @Autowired
    public ChangePasswordMahindraParser(MahindraProperties pmahindraProperties,
                                        StatusCodeConfig pstatusCodeConfig,
                                        ValidationPasswordService pvalidationPasswordService,
                                        ExtraValidationsProperties pextraValidations,
                                        UtilHelper putilHelper) {
        super();
        this.mahindraProperties = pmahindraProperties;
        this.validationPasswordService = pvalidationPasswordService;
        this.statusCodeConfig = pstatusCodeConfig;
        this.extraValidations = pextraValidations;
        this.utilHelper = putilHelper;
    }

    @Override
    public final IRequest parseRequest(@NotNull Request data) throws PasswordFormatInvalidException {

        CommandChangePasswordRequest command = new CommandChangePasswordRequest();

        command.setMsisdn(data.getUserLogin());
        command.setMpin(data.getPin());
        command.setNewmpin(data.getNewpin());
        command.setConfirmmpin(data.getConfirmnewpin());
        command.setType(mahindraProperties.getChangePasswordChannelType());
        command.setLanguage1(mahindraProperties.getChangePasswordLanguage1());

        //Validar formato de password
        if (extraValidations.isEnable()) {
            String doc = null;
            String dob = null;
            if (data.getUser() != null) {
                doc = data.getUser().getIdno();
                dob = data.getUser().getDob();
            }
            if (!validationPasswordService.isValidPasswordFormat(command.getNewmpin(), doc, command.getMsisdn(), dob, UtilHelper.DATE_FORMAT_DOB_LOGIN_MAHINDRA)) {
                throw new PasswordFormatInvalidException("Error al validar contraseña");
            }
        }

        return command;
    }


    @Override
    public final Response parseResponse(@NotNull Request request, @NotNull IResponse pcommand) {
        return utilHelper.parseResponse(pcommand, statusCodeConfig);
    }

}

