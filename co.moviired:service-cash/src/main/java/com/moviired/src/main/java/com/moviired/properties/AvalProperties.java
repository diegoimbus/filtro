package com.moviired.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;

@Data
@ConfigurationProperties(prefix = "endpoints.aval")
public class AvalProperties implements Serializable {

    // Conexión
    private String clientPackage;
    private String namespace;
    private String url;
    private int connectionTimeout;
    private int readTimeout;

    // Operaciones
    // # Generate OTP
    private String generateOtpOperation;
    private String clientAppOrg;
    private String clientAppName;
    private String clientAppVersion;
    private String bankId;
    private String govIssueIdentType;
    private String govIssueIdentNumber;
    private String msgRqHdrChannel;
    private String msgRqHdrSessKey;
    private Boolean msgRqHdrReverse;
    private String msgRqHdrLanguage;
    private String otpInfoChannel;
    private String otpInfoOTPType;
    private String depAcctIdId;
    private String depAcctIdType;
    private String curAmtCode;

}

