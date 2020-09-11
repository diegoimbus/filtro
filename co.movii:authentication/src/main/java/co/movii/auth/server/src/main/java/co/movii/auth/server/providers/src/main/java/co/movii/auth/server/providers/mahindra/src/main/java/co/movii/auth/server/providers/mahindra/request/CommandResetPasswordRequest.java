package co.movii.auth.server.providers.mahindra.request;
/*
 * Copyright @2019. Movii, SAS. Todos los derechos reservados.
 *
 * @author Bejarano, Cindy
 * @version 1, 2019
 * @since 1.0
 */


import co.movii.auth.server.providers.IRequest;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({
        "type",
        "msisdn",
        "provider",
        "usertype",
        "pintype",
        "newpin",
        "confirmpin",
        "otp",
        "language1"
})

@JsonRootName("command")
public class CommandResetPasswordRequest implements IRequest {

    private static final long serialVersionUID = 5241732459822225389L;

    private String type;

    private String msisdn;

    private String provider;

    private String usertype;

    private String pintype;

    private String newpin;

    private String confirmpin;

    private String otp;

    private String language1;

    public CommandResetPasswordRequest(CommandResetPasswordRequest pcommandResetPasswordRequest) {
        this.type = pcommandResetPasswordRequest.type;
        this.msisdn = pcommandResetPasswordRequest.msisdn;
        this.provider = pcommandResetPasswordRequest.provider;
        this.usertype = pcommandResetPasswordRequest.usertype;
        this.pintype = pcommandResetPasswordRequest.pintype;
        this.newpin = pcommandResetPasswordRequest.newpin;
        this.confirmpin = pcommandResetPasswordRequest.confirmpin;
        this.otp = pcommandResetPasswordRequest.otp;
        this.language1 = pcommandResetPasswordRequest.language1;
    }
}



