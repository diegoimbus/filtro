package co.moviired.register.helper;

import co.moviired.connector.connector.ReactiveConnector;
import co.moviired.register.domain.model.entity.UserMoviired;
import co.moviired.register.properties.OtpProperties;
import co.moviired.register.properties.SmsProperties;
import co.moviired.register.providers.supportotp.OtpRequest;
import co.moviired.register.providers.supportotp.OtpResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

    private final ReactiveConnector supportOtpClient;
    private final OtpProperties otpProperties;
    private final SmsProperties smsProperties;
    private final ObjectMapper objectMapper;

    @Value("${providers.movii}")
    private String movii;

    @Value("${providers.moviired}")
    private String moviired;

    public OTPHelper(
            @NotNull ReactiveConnector pSupportOtpClient,
            @NotNull OtpProperties pOtpProperties,
            @NotNull SmsProperties pSmsProperties
    ) {
        super();
        this.supportOtpClient = pSupportOtpClient;
        this.otpProperties = pOtpProperties;
        this.smsProperties = pSmsProperties;

        this.objectMapper = new ObjectMapper();
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public Mono<OtpResponse> generateOTP(@NotNull String source, @NotNull UserMoviired user) {
        // Establecer el template del SMS, según el source
        String template = this.smsProperties.getOtpMoviiTemplate();
        if (moviired.equalsIgnoreCase(source)) {
            template = smsProperties.getOtpMoviiredTemplate();
        }

        String email = smsProperties.getDefaultEmail();
        // Obtener el endpoint a invocar
        String url = otpProperties.getPathGenerateOTP(source, user.getPhoneNumber(), email, template);
        log.info("generateOTP Request: {URL: '{}'", this.otpProperties.getUrl().concat(url));

        // Variables del SMS
        Map<String, String> variables = new HashMap<>();
        variables.put("EXPIRATION", otpProperties.getOtpExpiration().toString());

        // Generar los params de la peticion
        OtpRequest generateOtpRequest = OtpRequest.builder()
                .email(email)
                .otpAlphanumeric(otpProperties.isOtpAlpha())
                .sendSms(true)
                .otpLength(otpProperties.getOtpLength())
                .otpExpiration(otpProperties.getOtpExpiration())
                .templateCode(template)
                .variables(variables)
                .build();

        // Invocar al servicio
        return supportOtpClient.post(url, generateOtpRequest, String.class, MediaType.APPLICATION_JSON, null
        ).flatMap(response -> {
            try {
                return Mono.just(this.objectMapper.readValue((String) response, OtpResponse.class));
            } catch (Exception e) {
                log.error("Error generando OTP {}", e.getMessage());
                return Mono.error(e);
            }
        });
    }

    public Mono<OtpResponse> isValid(String source, String phoneNumber, String otp) {
        log.info("validateOTP Request: {URL: '{}', source: '{}', phone_number: '{}'}", otpProperties.getUrl(), source, phoneNumber);
        return supportOtpClient.post(otpProperties.getPathValidateOTP(source, phoneNumber, otp),
                "", String.class, MediaType.APPLICATION_JSON, null
        ).flatMap(resp -> {
            log.info("Response: {}", resp);
            try {
                OtpResponse otpResponse = this.objectMapper.readValue((String) resp, OtpResponse.class);
                return Mono.just(otpResponse);
            } catch (Exception exc) {
                return Mono.error(exc);
            }
        }).onErrorResume(th -> {
            log.error("Error validate OTP. cause: {}", th.getMessage());
            return Mono.error(th);
        }).doAfterTerminate(() -> log.info("finalized"));
    }

    public Mono<OtpResponse> resendOTP(String source, String phoneNumber, String notifyChannel) {
        log.info("resendOTP Request: {URL: '{}', source: '{}', phone_number: '{}', notify_channel: '{}'}", otpProperties.getUrl(), source, phoneNumber, notifyChannel);
        return supportOtpClient.exchange(HttpMethod.PUT, otpProperties.getPathResendOTP(source, phoneNumber, notifyChannel),
                "", String.class, MediaType.APPLICATION_JSON, null
        ).flatMap(response -> {
            log.info("Response: {}", response);
            try {
                OtpResponse res = this.objectMapper.readValue((String) response, OtpResponse.class);
                return Mono.just(res);
            } catch (Exception e) {
                return Mono.error(e);
            }
        }).onErrorResume(throwable -> {
            log.error("Error resend OTP. cause: {}", throwable.getMessage());
            return Mono.error(throwable);
        }).doAfterTerminate(() -> log.info("finalized"));
    }

}

