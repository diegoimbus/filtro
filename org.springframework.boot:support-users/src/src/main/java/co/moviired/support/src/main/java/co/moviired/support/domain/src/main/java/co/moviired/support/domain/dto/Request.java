package co.moviired.support.domain.dto;
/*
 * Copyright @2019. Movii, SAS. Todos los derechos reservados.
 *
 * @author Bejarano, Cindy
 * @version 1, 2019
 * @since 1.0
 */


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

@Slf4j
@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Request implements Serializable {
    private String msisdn;
    private String mpin;
    private String newmpin;

    private UserDto user;
    private String otp;

    private String correlationId;
    private String ip;
    private String source;
    private String imei;
    private String origin;


    public Request(Request prequest) {
        this.msisdn = prequest.msisdn;
        this.mpin = prequest.mpin;
        this.newmpin = prequest.newmpin;
        this.user = null;
        if(prequest.getUser() != null) {
            this.user = new UserDto(prequest.getUser());
        }
        this.otp = prequest.otp;

        this.correlationId = prequest.correlationId;
        this.ip = prequest.ip;
        this.source = prequest.source;
        this.imei = prequest.imei;
        this.origin = prequest.origin;
    }
}

