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
import co.movii.auth.server.exception.PasswordFormatInvalidException;
import co.movii.auth.server.helper.UtilHelper;
import co.movii.auth.server.properties.ExtraValidationsProperties;
import co.movii.auth.server.properties.MahindraProperties;
import co.movii.auth.server.providers.IParser;
import co.movii.auth.server.providers.IRequest;
import co.movii.auth.server.providers.IResponse;
import co.movii.auth.server.providers.mahindra.request.CommandChangePasswordRequest;
import co.movii.auth.server.service.ValidationPasswordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;


@Slf4j
@Service
public class ChangePasswordMahindraParser implements IParser {

    private static final long serialVersionUID = 8488946390611766156L;


    private final MahindraProperties mahindraProperties;
    private final ValidationPasswordService validationPasswordService;
    private final ExtraValidationsProperties extraValidations;
    private final StatusCodeConfig statusCodeConfig;
    private final UtilHelper utilHelper;

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

        if (UtilHelper.SUBSCRIBER_SOURCE.equals(data.getSource())) {
            command.setType(mahindraProperties.getChangePasswordSubscriberType());
        }

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

