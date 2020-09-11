package co.movii.auth.server.providers.supportprofile.request;
/*
 * Copyright @2019. Movii, SAS. Todos los derechos reservados.
 *
 * @author Rivas, Rodolfo
 * @version 1, 2019
 * @since 1.0
 */

import co.movii.auth.server.providers.IRequest;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ProfileNameRequest implements IRequest {

    private static final long serialVersionUID = 5241732459822225389L;

    private String name;

    public ProfileNameRequest(ProfileNameRequest pprofileNameRequest) {
        this.name = pprofileNameRequest.name;
    }
}


