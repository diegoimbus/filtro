package co.moviired.auth.server.providers.mahindra.parser;
/*
 * Copyright @2020. Moviired, SAS. Todos los derechos reservados.
 *
 * @author Bejarano, Cindy
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
import co.moviired.auth.server.providers.mahindra.request.CommandResetPasswordRequest;
import co.moviired.auth.server.service.QueryUserInfoService;
import co.moviired.auth.server.service.ValidationPasswordService;
import co.moviired.base.domain.StatusCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;

@Slf4j
@Service
public class ResetPasswordMahindraParser implements IParser {

    private static final long serialVersionUID = 8488946390611766156L;
    private final MahindraProperties mahindraProperties;
    private final UtilHelper utilHelper;
    private final StatusCodeConfig statusCodeConfig;
    private final QueryUserInfoService queryUserInfoService;
    private final ExtraValidationsProperties extraValidations;
    private final ValidationPasswordService validationPasswordService;

    public ResetPasswordMahindraParser(MahindraProperties pmahindraProperties,
                                       StatusCodeConfig pstatusCodeConfig,
                                       ValidationPasswordService pvalidationPasswordService,
                                       QueryUserInfoService pqueryUserInfoService,
                                       ExtraValidationsProperties pextraValidations,
                                       UtilHelper putilHelper) {
        super();
        this.mahindraProperties = pmahindraProperties;
        this.statusCodeConfig = pstatusCodeConfig;
        this.queryUserInfoService = pqueryUserInfoService;
        this.validationPasswordService = pvalidationPasswordService;
        this.extraValidations = pextraValidations;
        this.utilHelper = putilHelper;
    }

    @Override
    public final IRequest parseRequest(@NotNull Request data) throws PasswordFormatInvalidException {

        CommandResetPasswordRequest command = new CommandResetPasswordRequest();

        // Datos especificos de la transaccion
        command.setMsisdn(data.getUserLogin());
        command.setNewpin(data.getNewpin());
        command.setConfirmpin(data.getConfirmnewpin());
        command.setOtp(data.getOtp());

        // Datos prestablecidos
        command.setType(mahindraProperties.getResetPasswordType());
        command.setProvider(mahindraProperties.getResetPasswordProvider());
        command.setUsertype(data.getSource());
        command.setPintype(mahindraProperties.getResetPasswordPinType());
        command.setLanguage1(mahindraProperties.getResetPasswordLanguage1());

        Response responseUserInfo = this.queryUserInfoService.queryUserInfo(Mono.just(data), command.getMsisdn());

        //Validar formato de password
        if (extraValidations.isEnable() && (responseUserInfo.getErrorCode().equals(StatusCode.Level.SUCCESS.value())) &&
                (!validationPasswordService.isValidPasswordFormat(command.getNewpin(),
                        responseUserInfo.getUser().getIdno(),
                        command.getMsisdn(),
                        responseUserInfo.getUser().getDob(),
                        UtilHelper.DATE_FORMAT_DOB_QUERY_USER_INFO_MAHINDRA))
        ) {
            throw new PasswordFormatInvalidException("Error al validar contraseña");
        }

        return command;
    }


    @Override
    public final Response parseResponse(@NotNull Request request, @NotNull IResponse pcommand) {
        return utilHelper.parseResponse(pcommand, statusCodeConfig);
    }

}

