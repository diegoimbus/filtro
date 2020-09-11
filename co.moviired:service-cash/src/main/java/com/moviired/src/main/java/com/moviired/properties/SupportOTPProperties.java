package com.moviired.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;

@Data
@ConfigurationProperties(prefix = "endpoints.support-otp")
public class SupportOTPProperties implements Serializable {

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
    private String pathExpireForceOTP;
    private String pathFindOTP;

    // OTHERS
    private Integer otpLength;
    private Integer otpExpiration;
    private boolean otpAlpha;
    private String key;
    private String initVector;


    /**
     * metodo getPathGenerateOTP (generador de ruta para invocar a support-otp (generate))
     *
     * @param source,phoneNumber,email,template
     * @return String
     */
    public String getPathGenerateOTP(String source, String phoneNumber, String email, String template) {
        return pathGenerateOTP
                .replace(ORIGIN, source)
                .replace(PHONE_NUMBER, phoneNumber)
                .replace(TEMPLATE, template)
                .replace(EMAIL, email);
    }


    /**
     * metodo getPathValidateOTP (generador de ruta para invocar a support-otp (validate))
     *
     * @param source,phoneNumber,email,template
     * @return String
     */
    public String getPathValidateOTP(String source, String phoneNumber, String otp) {
        return pathValidateOTP
                .replace(ORIGIN, source)
                .replace(PHONE_NUMBER, phoneNumber)
                .replace(OTP, otp);
    }


    /**
     * metodo getPathResendOTP (generador de ruta para invocar a support-otp (resend))
     *
     * @param source,phoneNumber,email,template
     * @return String
     */
    public String getPathResendOTP(String source, String phoneNumber, String notifyChannel) {
        return pathResendOTP
                .replace(ORIGIN, source)
                .replace(PHONE_NUMBER, phoneNumber)
                .replace(NOTIFY_CHANNEL, notifyChannel);
    }

}

