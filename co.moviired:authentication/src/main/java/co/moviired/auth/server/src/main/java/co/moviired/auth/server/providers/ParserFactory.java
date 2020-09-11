package co.moviired.auth.server.providers;


import co.moviired.auth.server.domain.enums.OperationType;
import co.moviired.auth.server.domain.enums.ProviderType;
import co.moviired.auth.server.exception.ParseException;
import co.moviired.auth.server.providers.mahindra.parser.*;
import co.moviired.auth.server.providers.supportprofile.parser.ProfileNameParser;
import co.moviired.auth.server.providers.supportuser.parser.*;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Component
public class ParserFactory implements Serializable {

    private static final long serialVersionUID = -8111145829471817085L;

    private static final String INVALID_OPERATION = "Operación inválida";

    private final ChangePasswordMahindraParser changePasswordMahindraParser;
    private final ResetPasswordMahindraParser resetPasswordMahindraParser;
    private final LoginMahindraParser loginServiceMahindraParser;
    private final UserQueryInfoMahindraParser userQueryInfoMahindraParser;
    private final GenerateOTPMahindraParser generateOTPMahindraParser;

    private final ChangePasswordParser changePasswordServiceLdapParser;
    private final ResetPasswordParser resetPasswordServiceLdapParser;
    private final LoginServiceParser loginServiceLdapParser;
    private final GenerateOTPParser generateOTPParser;
    private final GetUserParser getUserLdapParser;
    private final ProfileNameParser profileNameParser;

    public ParserFactory(@NotNull ParserMahindraFactory parserMahindraFactory,
                         @NotNull ChangePasswordParser pchangePasswordServiceLdapParser,
                         @NotNull ResetPasswordParser presetPasswordServiceLdapParser,
                         @NotNull LoginServiceParser ploginServiceLdapParser,
                         @NotNull GenerateOTPParser pgenerateOTPParser,
                         @NotNull GetUserParser pgetUserLdapParser,
                         @NotNull ProfileNameParser pprofileNameParser) {
        super();
        this.changePasswordMahindraParser = parserMahindraFactory.getChangePasswordMahindraParser();
        this.generateOTPMahindraParser = parserMahindraFactory.getGenerateOTPMahindraParser();
        this.resetPasswordMahindraParser = parserMahindraFactory.getResetPasswordMahindraParser();
        this.loginServiceMahindraParser = parserMahindraFactory.getLoginServiceMahindraParser();
        this.userQueryInfoMahindraParser = parserMahindraFactory.getUserQueryInfoMahindraParser();

        this.changePasswordServiceLdapParser = pchangePasswordServiceLdapParser;
        this.resetPasswordServiceLdapParser = presetPasswordServiceLdapParser;
        this.loginServiceLdapParser = ploginServiceLdapParser;
        this.generateOTPParser = pgenerateOTPParser;
        this.getUserLdapParser = pgetUserLdapParser;
        this.profileNameParser = pprofileNameParser;
    }

    public final IParser getParserMahindra(OperationType operationType) throws ParseException {
        switch (operationType) {

            case CHANGE_PASSWORD:
                return this.changePasswordMahindraParser;
            case GENERATE_OTP:
                return this.generateOTPMahindraParser;
            case RESET_PASSWORD:
                return this.resetPasswordMahindraParser;
            case LOGIN:
                return this.loginServiceMahindraParser;
            case USER_QUERY_INFO:
                return this.userQueryInfoMahindraParser;
            default:
                throw new ParseException(ParserFactory.INVALID_OPERATION);
        }
    }

    public final IParser getParserSupportUser(OperationType operationType) throws ParseException {
        switch (operationType) {

            case GENERATE_OTP:
                return this.generateOTPParser;
            case CHANGE_PASSWORD:
                return  this.changePasswordServiceLdapParser;
            case RESET_PASSWORD:
                return  this.resetPasswordServiceLdapParser;
            case LOGIN:
                return   this.loginServiceLdapParser;
            case USER_QUERY_INFO:
                return   this.getUserLdapParser;
            default:
                throw new ParseException(INVALID_OPERATION);
        }
    }
    public final IParser getParser(ProviderType providerType, OperationType operationType) throws ParseException {
        IParser parser;

        switch (providerType) {
            case MAHINDRA:
               return getParserMahindra(operationType);
            case SUPPORT_USER:
                return getParserSupportUser(operationType);
            case SUPPORT_PROFILE:
                if (operationType.equals(OperationType.PROFILE_NAME)) {
                    parser = this.profileNameParser;
                } else {
                    throw new ParseException(INVALID_OPERATION);
                }
                break;
            default:
                throw new ParseException(INVALID_OPERATION);
        }


        return parser;
    }
}

