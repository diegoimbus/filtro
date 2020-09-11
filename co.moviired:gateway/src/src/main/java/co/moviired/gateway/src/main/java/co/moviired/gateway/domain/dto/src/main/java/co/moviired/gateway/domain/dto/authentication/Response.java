package co.moviired.gateway.domain.dto.authentication;
/*
 * Copyright @2019. Movii, SAS. Todos los derechos reservados.
 *
 * @author Rivas, Rodolfo
 * @version 1, 2019
 * @since 1.0
 */

import co.moviired.gateway.domain.dto.Profile;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Response implements Serializable {
    @JsonProperty(value = "access_token")
    private String accessToken;

    @JsonProperty(value = "token_type")
    private String tokenType;

    @JsonProperty(value = "refresh_token")
    private String refreshToken;

    @JsonProperty(value = "expires_in")
    private String expiresIn;

    private String scope;
    private String role;
    private String credentials;
    private String errorType;
    private String errorCode;
    private String errorMessage;
    private String nacimiento;

    private Profile profile;
    private User user;
    private String value;

}


