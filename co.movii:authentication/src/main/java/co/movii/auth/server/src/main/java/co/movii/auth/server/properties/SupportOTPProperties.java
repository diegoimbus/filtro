package co.movii.auth.server.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;

@Data
@ConfigurationProperties(prefix = "providers.support-otp")
public final class SupportOTPProperties implements Serializable {

    private static final long serialVersionUID = -4351094487498311258L;
    private static final String ORIGIN = "##ORIGIN##";
    private static final String PHONE_NUMBER = "##PHONE_NUMBER##";
    private static final String EMAIL = "##EMAIL##";
    private static final String OTP = "##OTP##";
    private static final String TEMPLATE = "##TEMPLATE##";
    private static final String NOTIFY_CHANNEL = "##NOTIFY_CHANNEL##";

    // SERVICE WEB
    private String url;
    private int connectionTimeout;
    private int readTimeout;
    private long numRetries;

    // PATHS
    private String pathGenerateOTP;
    private String pathValidateOTP;
    private String pathResendOTP;

    // OTHERS
    private Integer otpLength;
    private Integer otpExpiration;
    private boolean otpAlpha;
    private Boolean sendNotificationChannel;
    private Boolean sendNotificationSubscriber;
    private String notifyChannel;
    private String notifySubscriber;

    // UTILS
    public String getPathGenerateOTP(String source, String phoneNumber, String email, String template) {
        return pathGenerateOTP
                .replace(ORIGIN, source)
                .replace(PHONE_NUMBER, phoneNumber)
                .replace(TEMPLATE, template)
                .replace(EMAIL, email);
    }

    public String getPathValidateOTP(String source, String phoneNumber, String otp) {
        return pathValidateOTP
                .replace(ORIGIN, source)
                .replace(PHONE_NUMBER, phoneNumber)
                .replace(OTP, otp);
    }

    public String getPathResendOTP(String source, String phoneNumber, String pnotifyChannel) {
        return pathResendOTP
                .replace(ORIGIN, source)
                .replace(PHONE_NUMBER, phoneNumber)
                .replace(NOTIFY_CHANNEL, pnotifyChannel);
    }
}

