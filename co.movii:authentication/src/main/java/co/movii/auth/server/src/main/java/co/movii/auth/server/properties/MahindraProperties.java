package co.movii.auth.server.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;

@Data
@ConfigurationProperties(prefix = "providers.mahindra")
public class MahindraProperties implements Serializable {

    private static final long serialVersionUID = -4351094487498311258L;

    // SERVICE WEB
    private String url;
    private int connectionTimeout;
    private int readTimeout;

    // USER QUERY INFO
    private String userQueryInfoType;
    private String userQueryInfoProvider;

    // CHANGE PASSWORD
    private String changePasswordChannelType;
    private String changePasswordSubscriberType;
    private String changePasswordLanguage1;

    // GENERATE OTP
    private String generateOTPType;
    private String generateOTPProvider;
    private String generateOTPLanguageEMAIL;
    private String generateOTPLanguageSMS;
    private String generateOTPSrvReqType;

    // AUTENTICACIONES
    private String nameAuthpinreq;
    private String otpReq;
    private String isPinCheckReq;
    private String source;
    private String providerAuth;

    // RESET PASSWORD
    private String resetPasswordType;
    private String resetPasswordProvider;
    private String resetPasswordLanguage1;
    private String resetPasswordPinType;

}

