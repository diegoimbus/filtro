package co.moviired.support.otp.model.dto;

import co.moviired.support.otp.model.enums.NotifyChannel;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.io.Serializable;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Request implements Serializable {

    private String component;
    private String origin;
    private String phoneNumber;
    private String email;
    private String otp;
    private Integer otpExpiration;
    private Integer otpLength;
    private Boolean otpAlphanumeric;
    private String templateCode;

    @Builder.Default
    private boolean sendSms = Boolean.TRUE;

    private Map<String, String> variables;

    @Enumerated(EnumType.STRING)
    private NotifyChannel notifyChannel;

}

