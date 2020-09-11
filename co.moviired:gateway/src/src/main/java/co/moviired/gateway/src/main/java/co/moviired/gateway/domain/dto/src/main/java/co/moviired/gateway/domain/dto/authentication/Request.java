package co.moviired.gateway.domain.dto.authentication;
/*
 * Copyright @2019. Movii, SAS. Todos los derechos reservados.
 *
 * @author Rivas, Rodolfo
 * @version 1, 2019
 * @since 1.0
 */

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.io.Serializable;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Request implements Serializable {

    private String value;
    private String userLogin;
    private String pin;

    private String newpin;
    private String confirmnewpin;
    private String name;
    private String otp;
    private String correlationId;

    private String imei;
    private String ip;

    private String source;
    private String operatingSystem;
    private String channel;
    private String browser;
    private String version;
    private String issuerDate;
    private String notifyChannel;

    private User user;

    public Request(String puserLogin, String ppin) {
        this.userLogin = puserLogin;
        this.pin = ppin;
    }


}

