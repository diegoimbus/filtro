package co.movii.auth.server.providers.mahindra.request;
/*
 * Copyright @2019. Movii, SAS. Todos los derechos reservados.
 *
 * @author Rivas, Rodolfo
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
        "mpin",
        "newmpin",
        "confirmmpin",
        "language1"
})

@JsonRootName("command")
public class CommandChangePasswordRequest implements IRequest {

    private static final long serialVersionUID = 5241732459822225389L;

    private String type;

    private String msisdn;

    private String mpin;

    private String newmpin;

    private String confirmmpin;

    private String language1;

    public CommandChangePasswordRequest(CommandChangePasswordRequest pcommandChangePasswordRequest) {
        this.type = pcommandChangePasswordRequest.type;
        this.msisdn = pcommandChangePasswordRequest.msisdn;
        this.mpin = pcommandChangePasswordRequest.mpin;
        this.newmpin = pcommandChangePasswordRequest.newmpin;
        this.confirmmpin = pcommandChangePasswordRequest.confirmmpin;
        this.language1 = pcommandChangePasswordRequest.language1;
    }
}


