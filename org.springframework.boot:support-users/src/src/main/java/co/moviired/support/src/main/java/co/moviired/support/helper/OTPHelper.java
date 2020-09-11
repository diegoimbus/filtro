package co.moviired.support.helper;

import co.moviired.connector.connector.ReactiveConnector;
import co.moviired.support.conf.SupportOtpProperties;
import co.moviired.support.domain.dto.enums.NotifyChannel;
import co.moviired.support.domain.otp.SupportOTPResponse;
import co.moviired.support.provider.otp.request.GenerateOTPRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.io.Serializable;
import java.util.LinkedHashMap;

@Slf4j
@Component
public final class OTPHelper implements Serializable {

    private final ReactiveConnector supportOtpConnector;
    private static final  int OTP_EXPIRE = 30;

    private final SupportOtpProperties supportOTPProperties;
    private final ObjectMapper objectMapper;

    public OTPHelper(ReactiveConnector psupportOtpConnector, SupportOtpProperties psupportOTPProperties) {
        this.supportOtpConnector = psupportOtpConnector;
        this.supportOTPProperties = psupportOTPProperties;

        this.objectMapper = new ObjectMapper();
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public Mono<SupportOTPResponse> generateOTP(String phoneNumber, String email, String name, String origin) {
        String url = supportOTPProperties.getPathGenerateOTP(phoneNumber, origin);
        log.info("generateOTP Request: {URL: '{}', phone_number: '{}'}", url, phoneNumber);
        GenerateOTPRequest request = new GenerateOTPRequest();
        request.setEmail(email);
        request.setOtpAlphanumeric(false);
        request.setOtpLength(supportOTPProperties.getOtpLength());
        request.setOtpExpiration(OTPHelper.OTP_EXPIRE);
        request.setTemplateCode("0001");
        request.setSendSms(supportOTPProperties.getSendNotify());
        request.setNotifyChannel(NotifyChannel.valueOf(supportOTPProperties.getNotifyChannel()));
        LinkedHashMap<String, String> variables = new LinkedHashMap<>();
        variables.put("NOMBRE", name);
        request.setVariables(variables);
        return supportOtpConnector.post(
                url,
                request,
                String.class,
                MediaType.APPLICATION_JSON,
                null
        ).flatMap(response -> {
            log.info("response : " + response);
            try {
                return Mono.just(this.objectMapper.readValue((String) response, SupportOTPResponse.class));
            } catch (Exception e) {
                log.error("Error generando OTP {}", e.getMessage());
                return Mono.error(e);
            }
        }).onErrorResume(throwable -> {
            log.error("Error validate OTP. cause: {}", throwable.getMessage());
            return Mono.error(throwable);
        }).doAfterTerminate(() -> log.info("finalized"));
    }

    public Mono<SupportOTPResponse> isValid(String phoneNumber, String otp, String source) {
        String url = supportOTPProperties.getPathValidateOTP(phoneNumber, otp, source);
        log.info("validateOTP Request: {URL: '{}', phone_number: '{}'}", url, phoneNumber);
        return supportOtpConnector.post(
                url,
                "",
                String.class,
                MediaType.APPLICATION_JSON,
                null
        ).flatMap(response -> {
            log.info("response : " + response);
            try {
                return Mono.just(this.objectMapper.readValue((String) response, SupportOTPResponse.class));
            } catch (Exception e) {
                return Mono.error(e);
            }
        }).onErrorResume(throwable -> {
            log.error("Error validate OTP. cause: {}", throwable.getMessage());
            return Mono.error(throwable);
        }).doAfterTerminate(() -> log.info("finalized"));
    }

}

