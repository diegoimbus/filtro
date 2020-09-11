package co.moviired.auth.server.providers.supportotp.response;

import co.moviired.auth.server.providers.IResponse;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class SupportOTPResponse implements IResponse {

    private String responseCode;
    private String responseMessage;
    private String otp;
    private boolean valid;

}

