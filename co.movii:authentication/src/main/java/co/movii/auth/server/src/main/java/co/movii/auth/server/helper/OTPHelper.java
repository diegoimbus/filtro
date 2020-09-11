package co.movii.auth.server.helper;

import co.movii.auth.server.domain.dto.GenerateOtpRequest;
import co.movii.auth.server.domain.dto.User;
import co.movii.auth.server.properties.SupportOTPProperties;
import co.movii.auth.server.properties.SupportSmsProperties;
import co.movii.auth.server.providers.supportotp.response.SupportOTPResponse;
import co.moviired.connector.connector.ReactiveConnector;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public final class OTPHelper implements Serializable {

    private static final String FINALIZED = "finalized";
    private final ReactiveConnector supportOTPConnector;
    private final SupportOTPProperties supportOTPProperties;
    private final SupportSmsProperties supportSmsProperties;
    private final ObjectMapper objectMapper;

    public OTPHelper(
            @Qualifier("supportOTPConnector") @NotNull ReactiveConnector psupportOTPConnector,
            @NotNull SupportOTPProperties psupportOTPProperties,
            @NotNull SupportSmsProperties psupportSmsProperties
    ) {
        super();
        this.supportOTPConnector = psupportOTPConnector;
        this.supportOTPProperties = psupportOTPProperties;
        this.supportSmsProperties = psupportSmsProperties;

        this.objectMapper = new ObjectMapper();
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public Mono<SupportOTPResponse> generateOTP(@NotNull String source, @NotNull User user) {
        // Establecer el template del SMS, según el source
        Boolean sendNotification = false;
        String notificationChannel;
        String template = this.supportSmsProperties.getOtpMoviiTemplate();
        sendNotification = this.supportOTPProperties.getSendNotificationSubscriber();
        notificationChannel = this.supportOTPProperties.getNotifySubscriber();

        // Obtener el endpoint a invocar
        String url = supportOTPProperties.getPathGenerateOTP(source, user.getCellphone(), user.getEmail(), template);
        log.info("generateOTP Request: {URL: '{}'", url);

        // Variables del SMS
        Map<String, String> variables = new HashMap<>();
        variables.put("NOMBRE", user.getFirstName());
        variables.put("EXPIRATION", supportOTPProperties.getOtpExpiration().toString());

        // Generar los params de la peticion
        GenerateOtpRequest generateOtpRequest = GenerateOtpRequest.builder()
                .email(user.getEmail())
                .otpAlphanumeric(supportOTPProperties.isOtpAlpha())
                .otpLength(supportOTPProperties.getOtpLength())
                .otpExpiration(supportOTPProperties.getOtpExpiration())
                .notifyChannel(notificationChannel)
                .sendSms(sendNotification)
                .templateCode(template)
                .variables(variables)
                .build();

        // Invocar al servicio
        return supportOTPConnector.post(url, generateOtpRequest, String.class, MediaType.APPLICATION_JSON, null
        ).flatMap(response -> this.printResponse((String) response)
                .onErrorResume(throwable -> {
                    log.error("Error resend OTP. cause: {}", throwable.getMessage());
                    return Mono.error(throwable);
                }).doAfterTerminate(() -> log.info(OTPHelper.FINALIZED)));
    }

    public Mono<SupportOTPResponse> isValid(String source, String phoneNumber, String otp) {
        log.info("validateOTP Request: {URL: '{}', source: '{}', phone_number: '{}'}", supportOTPProperties.getUrl(), source, phoneNumber);
        return supportOTPConnector.post(supportOTPProperties.getPathValidateOTP(source, phoneNumber, otp),
                "", String.class, MediaType.APPLICATION_JSON, null
        ).flatMap(response -> {
            log.info("Response: {}", response);
            try {
                SupportOTPResponse res = this.objectMapper.readValue((String) response, SupportOTPResponse.class);
                return Mono.just(res);
            } catch (Exception e) {
                return Mono.error(e);
            }
        }).onErrorResume(throwable -> {
            log.error("Error validate OTP. cause: {}", throwable.getMessage());
            return Mono.error(throwable);
        }).doAfterTerminate(() -> log.info(OTPHelper.FINALIZED));
    }

    public Mono<SupportOTPResponse> resendOTP(String source, String phoneNumber, String pnotifyChannel) {
        log.info("resendOTP Request: {URL: '{}', source: '{}', phone_number: '{}', notify_channel: '{}'}", supportOTPProperties.getUrl(), source, phoneNumber, pnotifyChannel);
        String notifyChannel = pnotifyChannel;
        if (notifyChannel == null) {
            notifyChannel = supportOTPProperties.getNotifyChannel();
        }

        return supportOTPConnector.exchange(HttpMethod.PUT, supportOTPProperties.getPathResendOTP(source, phoneNumber, notifyChannel),
                "", String.class, MediaType.APPLICATION_JSON, null
        ).flatMap(response -> this.printResponse((String) response))
                .onErrorResume(throwable -> {
                    log.error("Error resend OTP. cause: {}", throwable.getMessage());
                    return Mono.error(throwable);
                }).doAfterTerminate(() -> log.info(OTPHelper.FINALIZED));
    }

    private Mono<SupportOTPResponse> printResponse(String response) {
        log.info("Response: {}", response);
        try {
            SupportOTPResponse res = this.objectMapper.readValue(response, SupportOTPResponse.class);
            return Mono.just(res);
        } catch (Exception e) {
            return Mono.error(e);
        }
    }

}

