package co.moviired.support.domain.otp;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class SupportOTPResponse implements Serializable {

    private String responseCode;
    private String responseMessage;
    private String otp;
    private String idOtp;
    private boolean valid;

}

