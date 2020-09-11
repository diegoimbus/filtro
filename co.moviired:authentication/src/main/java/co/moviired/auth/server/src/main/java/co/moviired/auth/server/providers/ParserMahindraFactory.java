package co.moviired.auth.server.providers;


import co.moviired.auth.server.providers.mahindra.parser.*;
import lombok.Getter;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Getter
@Component
public class ParserMahindraFactory implements Serializable {

    private static final long serialVersionUID = -8111145829471817085L;

    private static final String INVALID_OPERATION = "Operación inválida";

    private final ChangePasswordMahindraParser changePasswordMahindraParser;
    private final ResetPasswordMahindraParser resetPasswordMahindraParser;
    private final LoginMahindraParser loginServiceMahindraParser;
    private final UserQueryInfoMahindraParser userQueryInfoMahindraParser;
    private final GenerateOTPMahindraParser generateOTPMahindraParser;

    public ParserMahindraFactory(@NotNull ChangePasswordMahindraParser pchangePasswordMahindraParser,
                                 @NotNull GenerateOTPMahindraParser pgenerateOTPMahindraParser,
                                 @NotNull ResetPasswordMahindraParser presetPasswordMahindraParser,
                                 @NotNull LoginMahindraParser ploginServiceMahindraParser,
                                 @NotNull UserQueryInfoMahindraParser puserQueryInfoMahindraParser) {
        super();
        this.changePasswordMahindraParser = pchangePasswordMahindraParser;
        this.generateOTPMahindraParser = pgenerateOTPMahindraParser;
        this.resetPasswordMahindraParser = presetPasswordMahindraParser;
        this.loginServiceMahindraParser = ploginServiceMahindraParser;
        this.userQueryInfoMahindraParser = puserQueryInfoMahindraParser;

    }
}

