package co.moviired.auth.server.providers.supportuser.response;

import co.moviired.auth.server.providers.IResponse;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)

public class GenerateOTPResponse implements IResponse {

    private static final long serialVersionUID = -3023215829840709823L;
    private String errorCode;
    private String errorMessage;
    private String errorType;
    private String otp;


}

