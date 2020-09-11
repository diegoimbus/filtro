package co.movii.auth.server.providers;


import co.movii.auth.server.domain.enums.OperationType;
import co.movii.auth.server.domain.enums.ProviderType;
import co.movii.auth.server.exception.ParseException;
import co.movii.auth.server.providers.mahindra.parser.*;
import co.movii.auth.server.providers.supportprofile.parser.ProfileNameParser;
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

    private final ProfileNameParser profileNameParser;

    public ParserFactory(@NotNull ChangePasswordMahindraParser pchangePasswordMahindraParser,
                         @NotNull GenerateOTPMahindraParser pgenerateOTPMahindraParser,
                         @NotNull ResetPasswordMahindraParser presetPasswordMahindraParser,
                         @NotNull LoginMahindraParser ploginServiceMahindraParser,
                         @NotNull UserQueryInfoMahindraParser puserQueryInfoMahindraParser,
                         @NotNull ProfileNameParser pprofileNameParser) {
        super();
        this.changePasswordMahindraParser = pchangePasswordMahindraParser;
        this.generateOTPMahindraParser = pgenerateOTPMahindraParser;
        this.resetPasswordMahindraParser = presetPasswordMahindraParser;
        this.loginServiceMahindraParser = ploginServiceMahindraParser;
        this.userQueryInfoMahindraParser = puserQueryInfoMahindraParser;

        this.profileNameParser = pprofileNameParser;
    }

    public final IParser getParser(ProviderType providerType, OperationType operationType) throws ParseException {
        IParser parser;

        switch (providerType) {
            case MAHINDRA:
                switch (operationType) {

                    case CHANGE_PASSWORD:
                        parser = this.changePasswordMahindraParser;
                        break;

                    case GENERATE_OTP:
                        parser = this.generateOTPMahindraParser;
                        break;

                    case RESET_PASSWORD:
                        parser = this.resetPasswordMahindraParser;
                        break;

                    case LOGIN:
                        parser = this.loginServiceMahindraParser;
                        break;
                    case USER_QUERY_INFO:
                        parser = this.userQueryInfoMahindraParser;
                        break;

                    default:
                        throw new ParseException(ParserFactory.INVALID_OPERATION);
                }
                break;
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

