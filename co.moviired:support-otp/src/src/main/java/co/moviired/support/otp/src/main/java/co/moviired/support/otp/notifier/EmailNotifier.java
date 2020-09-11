package co.moviired.support.otp.notifier;

import co.moviired.base.domain.exception.ParsingException;
import co.moviired.connector.connector.ReactiveConnector;
import co.moviired.support.otp.helper.OtpHelper;
import co.moviired.support.otp.model.entity.Otp;
import co.moviired.support.otp.properties.EmailProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// Enviar el OTP por EMAIL

@Slf4j
@Service
public class EmailNotifier implements INotifier {

    private final OtpHelper otpHelper;
    private final ReactiveConnector supportEMAILClient;
    private final EmailProperties emailProperties;

    public EmailNotifier(
            @NotNull OtpHelper otpHelper,
            @NotNull ReactiveConnector supportEMAILClient,
            EmailProperties emailProperties) {
        this.otpHelper = otpHelper;
        this.supportEMAILClient = supportEMAILClient;
        this.emailProperties = emailProperties;
    }

    @Override
    public void notify(@NotNull String uuid, @NotNull Otp otp) {
        try {
            // Armar el mensaje
            MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
            formData.add("otp", otpHelper.decryptOtp(otp.getValue()));
            formData.add("email", otp.getEmail());
            // Enviar el mensaje
            String path = emailProperties.getPathMovii();
            if (otp.getOrigin().equals("CHANNEL"))
                path = emailProperties.getPathMoviired();

            ExecutorService taskThread = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
            String finalPath = path;
            taskThread.submit(() -> {
                this.supportEMAILClient.post(this.emailProperties.getUrl() + finalPath, formData, String.class, MediaType.APPLICATION_FORM_URLENCODED, new HashMap<>()).block();
            });

            log.info("[{}] EMAIL del OTP enviado correctamente, al correo: {}", uuid, otp.getPhoneNumber());

        } catch (Exception | ParsingException e) {
            log.error("[{}] Error enviando el EMAIL del OTP. Cause: {}", uuid, e.getMessage(), e);
        }
    }

}

