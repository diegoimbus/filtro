package co.moviired.moneytransfer.client.supportathentication;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;


@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class SupportAuthenticationResponse {

    //ERROR
    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("token_type")
    private String tokenType;

    @JsonProperty("refresh_token")
    private String refreshToken;

    @JsonProperty("expires_in")
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

