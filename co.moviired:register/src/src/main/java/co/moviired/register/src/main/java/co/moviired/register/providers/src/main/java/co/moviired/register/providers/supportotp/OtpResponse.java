package co.moviired.register.providers.supportotp;

import co.moviired.register.providers.IResponse;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class OtpResponse implements IResponse {

    private String responseCode;
    private String responseMessage;
    private String otp;
    private Integer idOtp;
    private boolean valid;

}

