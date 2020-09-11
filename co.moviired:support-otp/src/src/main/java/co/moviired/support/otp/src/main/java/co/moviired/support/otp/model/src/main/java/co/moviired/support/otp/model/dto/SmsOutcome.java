package co.moviired.support.otp.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SmsOutcome implements Serializable {

    private String statusCode;

    private String message;

    private Error error;

}

