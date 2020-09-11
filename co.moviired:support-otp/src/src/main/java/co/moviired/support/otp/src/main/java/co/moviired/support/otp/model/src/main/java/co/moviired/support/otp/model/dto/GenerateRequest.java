package co.moviired.support.otp.model.dto;

import co.moviired.support.otp.model.enums.NotifyChannel;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class GenerateRequest implements Serializable {

    private Integer otpExpiration;
    private Integer otpLength;
    private Boolean otpAlphanumeric;
    private String email;
    private String templateCode;
    private Boolean sendSms;
    private Map<String, String> variables;
    private NotifyChannel notifyChannel;

}

