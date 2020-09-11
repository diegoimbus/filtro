package co.moviired.support.otp.notifier;

import co.moviired.base.domain.exception.ParsingException;
import co.moviired.connector.connector.ReactiveConnector;
import co.moviired.support.otp.helper.OtpHelper;
import co.moviired.support.otp.model.dto.SmsData;
import co.moviired.support.otp.model.dto.SmsRequest;
import co.moviired.support.otp.model.dto.SmsResponse;
import co.moviired.support.otp.model.entity.Otp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// Enviar el OTP por SMS

@Slf4j
@Service
public class SmsNotifier implements INotifier {

    private final OtpHelper otpHelper;
    private final ReactiveConnector supportSMSClient;

    public SmsNotifier(
            @NotNull OtpHelper otpHelper,
            @NotNull ReactiveConnector supportSMSClient
    ) {
        this.otpHelper = otpHelper;
        this.supportSMSClient = supportSMSClient;
    }

    @Override
    public void notify(@NotNull String uuid, @NotNull Otp otp) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

            // Variables para el mensaje
            Map<String, String> variables = otp.getVariables();
            if (variables == null) {
                variables = new HashMap<>();
            }
            variables.put("OTP", otpHelper.decryptOtp(otp.getValue()));
            variables.put("PHONE_NUMBER", otp.getPhoneNumber());
            variables.put("EXPIRATION", (otp.getExpirationLapse() != null) ? otp.getExpirationLapse().toString() : "");
            variables.put("EXPIRATION_DATE", sdf.format(otp.getExpirationDate()));

            // Armar el mensaje
            SmsData data = SmsData.builder()
                    .phoneNumber(otp.getPhoneNumber())
                    .templateCode(otp.getTemplateCode())
                    .operatorId("1")
                    .variables(variables)
                    .build();
            SmsRequest sms = new SmsRequest(data);

            // Enviar el mensaje
            ExecutorService taskThread = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
            taskThread.submit(() -> {
                this.supportSMSClient.post(sms, SmsResponse.class, MediaType.APPLICATION_JSON, new HashMap<>())
                        .doAfterTerminate(() ->
                                log.info("[{}] SMS del OTP enviado correctamente, al teléfono: {}", uuid, otp.getPhoneNumber())
                        ).block();
            });


        } catch (Exception | ParsingException e) {
            log.error("[{}] Error enviando el SMS del OTP. Cause: {}", uuid, e.getMessage(), e);
        }
    }

}

