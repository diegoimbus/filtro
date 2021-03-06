package co.moviired.auth.server.providers.supportuser.request;
/*
 * Copyright @2020. Moviired, SAS. Todos los derechos reservados.
 *
 * @author Rivas, Rodolfo
 * @version 1, 2019
 * @since 1.0
 */

import co.moviired.auth.server.providers.IRequest;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ChangePasswordRequest implements IRequest {

    private static final long serialVersionUID = 5241732459822225389L;

    private String msisdn;
    private String mpin;
    private String newmpin;
    private String confirmmpin;

    public ChangePasswordRequest(ChangePasswordRequest pchangePasswordRequest) {
        this.msisdn = pchangePasswordRequest.msisdn;
        this.mpin = pchangePasswordRequest.mpin;
        this.newmpin = pchangePasswordRequest.newmpin;
        this.confirmmpin = pchangePasswordRequest.confirmmpin;
    }
}


