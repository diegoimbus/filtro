package co.moviired.support.provider.otp.request;
/*
 * Copyright @2019. Movii, SAS. Todos los derechos reservados.
 *
 * @author Rivas, Rodolfo
 * @version 1, 2019
 * @since 1.0
 */

import co.moviired.support.domain.dto.enums.NotifyChannel;
import co.moviired.support.provider.IRequest;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class GenerateOTPRequest implements IRequest {


    private Integer otpExpiration;
    private Integer otpLength;
    private Boolean otpAlphanumeric;
    private String email;
    private String templateCode;
    private Boolean sendSms;
    private Map<String, String> variables;
    private NotifyChannel notifyChannel;

}


