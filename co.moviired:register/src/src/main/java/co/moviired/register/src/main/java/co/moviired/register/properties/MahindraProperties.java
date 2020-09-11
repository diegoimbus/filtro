package co.moviired.register.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;

@Data
@ConfigurationProperties(prefix = "providers.mahindra")
public final class MahindraProperties implements Serializable {

    // Service web
    private String url;
    private int connectionTimeout;
    private String specialCharacters;
    private int readTimeout;

    // REGISTRAR USUARIO
    private String userQueryInfoType;
    private String userQueryInfoProvider;
    private String type;
    private String provider;
    private String payid;
    private String npref;
    private String language1;
    private String ispincheckreq;
    private String doc1Name;

    // Change Password
    private String newPasswordType;
    private String newPasswordLanguage1;

    // Login
    private String loginType;
    private String loginSource;
    private String loginProvider;
    private String loginLanguage1;
    private String loginOtpReq;
    private String loginIsPinCheckReq;

    // Cash In
    private String rciReqType;
    private String rciReqMsisdn;
    private String rciReqPin;
    private String rciReqSndProvider;
    private String rciReqRcvProvider;
    private String rciReqSndInstrument;
    private String rciReqRcvInstrument;
    private String rciReqLanguage1;
    private String rciReqLanguage2;
}

