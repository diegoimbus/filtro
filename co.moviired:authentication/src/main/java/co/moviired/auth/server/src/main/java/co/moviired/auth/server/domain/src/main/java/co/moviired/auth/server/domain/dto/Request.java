package co.moviired.auth.server.domain.dto;
/*
 * Copyright @2020. Moviired, SAS. Todos los derechos reservados.
 *
 * @author Rivas, Rodolfo
 * @version 1, 2019
 * @since 1.0
 */

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Request {

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

    private String idno;
    private String dob;

    public Request(String puserLogin, String ppin) {
        this.userLogin = puserLogin;
        this.pin = ppin;
    }

    public Request(Request rreequest) {
        this.value = rreequest.value;
        this.userLogin = rreequest.userLogin;
        this.pin = rreequest.pin;
        this.newpin = rreequest.newpin;
        this.confirmnewpin = rreequest.confirmnewpin;
        this.name = rreequest.name;
        this.otp = rreequest.otp;
        this.correlationId = rreequest.correlationId;
        this.imei = rreequest.imei;
        this.ip = rreequest.ip;
        this.source = rreequest.source;
        this.operatingSystem = rreequest.operatingSystem;
        this.channel = rreequest.channel;
        this.browser = rreequest.browser;
        this.version = rreequest.version;
        this.issuerDate = rreequest.issuerDate;
        this.notifyChannel = rreequest.notifyChannel;
        this.user = rreequest.user;
        this.idno = rreequest.idno;
        this.dob = rreequest.dob;
    }
}

