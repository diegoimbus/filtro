package co.moviired.support.otp.notifier;

import co.moviired.base.domain.exception.ParsingException;
import co.moviired.base.util.Security;
import co.moviired.connector.connector.ReactiveConnector;
import co.moviired.support.otp.helper.OtpHelper;
import co.moviired.support.otp.model.entity.Otp;
import co.moviired.support.otp.properties.GuarumoProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// Enviar el OTP por LLAMADA

@Slf4j
@Service
public class CallNotifier implements INotifier {

    private static final String USER_PATTERN = "ABCDEFGHIJ";
    private static final String OTP_PATTERN = "KLMNOPQRS";

    private final OtpHelper otpHelper;
    private final GuarumoProperties guarumoProperties;
    private final ReactiveConnector guarumoClient;

    public CallNotifier(
            @NotNull OtpHelper otpHelper,
            @NotNull GuarumoProperties guarumoProperties,
            @NotNull ReactiveConnector guarumoClient
    ) {
        super();
        this.otpHelper = otpHelper;
        this.guarumoProperties = guarumoProperties;
        this.guarumoClient = guarumoClient;
    }

    @Override
    public void notify(@NotNull String uuid, @NotNull Otp otp) {
        try {
            // Armar el mensaje
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("destination", otp.getPhoneNumber());
            params.add("user", this.guarumoProperties.getUser());
            params.add("password", this.guarumoProperties.getPassword());
            params.add("message", this.guarumoProperties.getMessage());
            params.add("voice", this.guarumoProperties.getVoice());
            params.add("timesToRetry", this.guarumoProperties.getTimesToRetry());
            params.add("retryAfter", this.guarumoProperties.getRetryAfter());

            // Colocar el valor del OTP separado por spacios en blanco
            String otpValue = this.otpHelper.decryptOtp(otp.getValue());
            StringBuilder otpV = new StringBuilder();
            for (char car : otpValue.toCharArray()) {
                otpV.append(car);
                otpV.append(" ");
            }
            otpV.deleteCharAt(otpV.length() - 1);
            otpV.trimToSize();
            params.add("userParams", this.guarumoProperties.getUserParams().replace(USER_PATTERN, otp.getPhoneNumber()).replace(OTP_PATTERN, otpV.toString()));

            // Realizar la llamada
            ExecutorService taskThread = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
            taskThread.submit(() -> {
                try {
                    log.info("[{}] Guarumo request: POST {}, PARAMS: {}", uuid, guarumoProperties.getUri(), Security.printIgnore(new ObjectMapper().writeValueAsString(params), "user", "password", "code"));
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
                this.guarumoClient.post(params, String.class, MediaType.APPLICATION_FORM_URLENCODED, null)
                        .flatMap(resp -> {
                            try {
                                log.info("[{}] Guarumo response: {}", uuid, new ObjectMapper().writeValueAsString(resp));
                            } catch (JsonProcessingException e) {
                                log.error(e.getMessage(), e);
                            }
                            return Mono.empty();
                        })
                        .doAfterTerminate(() ->
                                log.info("[{}] CALL del OTP realizada correctamente, al teléfono: {}", uuid, otp.getPhoneNumber())
                        ).block();
            });


        } catch (Exception | ParsingException e) {
            log.error("[{}] Error enviando el SMS del OTP. Cause: {}", uuid, e.getMessage(), e);
        }

    }

}

