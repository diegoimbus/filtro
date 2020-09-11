package co.moviired.support.conf;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;

@Data
@ConfigurationProperties(prefix = "properties.support-otp")
public class SupportOtpProperties implements Serializable {

    private static final long serialVersionUID = -4351094487498311258L;

    // SERVICE WEB
    private String url;
    private int connectionTimeout;
    private int readTimeout;
    private long numRetries;
    private int otpLength;
    private Boolean sendNotify;
    private String notifyChannel;

    // PATHS
    private String pathGenerateOTP;
    private String pathValidateOTP;

    // UTILS
    public String getPathGenerateOTP(String phoneNumber,String source) {
        return url.concat(pathGenerateOTP)
                .replace("##COMPONENT##", source)
                .replace("##PHONE_NUMBER##", phoneNumber);
    }

    public String getPathValidateOTP(String phoneNumber, String otp,String source) {
        return url.concat(pathValidateOTP)
                .replace("##COMPONENT##", source)
                .replace("##PHONE_NUMBER##", phoneNumber)
                .replace("##OTP##", otp);
    }
}

