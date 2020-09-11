package co.moviired.auth.server.domain.dto;

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
public class GenerateOtpRequest implements Serializable {

    private Integer otpExpiration;
    private Integer otpLength;
    private Boolean otpAlphanumeric;
    private Boolean sendSms;
    private String email;
    private String templateCode;
    private Map<String, String> variables;
    private String notifyChannel;

}

